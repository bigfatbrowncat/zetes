package zetes.wings;

public class WinLinMacApi {

	public static native String locateExecutable();
	public static native String locateResource(String path, String filename);
	public static native String getAppId();
}
