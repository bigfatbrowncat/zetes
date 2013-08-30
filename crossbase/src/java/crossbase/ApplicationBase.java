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
                                      TMC extends MenuConstructor<TVW>,
                                      TVWM extends ViewWindowsManager<TD, TVW, TMC>> implements Application<TAB, TD, TVW, TMC, TVWM>
{
	private final int OSX_SYSTEM_MENU_ABOUT = -1;
	private final int OSX_SYSTEM_MENU_PREFERENCES = -2;
	private final int OSX_SYSTEM_MENU_QUIT = -6;
	
	private boolean dummyShell = false;
	private Shell shell;
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
	protected final void showAbout(Shell parentShell)
	{
		if (aboutBox == null || aboutBox.isDisposed())
		{
			if (dummyShell)
			{
				shell.dispose();
				shell = null;
				dummyShell = false;
			}
			
			if (!SWT.getPlatform().equals("cocoa"))
			{
				// For OS X ignoring the parent shell. About box should be independent of any window
				shell = parentShell;
			}
			
			if (shell == null) 
			{
				shell = new Shell(Display.getDefault());
				dummyShell = true;
			}
			
			aboutBox = createAboutBox(shell);
			aboutBox.open();
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
	
	private Listener applicationCloseListener = new Listener() {
		
		@Override
		public void handleEvent(Event arg0) {
			// That listener is necessary to handle closing process correctly
			// If we remove this handler, the system menu will stay
			// showing our applcation after it's closed
			arg0.doit = false;
			terminated = true;
			
		}
	};
	
	private SelectionAdapter aboutSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(final SelectionEvent arg0)
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
	
	boolean terminated = false;
	
	private void eventLoop()
	{
		Display display = Display.getDefault();
		try
		{
			while (!terminated && !display.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					onIdle();
				}
			}
		}
		finally
		{
			display.dispose();
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
				terminated = true;
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
					terminated = true;
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
			menuConstructor.updateMenus(null);
			
			// Here we guarantee that menuConstructor type is compatible to viewWindowsManager
			viewWindowsManager.setMenuConstructor(menuConstructor);
			
			// Adding OS X system menu handlers
			if (SWT.getPlatform().equals("cocoa"))
			{
				for (int i = 0; i < Display.getDefault().getSystemMenu().getItems().length; i++)
				{
					MenuItem item = Display.getDefault().getSystemMenu().getItems()[i];
					
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
			
			// Adding a proper close listener
			Display.getDefault().addListener(SWT.Close, applicationCloseListener);

			
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
