package crossbase.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import crossbase.ui.abstracts.ViewWindow;
import crossbase.ui.abstracts.ViewWindowClosedListener;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ViewWindowsManager<T extends ViewWindow>
{
	private ArrayList<T> windows = new ArrayList<T>();
	private ViewWindowFactory<T> viewWindowFactory;

	private ViewWindowClosedListener<T> viewWindowClosedListener = new ViewWindowClosedListener<T>()
	{
		@Override
		public void windowClosed(T viewWindow)
		{
			closeWindow(viewWindow);
		}
	};

	/**
	 * Closes the window. If no windows remain opened 
	 * and we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	public void closeWindow(T viewWindow)
	{
		windows.remove(viewWindow);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			if (windows.size() == 0 && Display.getDefault() != null && !Display.getDefault().isDisposed())
			{
				Display.getDefault().dispose();
			}
		}
	}
	
	/**
	 * Closes every window. After all windows are closed 
	 * if we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	public void closeAllWindows()
	{
		while (windows.size() > 0)
		{
			closeWindow(windows.get(0));
		}
	}
	
	/**
	 * Opens a new window. If <code>fileName</code> argument isn't null, opens
	 * the selected file in that window. Otherwise it opens an empty window.
	 * @param fileName The file's name to open in the new window (can be null)
	 * @return The opened window
	 */
	public T openNewWindow(String fileName)
	{
		T newWindow = viewWindowFactory.create();
		
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
	public T openFile(String fileName)
	{
		if (fileName == null) throw new IllegalArgumentException("file name shouldn't be null");
		
		// Searching for an empty window
		for (T vw : windows)
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
	
	public ViewWindowsManager()
	{
	}
	
	public void setViewWindowFactory(ViewWindowFactory<T> viewWindowFactory)
	{
		this.viewWindowFactory = viewWindowFactory;
	}
	
	public ViewWindowFactory<T> getViewWindowFactory()
	{
		return viewWindowFactory;
	}
}
