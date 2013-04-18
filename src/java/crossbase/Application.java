package crossbase;

import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.MainWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class Application
{
	public static final String APP_NAME = "SWT Application";
	
	private static MainWindow mainWindow = null;

	private static Listener quitListener = new Listener()
	{
		@Override
		public void handleEvent(Event arg0)
		{
			if (mainWindow != null)
			{
				mainWindow.userClose();
			}
		}
	};
	
	private static Runnable aboutAction = new Runnable()
	{
		
		@Override
		public void run()
		{
			if (mainWindow != null)
			{
				mainWindow.userAbout();
			}			
		}
	};
	
	private static Runnable preferencesAction = new Runnable()
	{
		
		@Override
		public void run()
		{
			if (mainWindow != null)
			{
				mainWindow.userPreferences();
			}			
		}
	};
	
	private static Listener openDocumentListener = new Listener() {
		
		@Override
		public void handleEvent(Event arg0) {
			String fileName = arg0.text;
			mainWindow.openImageFile(fileName);
		}
	};
	
	
	public static void main(String... args) throws InterruptedException
	{
		boolean shouldIOpenWindow = true;
		MDIHelper mdiHelper = null;
		
		if (SWT.getPlatform().equals("win32"))
		{
			mdiHelper = new MDIHelper(args, openDocumentListener);
			shouldIOpenWindow = !mdiHelper.areFilesSent();
		}
		
		if (shouldIOpenWindow)
		{
			Display.setAppName(APP_NAME);
	
			mainWindow = new MainWindow();
	
			if (SWT.getPlatform().equals("cocoa"))
			{
				// In Cocoa we use a special hook class to handle the default
				// About, Quit and Preferences items from the system menu.
				new CocoaUIEnhancer(APP_NAME).hookApplicationMenu(Display.getDefault(), quitListener, aboutAction, preferencesAction);
	
				// Add listener to OpenDocument event thus user can open documents
				// with our Cocoa application
				Display.getCurrent().addListener(SWT.OpenDocument, openDocumentListener);
			}
			
			mainWindow.open();
		}
		
		if (SWT.getPlatform().equals("win32"))
		{
			mdiHelper.stop();
		}
			
		System.out.print("Bye!\n");
	}
}
