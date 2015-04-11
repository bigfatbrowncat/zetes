package zetes.wings.litehtml.swt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import zetes.wings.litehtml.jni.BackgroundPaint;
import zetes.wings.litehtml.jni.Document;
import zetes.wings.litehtml.jni.FontMetrics;
import zetes.wings.litehtml.jni.Position;
import zetes.wings.litehtml.jni.Size;
import zetes.wings.litehtml.jni.WebColor;

public class LiteHTMLView extends Composite {

	private GC gc;
	
	private String basePath; 
	private LiteHTMLViewListener listener;

	public class Container extends zetes.wings.litehtml.jni.DocumentContainer {
		private long latestFontId = -1;
		private HashMap<Long, Font> loadedFonts = new HashMap<Long, Font>();
		private HashMap<Integer, Color> loadedColors = new HashMap<>();
		private HashMap<String, ImageData> loadedImages = new HashMap<>();
		
		private ImageData getImage(String name) {
			if (loadedImages.containsKey(name)) {
				return loadedImages.get(name);
			} else {
				try {
					FileInputStream imageFIS = new FileInputStream(new File(basePath + File.separator + name));
					ImageData img = new ImageData(imageFIS);
					
					loadedImages.put(name, img);
					return img;
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}
		
		@Override
		protected long createFont(String faceName, int size, int weight, boolean italic) {

			int style = SWT.NORMAL;
			if (weight >= 550) {
				style |= SWT.BOLD;
			}
			if (italic) {
				style |= SWT.ITALIC;
			}
			
			Font newFont = new Font(gc.getDevice(), faceName, size, style);
			loadedFonts.put(++latestFontId, newFont);
			return latestFontId;
		}

		@Override
		protected FontMetrics getFontMetrics(long hFont) {
			Font font = loadedFonts.get(hFont);
			gc.setFont(font);
			org.eclipse.swt.graphics.FontMetrics swtFM = gc.getFontMetrics();
			return new FontMetrics(swtFM.getHeight(), swtFM.getAscent() + swtFM.getLeading(), swtFM.getDescent(), swtFM.getHeight(), true);
		}
		
		@Override
		protected void deleteFont(long hFont) {
			Font font = loadedFonts.get(hFont);
			if (font != null) {
				font.dispose();
				loadedFonts.put(hFont, null);
			}
		}
		
		@Override
		protected int textWidth(String text, long hFont) {
			gc.setFont(loadedFonts.get(hFont));
			return gc.textExtent(text).x;
		}
		
		@Override
		protected Position getClientRect() {
			return new Position(getClientArea());
		}
		
		@Override
		protected void drawText(long hdc, String text, long hFont, WebColor color, Position pos) {
			Font font = loadedFonts.get(hFont);
			gc.setFont(font);

			int index = (color.red & 0xFF) << 16 + (color.green & 0xFF) << 8 + (color.blue & 0xFF); 
			Color swtColor = loadedColors.get(index);
			if (swtColor == null) {
				swtColor = new Color(gc.getDevice(), color.red & 0xFF, color.green & 0xFF, color.blue & 0xFF);
				loadedColors.put(index, swtColor);
			}
			gc.setAlpha(color.alpha & 0xFF);
			gc.setForeground(swtColor);
			
			gc.drawText(text, pos.x, pos.y, SWT.DRAW_TRANSPARENT);
		}
		
		@Override
		protected int ptToPx(int pt) {
			return (int)((float)pt * 96 / gc.getDevice().getDPI().x);
		}
		
		@Override
		protected String getDefaultFontName() {
			return getDisplay().getSystemFont().getFontData()[0].getName();
		}
		
		@Override
		protected int getDefaultFontSize() {
			return getDisplay().getSystemFont().getFontData()[0].getHeight();
		}
		
		@Override
		protected void drawBackground(long hdc, BackgroundPaint bg) {
			Color bgColor = new Color(gc.getDevice(), bg.color.red & 0xFF, bg.color.green & 0xFF, bg.color.blue & 0xFF);
			gc.setAlpha(bg.color.alpha & 0xFF);

			gc.setBackground(bgColor);

			gc.fillRectangle(bg.borderBox.toRectangle());
			if (!bg.image.equals("")) {
				gc.setAlpha(0xFF);
				
				Image img = new Image(gc.getDevice(), getImage(bg.image));
				
				int imageSourceWidth = img.getImageData().width;
				int imageSourceHeight = img.getImageData().height;
				
				int imageWidth = imageSourceWidth;
				int imageHeight = imageSourceHeight;
				if (bg.imageSize.width != 0) imageWidth = bg.imageSize.width; 
				if (bg.imageSize.height != 0) imageHeight = bg.imageSize.height; 
				
				gc.drawImage(img, 0, 0, img.getImageData().width, img.getImageData().height, 
				                  bg.positionX, bg.positionY, imageWidth, imageHeight);
				img.dispose();
			}

			bgColor.dispose();
		}
		
		
		@Override
		protected void finalize() throws Throwable {
			for (Font f: loadedFonts.values()) {
				f.dispose();
			}
			for (Color c : loadedColors.values()) {
				c.dispose();
			}
		}

		public void clearLoadedImages() {
			loadedImages.clear();
		}
		
		@Override
		protected void loadImage(String src, String baseUrl, boolean redrawOnReady) {
			getImage(src);
		}

		@Override
		protected Size getImageSize(String src, String baseUrl) {
			ImageData img = getImage(src);
			return new Size(img.width, img.height);
		}
		
		@Override
		protected void setCaption(String caption) {
			if (listener != null) {
				listener.setCaption(LiteHTMLView.this, caption);
			}
		}
	}
	
	private Container container = new Container();
	private Document document = null;

	private Point latestSize;
	@Override
	public Point computeSize(int widthHint, int heightHint, boolean changed) {
		if (changed) {
			int width = 0, height = 0;
			if (document != null) {
				if (widthHint == SWT.DEFAULT) {
					document.render(1000000);	// A million pixels... I hope that isn't possible on a PC
				} else {
					document.render(widthHint);
				}
				width = document.width();
	
				if (heightHint == SWT.DEFAULT) {
					height = document.height();
				} else {
					height = heightHint;
				}
			}
			
			latestSize = new Point(width, height);
		}
		return latestSize;
	}
	
	

	public LiteHTMLView(Composite parent, int style) {
		super(parent, style | SWT.TRANSPARENT);
		
		addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {
				if (document != null) {
					gc = arg0.gc;
					document.draw(123, 0, 0, container.getClientRect());
				}
			}
		});
		
	}
	
	public void setContent(String html, String masterCSS) {
		gc = new GC(this);
		this.document = new Document(html, masterCSS, container);
		gc.dispose();
		redraw();
	}
	
	public void setBasePath(String basePath) {
		this.basePath = basePath;
		if (container != null) {
			container.clearLoadedImages();
		}
		
	}
	
	public String getBasePath() {
		return basePath;
	}
	
	public void setListener(LiteHTMLViewListener listener) {
		this.listener = listener;
	}
}
