package crossbase;

import java.util.ArrayList;

import crossbase.SingleAppInstanceDocumentHandler.FileNamesSendingFailed;
import crossbase.ui.AboutBox;
import crossbase.ui.CocoaUIEnhancer;
import crossbase.ui.HotKey;
import crossbase.ui.ViewWindow;
import crossbase.ui.MenuConstructor;
import crossbase.ui.ViewWindowClosedListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class Application
{
	public static final String APP_NAME = "SWT Application";
	
	private static ArrayList<ViewWindow> windows = new ArrayList<ViewWindow>();
	private static MenuConstructor menuConstructor;
	private static AboutBox aboutBox = null;
	
	private static SelectionAdapter openSelectionAdapter = new SelectionAdapter()
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
				openFile(fileName);
			}
			dummyShell.dispose();
		}
	};
	
	private static SelectionAdapter exitSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Display.getDefault().dispose();
		}
	};
	
	private static SelectionAdapter aboutSelectionAdapter = new SelectionAdapter()
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
	
	private static SelectionAdapter preferencesSelectionAdapter = new SelectionAdapter()
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
	
	private static Listener openDocumentListener = new Listener() {
		
		@Override
		public void handleEvent(Event arg0) {
			String fileName = arg0.text;
			openFile(fileName);
		}
	};
	
	private static DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				if (fileList.length > 0)
				{
					System.out.println("drop");
					openFile(fileList[0]);
				}
			}
		}
	};
	
	private static ViewWindowClosedListener viewWindowClosedListener = new ViewWindowClosedListener()
	{
		@Override
		public void windowClosed(ViewWindow window)
		{
			windows.remove(window);
			
			if (!SWT.getPlatform().equals("cocoa"))
			{
				if (windows.size() == 0)
				{
					Display.getDefault().dispose();
				}
			}
		}
	};
	
	private static ViewWindow openNewWindow(String fileName)
	{
		System.out.println("opening new");
		ViewWindow newWindow = new ViewWindow();
		newWindow.open(menuConstructor);
		newWindow.addDropTargetListener(viewWindowDropTargetAdapter);
		newWindow.setClosedListener(viewWindowClosedListener);
		if (fileName != null)
		{
			newWindow.openImageFile(fileName);
		}
		windows.add(newWindow);
		return newWindow;
	}
	
	private static ViewWindow openFile(String fileName)
	{
		// Searching for an empty window
		for (ViewWindow vw : windows)
		{
			if (!vw.isOccupied())
			{
				vw.openImageFile(fileName);
				return vw;
			}
		}
		
		// If we haven't found an empty window, we open a new one
		return openNewWindow(fileName);
	}
	
	private static void eventLoop()
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
	
	
	
	public static void main(String... args) throws InterruptedException
	{
		Display.setAppName(APP_NAME);
		menuConstructor = new MenuConstructor();
		menuConstructor.setOpenSelectionAdapter(openSelectionAdapter);
		menuConstructor.setExitSelectionAdapter(exitSelectionAdapter);
		menuConstructor.setAboutSelectionAdapter(aboutSelectionAdapter);
		
		SingleAppInstanceDocumentHandler mdiHelper = null;
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			try
			{
				mdiHelper = new SingleAppInstanceDocumentHandler(args, openDocumentListener);
				if (!mdiHelper.isServer())
				{
					// In that case we have done our job and just exiting the "main"
					return;
				}

			}
			catch (FileNamesSendingFailed e)
			{
				e.printStackTrace();
				return;
			}
		}
		
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
			if (windows.size() == 0)
			{
				// Opening a new empty window -- we need it to show menus
				openNewWindow(null);
			}
		}
		
		eventLoop();
		
		if (SWT.getPlatform().equals("win32"))
		{
			mdiHelper.stop();
		}

		System.out.print("Bye!\n");
	}
}
