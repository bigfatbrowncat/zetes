package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.abstracts.AboutBox;
import crossbase.abstracts.Application;
import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;
import crossbase.abstracts.ViewWindowsManagerListener;
import crossbase.ui.ViewWindowsManager;

//**
// * <p>This class is the base for Multi Document architecture.</p>
// * 
// * <p>You should use it if your application opens more than one document at a time. 
// * Some native Windows and Linux applications work in a mode of multi-loaded single-document
// * application style (for example you can open as much Notepad windows as you want and all of them
// * would become independent different processes). But this model is impossible on OS X where
// * only one instance of a UI application is possible to be executed at a moment.</p>
// */
public abstract class ApplicationBase<TAB extends AboutBox, 
                                      TD extends Document, 
                                      TVW extends ViewWindow<TD>, 
                                      TMC extends MenuConstructor<TD, TVW>,
                                      TVWM extends ViewWindowsManager<TD, TVW, TMC>> implements Application<TAB, TD, TVW, TMC, TVWM>
{
	private final int OSX_SYSTEM_MENU_ABOUT = -1;
	private final int OSX_SYSTEM_MENU_PREFERENCES = -2;
	private final int OSX_SYSTEM_MENU_QUIT = -6;
	
	
	private AboutBox aboutBox = null;

	private TMC menuConstructor;
	private TVWM viewWindowsManager;

	public abstract String getTitle();
	
	/**
	 * Shows the about box. The box shouldn't be opened already. If it is not, it will be
	 * created using <code>aboutBoxFactory</code>.
	 * @param shell The parent shell for about box window to create.
	 *              May be null &#151; in that case the about box will be created for the whole display.
	 */
	protected final void showAbout(Shell shell)
	{
		if (aboutBox == null || aboutBox.isDisposed())
		{
			boolean dummyShell = false;
			if (shell == null) 
			{
				shell = new Shell(Display.getDefault());
				dummyShell = true;
			}
			
			aboutBox = createAboutBox(shell);
			aboutBox.open();

			if (dummyShell)
			{
				shell.dispose();
			}
		}		
	}
	
	protected void showPreferences()
	{
		// TODO Implement a default preferences window
	}
	
	private Listener openDocumentListener = new Listener()
	{
		
		@Override
		public void handleEvent(final Event arg0)
		{
			String fileName = arg0.text;
			TD loadedDoc = loadFromFile(fileName);
			if (loadedDoc != null)
			{
				viewWindowsManager.openViewForDocument(loadedDoc);
			}
		}
	};
	
	private SelectionAdapter aboutSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			if (arg0.display != null)
			{
				showAbout(arg0.display.getActiveShell());
			}
			else
			{
				showAbout(null);
			}
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
				onIdle();
			}
		}
	}
	
	/**
	 * Override this if you want to do something special in the main loop
	 */
	protected void onIdle()
	{
		Display display = Display.getDefault();
		display.sleep();
	}
	
	private SelectionAdapter exitSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			viewWindowsManager.closeAllWindows();
			if (Display.getDefault() != null && !Display.getDefault().isDisposed())
			{
				Display.getDefault().dispose();
			}
		}
	};
	
	protected ApplicationBase()
	{
	}
	
	ViewWindowsManagerListener viewWindowsManagerListener = new ViewWindowsManagerListener()
	{
		@Override
		public void lastWindowClosed()
		{
			if (!SWT.getPlatform().equals("cocoa") || needsAtLeastOneView())
			{
				if (Display.getDefault() != null && !Display.getDefault().isDisposed())
				{
					Display.getDefault().dispose();
				}
			}
		}
	};
	
	public void run(String[] arguments)
	{
		SingleAppInstanceDocumentHandler mdiHelper = null;
		try
		{
			Display.setAppName(getTitle());
	
			viewWindowsManager = createViewWindowsManager();
			
			viewWindowsManager.addListener(viewWindowsManagerListener);

			menuConstructor = createMenuConstructor();
			menuConstructor.setExitSelectionAdapter(exitSelectionAdapter);
			menuConstructor.setAboutSelectionAdapter(aboutSelectionAdapter);
			menuConstructor.updateMenus();
			
			// Here we guarantee that menuConstructor type is compatible to viewWindowsManager
			viewWindowsManager.setMenuConstructor(menuConstructor);
			
			
			if (SWT.getPlatform().equals("cocoa"))
			{
				for (int i = 0; i < Display.getDefault().getSystemMenu().getItems().length; i++)
				{
					MenuItem item = Display.getDefault().getSystemMenu().getItems()[i];
					System.out.print(item.getText() + ", id=" + item.getID()  + "\n");
					
					switch (item.getID())
					{
					case OSX_SYSTEM_MENU_ABOUT:
						item.addSelectionListener(aboutSelectionAdapter);
						break;
					case OSX_SYSTEM_MENU_PREFERENCES:
						item.addSelectionListener(preferencesSelectionAdapter);
						break;
					case OSX_SYSTEM_MENU_QUIT:
						item.addSelectionListener(exitSelectionAdapter);
						break;
					}
				}
				
				// In Cocoa we use a special hook class to handle the default
				// About, Quit and Preferences items from the system menu.
				//new CocoaUIEnhancer(getTitle()).hookApplicationMenu(Display.getDefault(), exitSelectionAdapter, aboutSelectionAdapter, preferencesSelectionAdapter);
	
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
			}
			
			if (!SWT.getPlatform().equals("cocoa") || needsAtLeastOneView())
			{
				// Opening the first empty view when we need it 
				viewWindowsManager.ensureThereIsOpenedWindow();
			}
			
			eventLoop();
			
			viewWindowsManager.removeListener(viewWindowsManagerListener);
		}
		finally
		{
			if (!SWT.getPlatform().equals("cocoa"))
			{
				if (mdiHelper != null) mdiHelper.stop();
			}
		}		
	}
	
	public TVWM getViewWindowsManager()
	{
		return viewWindowsManager;
	}
}
