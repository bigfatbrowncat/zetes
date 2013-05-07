package crossbase.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;

public abstract class ViewWindowsManager<TD extends Document, TVW extends ViewWindow<TD>>
{
	private ArrayList<TVW> windows = new ArrayList<TVW>();
	private MenuConstructor<TD, TVW> menuConstructor;

	/**
	 * Closes the window. If no windows remain opened 
	 * and we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	public void closeWindow(TVW viewWindow)
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
	
	protected abstract TVW createViewWindow();
	
	/**
	 * Opens a new window. If <code>fileName</code> argument isn't null, opens
	 * the selected file in that window. Otherwise it opens an empty window.
	 * @param fileName The file's name to open in the new window (can be null)
	 * @return The opened window
	 */
	public TVW openNewWindow(TD document)
	{
		TVW newWindow = createViewWindow();
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
	public TVW openViewForDocument(TD document)
	{
		if (document == null) throw new IllegalArgumentException("file name shouldn't be null");
		
		// Searching for an empty window
		for (TVW vw : windows)
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
	
	public Object[] openViewForDocuments(TD[] documents)
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
	
	public MenuConstructor<TD, TVW> getMenuConstructor()
	{
		return menuConstructor;
	}
	
	public void setMenuConstructor(MenuConstructor<TD, TVW> menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
}
