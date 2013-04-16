package crossbase;

import crossbase.ui.MainWindow;
//import crossbase.ui.extensions.cocoa.CocoaUIEnhancer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;


public class Application
{
	public static final String APP_NAME = "CrossBase SWT Application";
	
	public static void main(String... args)
	{
		Display.setAppName(APP_NAME);
	
		boolean aboutBoxInHelpMenu = true;
		if (SWT.getPlatform().equals("cocoa"))
		{
			//new CocoaUIEnhancer().earlyStartup();

			// In Cocoa we don't add "About..." item the the help menu 
			// cause it should appear in the system menu
			aboutBoxInHelpMenu = false;
		}

		MainWindow mainWindow = new MainWindow(aboutBoxInHelpMenu);
		mainWindow.open();
	}
}
