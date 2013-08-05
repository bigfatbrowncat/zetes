package crossbase.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;
import crossbase.abstracts.ViewWindowsManagerListener;

public abstract class ViewWindowsManager<TD extends Document,
                                         TVW extends ViewWindow<TD>,
                                         TMC extends MenuConstructor<TD, TVW>>
{
	private HashMap<TD, ArrayList<TVW>> views = new HashMap<TD, ArrayList<TVW>>();
	private TMC menuConstructor;

	private HashSet<ViewWindowsManagerListener> listeners = new HashSet<ViewWindowsManagerListener>();
	
	private void addWindowForDocument(TD document, TVW window)
	{
		if (!views.containsKey(document))
		{
			views.put(document, new ArrayList<TVW>());
		}
		
		views.get(document).add(window);
	}
	
	private void findAndRemoveWindow(TVW window)
	{
		TD doc = window.getDocument();
		views.get(doc).remove(window);
		
		if (views.get(doc).size() == 0)
		{
			views.remove(doc);
		}
	}
	
	/**
	 * Closes the window. If no windows remain opened 
	 * and we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	public void closeWindow(TVW viewWindow)
	{
		findAndRemoveWindow(viewWindow);
		
		if (views.size() == 0)
		{
			callListenersLastWindowClosed();
		}
		
	}
	
	public void closeAllWindowsForDocument(TD document)
	{
		for (TVW view : views.get(document))
		{
			closeWindow(view);
		}
		
		views.remove(document);
	}
	
	/**
	 * Closes every window. After all windows are closed,
	 * if we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	public void closeAllWindows()
	{
		for (TD doc : views.keySet())
		{
			closeAllWindowsForDocument(doc);
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
			newWindow.setDocument(document);
		}
		
		addWindowForDocument(document, newWindow);
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
		if (views.containsKey(null))
		{
			if (views.get(null).size() > 0)
			{
				// Loading the document to the found empty window
				TVW vw = views.get(null).get(0);
				
				// Removing the window from empty windows list
				findAndRemoveWindow(vw);

				// Loading the new document for the window
				vw.setDocument(document);

				// Adding the window to the document windows list
				addWindowForDocument(document, vw);
				
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
		if (views.size() == 0)
		{
			openNewWindow(null);
		}
	}
	
	public MenuConstructor<TD, TVW> getMenuConstructor()
	{
		return menuConstructor;
	}
	
	public void setMenuConstructor(TMC menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	protected void callListenersLastWindowClosed()
	{
		for (ViewWindowsManagerListener listener : listeners)
		{
			listener.lastWindowClosed();
		}
	}
	
	public void addListener(ViewWindowsManagerListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeListener(ViewWindowsManagerListener listener)
	{
		listeners.remove(listener);
	}
	
	public List<TVW> getViewsForDocument(TD document)
	{
		if (views.get(document) != null)
		{
			return Collections.unmodifiableList(views.get(document));
		}
		else
		{
			return Collections.unmodifiableList(new ArrayList<TVW>());
		}
	}
}
