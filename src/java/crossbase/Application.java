package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import tinyviewer.ui.TinyViewerApplication;
import tinyviewer.ui.ViewWindow;
import tinyviewer.ui.ViewWindowFactory;

import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.ui.AboutBox;
import crossbase.ui.AboutHandler;
import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.DocumentWindow;
import crossbase.ui.DocumentWindowsManager;
import crossbase.ui.DocumentHandler;
import crossbase.ui.MenuConstructor;
import crossbase.ui.PreferencesHandler;


public class Application
{
	public static final String APP_NAME = "SWT Application";
	
	private MenuConstructor menuConstructor;
	private DocumentWindowsManager<? extends DocumentWindow> documentWindowsManager;

	protected void showAbout()
	{
		
	}
	
	protected void showPreferences()
	{
		
	}
	
	private Listener openDocumentListener = new Listener()
	{
		
		@Override
		public void handleEvent(Event arg0) {
			String fileName = arg0.text;
			documentWindowsManager.openFile(fileName);
		}
	};
	
	private SelectionAdapter aboutSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			showAbout();
		}
	};
	
	private SelectionAdapter preferencesSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			showPreferences();
		}
	};
	
	private void eventLoop()
	{
		Display display = Display.getDefault();
		while (!display.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
	
	private SelectionAdapter exitSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Display.getDefault().dispose();
		}
	};
	
	protected Application()
	{

	}
	
	public void run(String[] arguments)
	{
		SingleAppInstanceDocumentHandler mdiHelper = null;
		try
		{
			Display.setAppName(APP_NAME);
	
			menuConstructor.setExitSelectionAdapter(exitSelectionAdapter);
			menuConstructor.setAboutSelectionAdapter(aboutSelectionAdapter);
			
			if (SWT.getPlatform().equals("cocoa"))
			{
				// In Cocoa we use a special hook class to handle the default
				// About, Quit and Preferences items from the system menu.
				new CocoaUIEnhancer(APP_NAME).hookApplicationMenu(Display.getDefault(), exitSelectionAdapter, aboutSelectionAdapter, preferencesSelectionAdapter);
	
				// Add listener to OpenDocument event thus user can open documents
				// with our Cocoa application
				Display.getCurrent().addListener(SWT.OpenDocument, openDocumentListener);
				
				// Creating the main application menu
				menuConstructor.appendMenusToGlobalMenu();
			}
			else
			{
				try
				{
					mdiHelper = new SingleAppInstanceDocumentHandler(arguments, openDocumentListener);
					if (!mdiHelper.isServer())
					{
						// In that case we have done our job and just exiting "main"
						return;
					}
				}
				catch (FileNamesSendingFailed e)
				{
					e.printStackTrace();
					return;
				}
	
				documentWindowsManager.ensureThereIsOpenedWindow();
			}
			
			eventLoop();
			
		}
		finally
		{
			if (!SWT.getPlatform().equals("cocoa"))
			{
				mdiHelper.stop();
			}
	
			System.out.print("Bye!\n");
		}		
	}
	
	public DocumentWindowsManager<? extends DocumentWindow> getDocumentWindowsManager()
	{
		return documentWindowsManager;
	}
	
	public void setDocumentWindowsManager(DocumentWindowsManager<? extends DocumentWindow> documentWindowsManager)
	{
		this.documentWindowsManager = documentWindowsManager;
	}
	
	public MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}
	
	public void setMenuConstructor(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	public static void main(String... args)
	{
		//new Application(args);
		new TinyViewerApplication().run(args);
		
	}
}
