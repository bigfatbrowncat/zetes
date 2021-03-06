package zetes.wings.base;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import zetes.feet.WinLinMacApi;
import zetes.wings.SingleAppInstanceDocumentHandler;
import zetes.wings.UnhandledExceptionBox;
import zetes.wings.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import zetes.wings.abstracts.AboutBox;
import zetes.wings.abstracts.Application;
import zetes.wings.abstracts.ApplicationListener;
import zetes.wings.abstracts.Document;
import zetes.wings.abstracts.ViewWindow;
import zetes.wings.abstracts.ViewWindowsManager;
import zetes.wings.abstracts.ViewWindowsManagerListener;
import zetes.wings.actions.Handler;
import zetes.wings.base.ApplicationBase;

public abstract class ApplicationBase<TAB extends AboutBox, 
                                      TD extends Document, 
                                      TVW extends ViewWindow<TD>, 
                                      TMC extends MenuConstructorBase<TVW>,
                                      TVWM extends ViewWindowsManager<TD, TVW>> implements Application<TAB, TD, TVW, TMC, TVWM>
{

	private AboutBox aboutBox = null;

	private TMC menuConstructor;
	private TVWM viewWindowsManager;
	
	private ApplicationListener listener;

	public abstract String getTitle();
	
	/**
	 * Shows the about box. The box shouldn't be opened already. If it is not, it will be
	 * created using <code>aboutBoxFactory</code>.
	 * @param shell The parent shell for about box window to create.
	 *              May be null &#151; in that case the about box will be created for the whole display.
	 */
	protected final void showAbout(TVW parentWindow)
	{
		if (aboutBox == null || aboutBox.isDisposed())
		{
			if (!SWT.getPlatform().equals("cocoa")) {
				aboutBox = createAboutBox(parentWindow);
			} else {
				aboutBox = createAboutBox(null);
			}
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
				viewWindowsManager.openWindowForDocument(loadedDoc);
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
	
	private Handler<TVW> exitHandler = new Handler<TVW> () {

		@Override
		public void execute(TVW window) {
			viewWindowsManager.closeAllWindows();
			if (Display.getDefault() != null && !Display.getDefault().isDisposed())
			{
				terminated = true;
			}
		}
		
	};
	
	private Handler<TVW> aboutHandler = new Handler<TVW> () {

		@Override
		public void execute(TVW window) {
			if (window != null)
			{
				showAbout(window);
			}
			else
			{
				showAbout(null);
			}
		}
		
		@Override
		public String getTitle() {
			return "About " + ApplicationBase.this.getTitle();
		}
		
	};
	
	boolean terminated = false;
	
	private void eventLoop()
	{
		Display display = Display.getDefault();
		try
		{
			if (listener != null) listener.started(this);
			while (!terminated /*&& !display.isDisposed()*/)
			{
				if (!display.readAndDispatch())
				{
					onIdle();
				}
			}
			if (listener != null) listener.stopped(this);
		} catch (Throwable e) {
			UnhandledExceptionBox box = new UnhandledExceptionBox(null);
			box.setException(e);
			box.open();
		} finally {
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
	
	protected ApplicationBase()
	{
	}
	
	private ViewWindowsManagerListener<TVW> viewWindowsManagerListener = new ViewWindowsManagerListener<TVW>()
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

		@Override
		public void windowOpened(TVW window) {
			
		}

		@Override
		public void windowClosed(TVW window) {
			
		}
	};
	
	public void run(String[] arguments)
	{
		SingleAppInstanceDocumentHandler mdiHelper = null;
		try
		{
			Display.setAppName(getTitle());
	
			viewWindowsManager = createViewWindowsManager();
			viewWindowsManager.setApplicationTitle(getTitle());
			viewWindowsManager.addListener(viewWindowsManagerListener);

			menuConstructor = createMenuConstructor(viewWindowsManager);
			menuConstructor.setExitGlobalHandler(exitHandler);
			menuConstructor.setAboutGlobalHandler(aboutHandler);
			menuConstructor.updateMenus(null);
			
			// Adding OS X system menu handlers
			if (SWT.getPlatform().equals("cocoa"))
			{
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
			
		} finally {
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
	
	@Override
	public void setListener(ApplicationListener listener) {
		this.listener = listener;
	}
}
