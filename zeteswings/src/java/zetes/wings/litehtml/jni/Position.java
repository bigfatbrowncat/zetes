package zetes.wings.litehtml.jni;

import org.eclipse.swt.graphics.Rectangle;

public class Position {
	public final int x, y, width, height;

	public Position(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle toRectangle() {
		return new Rectangle(x, y, width, height);
	}
	
	public Position(Rectangle rect) {
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}
	
}
