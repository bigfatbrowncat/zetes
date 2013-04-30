package crossbase.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import crossbase.ui.abstracts.Document;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;
import crossbase.ui.abstracts.ViewWindowClosedListener;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ViewWindowsManager<T extends ViewWindow>
{
	private ArrayList<T> windows = new ArrayList<T>();
	private ViewWindowFactory<T> viewWindowFactory;
	private MenuConstructor menuConstructor;

	private ViewWindowClosedListener viewWindowClosedListener = new ViewWindowClosedListener()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void windowClosed(ViewWindow viewWindow)
		{
			closeWindow((T)viewWindow);
			viewWindow.removeClosedListener(viewWindowClosedListener);
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
	 * Closes every window. After all windows are closed,
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
	public T openNewWindow(Document document)
	{
		T newWindow = viewWindowFactory.create();
		newWindow.setMenuConstructor(menuConstructor);
		newWindow.addClosedListener(viewWindowClosedListener);
		newWindow.open();
		
		if (document != null)
		{
			newWindow.loadDocument(document);
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
	public T openViewForDocument(Document document)
	{
		if (document == null) throw new IllegalArgumentException("file name shouldn't be null");
		
		// Searching for an empty window
		for (T vw : windows)
		{
			if (!vw.documentIsLoaded())
			{
				vw.loadDocument(document);
				return vw;
			}
		}
		
		// If we haven't found an empty window, we open a new one
		return openNewWindow(document);
	}
	
	public Object[] openViewForDocuments(Document[] documents)
	{
		ArrayList<Object> res = new ArrayList<Object>();
		for (int i = 0; i < documents.length; i++)
		{
			res.add(openViewForDocument(documents[i]));
		}
		return res.toArray();
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

	public MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}

	public void setMenuConstructor(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
}
