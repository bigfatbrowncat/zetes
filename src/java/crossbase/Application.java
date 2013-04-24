package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import tinyviewer.ui.TinyViewerApplication;
import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.ui.AboutBox;
import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.DefaultMenuConstructor;
import crossbase.ui.ViewWindowsManager;
import crossbase.ui.abstracts.AboutBoxFactory;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;


public class Application
{
	public static final String APP_NAME = "SWT Application";
	
	private AboutBox aboutBox = null;
	private AboutBoxFactory<? extends AboutBox> aboutBoxFactory;

	private DefaultMenuConstructor menuConstructor;
	private ViewWindowsManager<? extends ViewWindow> documentWindowsManager;

	/**
	 * Shows the about box. The box shouldn't be opened already. If it is not, it will be
	 * created using <code>aboutBoxFactory</code>.
	 * @param shell The parent shell for about box window to create.
	 *              May be null &#151; in that case the about box will be created for the whole display.
	 */
	protected void showAbout(Shell shell)
	{
		if (aboutBox == null || aboutBox.isDisposed())
		{
			boolean dummyShell = false;
			if (shell == null) 
			{
				shell = new Shell(Display.getDefault());
				dummyShell = true;
			}
			
			aboutBox = aboutBoxFactory.create(shell);
			aboutBox.open();

			if (dummyShell)
			{
				shell.dispose();
			}
		}		
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
			showAbout(arg0.display.getActiveShell());
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
			documentWindowsManager.closeAllWindows();
			if (Display.getDefault() != null && !Display.getDefault().isDisposed())
			{
				Display.getDefault().dispose();
			}
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
				Display.getDefault().addListener(SWT.OpenDocument, openDocumentListener);
				
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
				if (mdiHelper != null) mdiHelper.stop();
			}
	
			System.out.print("Bye!\n");
		}		
	}
	
	public ViewWindowsManager<? extends ViewWindow> getDocumentWindowsManager()
	{
		return documentWindowsManager;
	}
	
	public void setDocumentWindowsManager(ViewWindowsManager<? extends ViewWindow> documentWindowsManager)
	{
		this.documentWindowsManager = documentWindowsManager;
	}
	
	public MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}
	
	public void setMenuConstructor(DefaultMenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	public AboutBoxFactory<? extends AboutBox> getAboutBoxFactory()
	{
		return aboutBoxFactory;
	}

	public void setAboutBoxFactory(AboutBoxFactory<? extends AboutBox> aboutBoxFactory)
	{
		this.aboutBoxFactory = aboutBoxFactory;
	}

	public static void main(String... args)
	{
		//new Application(args);
		new TinyViewerApplication().run(args);
		
	}
}
