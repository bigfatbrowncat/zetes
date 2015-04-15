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
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import zetes.wings.litehtml.jni.BackgroundPaint;
import zetes.wings.litehtml.jni.Document;
import zetes.wings.litehtml.jni.FontMetrics;
import zetes.wings.litehtml.jni.Position;
import zetes.wings.litehtml.jni.Size;
import zetes.wings.litehtml.jni.WebColor;

public class LiteHTMLView extends Composite {

	private String basePath; 
	private LiteHTMLViewListener listener;

	public class Container extends zetes.wings.litehtml.jni.DocumentContainer {
		private GC gc;
		
		private long latestFontId = -1;
		private HashMap<Long, Font> loadedFonts = new HashMap<Long, Font>();
		private HashMap<Integer, Color> loadedColors = new HashMap<>();
		private HashMap<String, ImageData> loadedImages = new HashMap<>();

		private class TextCacheItemKey {
			public final Font font;
			public final Color color;
			public final String string;
			public TextCacheItemKey(Font font, Color color, String string) {
				super();
				this.font = font;
				this.color = color;
				this.string = string;
			}
			@Override
			public int hashCode() {
				return string.hashCode() + 1000 * color.hashCode() + 100000 * font.hashCode();
			}
			@Override
			public boolean equals(Object other) {
				if (other instanceof TextCacheItemKey) {
					TextCacheItemKey o = ((TextCacheItemKey) other);
					return o.font.equals(font) && o.string.equals(string) && o.color.equals(color);
				} else {
					return false;
				}
			}
		}
		private HashMap<TextCacheItemKey, Image> cachedTextImages = new HashMap<>();
		
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
			
			Font newFont = new Font(getDisplay(), faceName, size, style);
			loadedFonts.put(++latestFontId, newFont);
			return latestFontId;
		}

		@Override
		protected FontMetrics getFontMetrics(long hFont) {
			Font font = loadedFonts.get(hFont);
			GC gc = new GC(getDisplay());
			gc.setFont(font);
			org.eclipse.swt.graphics.FontMetrics swtFM = gc.getFontMetrics();
			gc.dispose();
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
			GC gc = new GC(getDisplay());
			gc.setFont(loadedFonts.get(hFont));
			int res = gc.textExtent(text).x;
			gc.dispose();
			return res;
		}
		
		@Override
		protected Position getClientRect() {
			return new Position(getClientArea());
		}
		
		private Image bufferStringImage(GC gc, String string) {
			Point te = gc.textExtent(string, SWT.TRANSPARENT);
			
			ImageData imgDt = new ImageData(te.x, te.y, 32, new PaletteData(0xff000000, 0x00ff0000, 0x0000ff00));
			imgDt.setAlpha(0, 0, 0);
			Image img = new Image(gc.getDevice(), imgDt);
			
			GC imgGc = new GC(img);
			imgGc.setFont(gc.getFont());
			imgGc.setBackground(new Color(gc.getDevice(), 0x00, 0x00, 0x00));
			imgGc.setForeground(new Color(gc.getDevice(), 0xFF, 0xFF, 0xFF));
			
			imgGc.drawText(string, 0, 0, SWT.TRANSPARENT);
			
			imgGc.dispose();
			imgDt = img.getImageData();

			for (int i = 0; i < te.x * te.y; i++) {
				imgDt.alphaData[i] = imgDt.data[i * 4 + 1];
				imgDt.data[i * 4 + 0] = (byte) gc.getForeground().getRed();
				imgDt.data[i * 4 + 1] = (byte) gc.getForeground().getGreen();
				imgDt.data[i * 4 + 2] = (byte) gc.getForeground().getBlue();
			}
			return new Image(gc.getDevice(), imgDt);
		}
		
		/*private void drawFast(ImageData target, ImageData source, int x, int y) {
			int tc, sc, s0, sa, sr, sg, sb, t0, tr, tg, tb;
			for (int i = 0; i < source.width; i++) {
				for (int j = 0; j < source.height; j++) {
					if (j + y < target.height && i + x < target.width) {
						tc = ((j + y) * target.width + (i + x));
						sc = ((j + 0) * source.width + (i + 0));
						
						sa = source.alphaData[sc] & 0xff;
						
						s0 = source.data[sc * 4 + 0] & 0xff;
						sr = source.data[sc * 4 + 1] & 0xff;
						sg = source.data[sc * 4 + 2] & 0xff;
						sb = source.data[sc * 4 + 3] & 0xff;

						t0 = target.data[tc * 4 + 0] & 0xff;
						tr = target.data[tc * 4 + 1] & 0xff;
						tg = target.data[tc * 4 + 2] & 0xff;
						tb = target.data[tc * 4 + 3] & 0xff;
						
						
						float a = ((float)sa) / 255;
						target.data[tc * 4 + 0] = (byte) (t0 * (1 - a) + s0 * a);
						target.data[tc * 4 + 1] = (byte) (tr * (1 - a) + sr * a);
						target.data[tc * 4 + 2] = (byte) (tg * (1 - a) + sg * a);
						target.data[tc * 4 + 3] = (byte) (tb * (1 - a) + sb * a);
					}
				}
			}
		}*/

		
		@Override
		protected void drawText(long hdc, String text, long hFont, WebColor color, Position pos) {
			
			Font font = loadedFonts.get(hFont);

			int index = (color.red & 0xFF) << 16 + (color.green & 0xFF) << 8 + (color.blue & 0xFF); 
			Color swtColor = loadedColors.get(index);
			if (swtColor == null) {
				swtColor = new Color(getDisplay(), color.red & 0xFF, color.green & 0xFF, color.blue & 0xFF);
				loadedColors.put(index, swtColor);
			}
			//gc.setAlpha(color.alpha & 0xFF);

			TextCacheItemKey cik = new TextCacheItemKey(font, swtColor, text);
			if (!cachedTextImages.containsKey(cik)) {
				gc.setForeground(swtColor);
				gc.setFont(font);
				cachedTextImages.put(cik, bufferStringImage(gc, text));
			}
			gc.drawImage(cachedTextImages.get(cik), pos.x, pos.y);
			//drawFast(buffer, cachedTextImages.get(cik), pos.x, pos.y);

		}
		
		@Override
		protected int ptToPx(int pt) {
			return (int)((float)pt * 96 / getDisplay().getDPI().x);
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
				
				ImageData data = getImage(bg.image);
				Image img = new Image(gc.getDevice(), data);
				
				int imageSourceWidth = data.width;
				int imageSourceHeight = data.height;
				
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
			if (img != null) {
				return new Size(img.width, img.height);
			} else {
				return new Size(0, 0);
			}
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
				//container.updateBuffer();
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
					container.gc = arg0.gc;
					document.draw(123, 0, 0, container.getClientRect());
				}
			}
		});
		
	}
	
	public void setContent(String html, String masterCSS) {
		//container.updateBuffer();
		this.document = new Document(html, masterCSS, container);
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
