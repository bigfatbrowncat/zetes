package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import tinyviewer.ui.ViewWindow;
import tinyviewer.ui.ViewWindowFactory;

import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.ui.AboutBox;
import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.DocumentWindowsManager;
import crossbase.ui.MenuConstructor;


public class Application
{
	public static final String APP_NAME = "SWT Application";
	
	private MenuConstructor menuConstructor;
	private AboutBox aboutBox = null;
	private DocumentWindowsManager<ViewWindow> documentWindowsManager;
	
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
			if (aboutBox == null || aboutBox.isDisposed())
			{
				Shell dummyShell = new Shell(Display.getDefault());
				aboutBox = new AboutBox(dummyShell);
				aboutBox.open();
				dummyShell.dispose();
			}
		}
	};
	
	private SelectionAdapter preferencesSelectionAdapter = new SelectionAdapter()
	{
		
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
/*			if (mainWindow != null)
			{
				mainWindow.userPreferences();
			}	*/
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
	
	private SelectionAdapter openSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Shell dummyShell = new Shell(Display.getDefault());
			FileDialog fileDialog = new FileDialog(dummyShell, SWT.OPEN);
			fileDialog.setText("Open image");
			fileDialog.setFilterNames(new String[] { "Image (*.png; *.bmp; *.jpg; *.jpeg)", "All files" });
			fileDialog.setFilterExtensions(new String[] { "*.png; *.bmp; *.jpg; *.jpeg", "*.*" });
			String fileName = fileDialog.open();
			if (fileName != null)
			{
				documentWindowsManager.openFile(fileName);
			}
			dummyShell.dispose();
		}
	};
		
	private SelectionAdapter exitSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Display.getDefault().dispose();
		}
	};
	
	protected Application(String[] arguments)
	{
		Display.setAppName(APP_NAME);
		
		menuConstructor = new MenuConstructor();
		menuConstructor.setOpenSelectionAdapter(openSelectionAdapter);
		menuConstructor.setExitSelectionAdapter(exitSelectionAdapter);
		menuConstructor.setAboutSelectionAdapter(aboutSelectionAdapter);

		documentWindowsManager = new DocumentWindowsManager<ViewWindow>(new ViewWindowFactory(menuConstructor));
		
		SingleAppInstanceDocumentHandler mdiHelper = null;
		
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
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			mdiHelper.stop();
		}

		System.out.print("Bye!\n");	
	}
	
	public static void main(String... args)
	{
		new Application(args);
	}
}
