package zetes.wings.litehtml.jni;

import org.eclipse.swt.graphics.Point;

public class Size {
	public final int width, height;

	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Point toPoint() {
		return new Point(width, height);
	}
	
	public Size(Point sz) {
		width = sz.x;
		height = sz.y;
	}
	
}
