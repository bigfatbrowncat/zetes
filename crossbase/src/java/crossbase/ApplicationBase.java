package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.abstracts.Application;
import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;
import crossbase.ui.DefaultAboutBox;
import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.ViewWindowsManager;


public abstract class ApplicationBase<TAB extends DefaultAboutBox, 
                                      TD extends Document, 
                                      TVW extends ViewWindow<TD>, 
                                      TMC extends MenuConstructor<TD, TVW>> implements Application<TAB, TD, TVW, TMC>
{
	private DefaultAboutBox aboutBox = null;

	private TMC menuConstructor;
	private ViewWindowsManager<TD, ? extends ViewWindow<TD>> viewWindowsManager;

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
				display.sleep();
			}
		}
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
	
	public void run(String[] arguments, Runnable beforeEventLoop)
	{
		SingleAppInstanceDocumentHandler mdiHelper = null;
		try
		{
			Display.setAppName(getTitle());
	
			viewWindowsManager = createViewWindowsManager();

			menuConstructor = createMenuConstructor();
			menuConstructor.setExitSelectionAdapter(exitSelectionAdapter);
			menuConstructor.setAboutSelectionAdapter(aboutSelectionAdapter);
			menuConstructor.updateMenus();
			
			// Here we guarantee that menuConstructor type is compatible to viewWindowsManager
			((ViewWindowsManager)viewWindowsManager).setMenuConstructor(menuConstructor);
			
			
			if (SWT.getPlatform().equals("cocoa"))
			{
				// In Cocoa we use a special hook class to handle the default
				// About, Quit and Preferences items from the system menu.
				new CocoaUIEnhancer(getTitle()).hookApplicationMenu(Display.getDefault(), exitSelectionAdapter, aboutSelectionAdapter, preferencesSelectionAdapter);
	
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
	
				viewWindowsManager.ensureThereIsOpenedWindow();
			}
			
			if (beforeEventLoop != null) beforeEventLoop.run();
					
			eventLoop();
			
		}
		finally
		{
			if (!SWT.getPlatform().equals("cocoa"))
			{
				if (mdiHelper != null) mdiHelper.stop();
			}
		}		
	}
	
	public ViewWindowsManager<TD, ? extends ViewWindow<TD>> getViewWindowsManager()
	{
		return viewWindowsManager;
	}
}
