package zetes.wings.litehtml.jni;

public class Document {	
	private long nativeObject;
	
	private native long createNativeFromString(String html, String masterCSS, long container);
	private static native void destroyNative(long ptr);
	
	public Document(String html, String masterCSS, DocumentContainer container) {
		nativeObject = createNativeFromString(html, masterCSS, container.nativeObject);
	}
	
	@Override
	protected void finalize() throws Throwable {
		destroyNative(nativeObject);
		super.finalize();
	}
	
	private static native void nativeDraw(long nativeObject, long hdc, int x, int y, Position clip);
	public void draw(long hdc, int x, int y, Position clip) {
		nativeDraw(nativeObject, hdc, x, y, clip);
	}
	
	private static native int nativeRender(long nativeObject, int maxWidth);
	public int render(int maxWidth) {
		return nativeRender(nativeObject, maxWidth);
	}
	
	private static native int nativeWidth(long nativeObject);
	public int width() {
		return nativeWidth(nativeObject);
	}

	private static native int nativeHeight(long nativeObject);
	public int height() {
		return nativeHeight(nativeObject);
	}
}
