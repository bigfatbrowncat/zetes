package zetes.wings.litehtml.jni;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class WebColor {
	public final short red, green, blue, alpha;

	public WebColor(short red, short green, short blue, short alpha) {
		if (red > 0xFF || red < 0) throw new IllegalArgumentException("red should be in range (0..255)");
		if (green > 0xFF || green < 0) throw new IllegalArgumentException("green should be in range (0..255)");
		if (blue > 0xFF || blue < 0) throw new IllegalArgumentException("blue should be in range (0..255)");
		if (alpha > 0xFF || alpha < 0) throw new IllegalArgumentException("alpha should be in range (0..255)");

		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

}
