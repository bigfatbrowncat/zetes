package crossbase.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Display;

public class DocumentWindowsManager<T extends ViewWindow>
{
	private ArrayList<ViewWindow> windows = new ArrayList<ViewWindow>();
	private DocumentWindowFactory<T> documentWindowFactory;

	private DocumentWindowClosedListener viewWindowClosedListener = new DocumentWindowClosedListener()
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

	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				if (fileList.length > 0)
				{
					openFile(fileList[0]);
				}
			}
		}
	};
	
	/**
	 * Opens a new window. If <code>fileName</code> argument isn't null, opens
	 * the selected file in that window. Otherwise it opens an empty window.
	 * @param fileName The file's name to open in the new window (can be null)
	 * @return The opened window
	 */
	public ViewWindow openNewWindow(String fileName)
	{
		/*DocumentWindow newWindow = new ViewWindow();
		newWindow.open(menuConstructor);*/
		
		T newWindow = documentWindowFactory.create();
		
		newWindow.addDropTargetListener(viewWindowDropTargetAdapter);
		newWindow.setClosedListener(viewWindowClosedListener);
		
		if (fileName != null)
		{
			newWindow.loadFile(fileName);
		}
		windows.add(newWindow);
		return newWindow;
	}
	
	/**
	 * Opens a file. If there's empty window, opens the file in it. 
	 * If there's no, empty windows, opens a new one.
	 * @param fileName The file's name to open
	 * @return The window where file is opened
	 */
	public ViewWindow openFile(String fileName)
	{
		if (fileName == null) throw new IllegalArgumentException("file name shouldn't be null");
		// Searching for an empty window
		for (ViewWindow vw : windows)
		{
			if (!vw.isOccupied())
			{
				vw.loadFile(fileName);
				return vw;
			}
		}
		
		// If we haven't found an empty window, we open a new one
		return openNewWindow(fileName);
	}
	
	/**
	 * This function opens an empty window if no windows are present.
	 * It's useful in Windows and Linux where a GUI application should
	 * have at least one window to show its menu
	 */
	public void ensureThereIsOpenedWindow()
	{
		if (windows.size() == 0)
		{
			openNewWindow(null);
		}
	}
	
	public DocumentWindowsManager(DocumentWindowFactory<T> documentWindowFactory)
	{
		this.documentWindowFactory = documentWindowFactory;
	}
}
