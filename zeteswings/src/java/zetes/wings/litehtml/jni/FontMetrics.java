package zetes.wings.litehtml.jni;

public class FontMetrics {

	public final int		height;
	public final int		ascent;
	public final int		descent;
	public final int		xHeight;
	public final boolean	drawSpaces;
	
	public FontMetrics(int height, int ascent, int descent, int xHeight, boolean drawSpaces) {
		this.height = height;
		this.ascent = ascent;
		this.descent = descent;
		this.xHeight = xHeight;
		this.drawSpaces = drawSpaces;
	}
	
	
}
