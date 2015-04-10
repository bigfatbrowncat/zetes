package zetes.wings.litehtml.jni;

public class BackgroundPaint {
	public final String image;
	public final String baseUrl;
	public final WebColor color;
	public final Position originBox;
	public final Position borderBox;
	public final Size imageSize;
	public final int positionX;
	public final int positionY;

	public BackgroundPaint(String image, String baseUrl, 
	                       WebColor color, 
	                       Position originBox, Position borderBox,
	                       Size imageSize, int positionX, int positionY) {
		this.image = image;
		this.baseUrl = baseUrl;
		this.color = color;
		this.originBox = originBox;
		this.borderBox = borderBox;
		this.imageSize = imageSize;
		this.positionX = positionX;
		this.positionY = positionY;
	}
	
}
