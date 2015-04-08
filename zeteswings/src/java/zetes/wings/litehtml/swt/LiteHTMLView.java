package zetes.wings.litehtml.swt;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import zetes.wings.litehtml.jni.BackgroundPaint;
import zetes.wings.litehtml.jni.Document;
import zetes.wings.litehtml.jni.FontMetrics;
import zetes.wings.litehtml.jni.Position;
import zetes.wings.litehtml.jni.WebColor;

public class LiteHTMLView extends Composite {

	private GC gc;

	public class Container extends zetes.wings.litehtml.jni.DocumentContainer {
		private long latestFontId = -1;
		private HashMap<Long, Font> loadedFonts = new HashMap<Long, Font>();
		private HashMap<Integer, Color> loadedColors = new HashMap<>();
		
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
			gc.setFont(loadedFonts.get(hFont));

			int index = (color.red & 0xFF) << 16 + (color.green & 0xFF) << 8 + (color.blue & 0xFF); 
			Color swtColor = loadedColors.get(index);
			if (swtColor == null) {
				swtColor = new Color(gc.getDevice(), color.red & 0xFF, color.green & 0xFF, color.blue & 0xFF);
				loadedColors.put(index, swtColor);
			}
			if (gc.getAlpha() != (color.alpha & 0xFF)) {
				gc.setAlpha(color.alpha & 0xFF);
			}
			if (gc.getForeground() != swtColor) {
				gc.setForeground(swtColor);
			}
			
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
			bgColor.dispose();
		}
	}
	
	private Container container = new Container();
	private Document document = null;

	@Override
	public Point computeSize(int widthHint, int heightHint, boolean changed) {
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
		
		return new Point(width, height);
	}
	
	

	public LiteHTMLView(Composite parent, int style) {
		super(parent, style);
		
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
}
