package zetes.wings.litehtml.jni;

public class BackgroundPaint {
	public final String image;
	public final String baseUrl;
	public final WebColor color;
	public final Position originBox;
	public final Position borderBox;

	public BackgroundPaint(String image, String baseUrl, 
	                       WebColor color, 
	                       Position originBox, Position borderBox) {
		this.image = image;
		this.baseUrl = baseUrl;
		this.color = color;
		this.originBox = originBox;
		this.borderBox = borderBox;
	}
	
}
