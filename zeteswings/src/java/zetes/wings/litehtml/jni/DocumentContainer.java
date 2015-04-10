package zetes.wings.litehtml.jni;

public abstract class DocumentContainer {
	final long nativeObject;
	
	private native long createNativeObject();
	private native void destroyNativeObject(long ptr);
	
	public DocumentContainer() {
		nativeObject = createNativeObject();
	}
	
	@Override
	protected void finalize() throws Throwable {
		destroyNativeObject(nativeObject);
		super.finalize();
	}
	
	protected abstract int textWidth(String text, long hFont);
	protected abstract void drawText(long hdc, String text, long hFont, WebColor color, Position pos);
	protected abstract long createFont(String faceName, int size, int weight, boolean italic);
	protected abstract FontMetrics getFontMetrics(long hFont);
	protected abstract void deleteFont(long hFont);
	protected abstract Position getClientRect();
	protected abstract int ptToPx(int pt);
	protected abstract int getDefaultFontSize();
	protected abstract String getDefaultFontName();
	protected abstract void drawBackground(long hdc, BackgroundPaint bg);
	protected abstract void loadImage(String src, String baseUrl, boolean redrawOnReady);
	protected abstract Size getImageSize(String src, String baseUrl);
	
}
