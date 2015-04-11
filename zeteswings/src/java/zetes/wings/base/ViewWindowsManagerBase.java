package zetes.wings.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

import zetes.wings.abstracts.Document;
import zetes.wings.abstracts.DocumentListener;
import zetes.wings.abstracts.ViewWindowsManager;
import zetes.wings.abstracts.ViewWindowsManagerListener;


public abstract class ViewWindowsManagerBase<TD extends Document, TVW extends ViewWindowBase<TD>> implements ViewWindowsManager<TD, TVW>
{
	private String applicationTitle;
	private HashMap<TD, ArrayList<TVW>> views = new HashMap<TD, ArrayList<TVW>>();
	private HashSet<ViewWindowsManagerListener<TVW>> listeners = new HashSet<ViewWindowsManagerListener<TVW>>();
	
	public String getApplicationTitle() {
		return applicationTitle;
	}

	public void setApplicationTitle(String applicationTitle) {
		this.applicationTitle = applicationTitle;
	}

	private DocumentListener documentListener = new DocumentListener() {
		
		@Override
		public void titleChanged(Document document) {
			for (TVW viewWindow : views.get(document)) {
				viewWindow.updateTitle();
			}
			
		}
	};
	
	private void addWindowForDocument(TD document, TVW window)
	{
		if (!views.containsKey(document))
		{
			views.put(document, new ArrayList<TVW>());
			if (document != null) document.addListener(documentListener);
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
			if (doc != null) doc.removeListener(documentListener);
		}
	}
	
	public TVW getActiveWindow() {
		for (TD doc : views.keySet()) {
			for (TVW view : views.get(doc)) {
				if (view.isActive()) return view;
			}
		}
		return null;
	}
	
	/**
	 * Closes the window. If no windows remain opened 
	 * and we are not in OS X, terminates the application.
	 * @param viewWindow The window to close
	 */
	@Override
	public void closeWindow(TVW viewWindow)
	{
		findAndRemoveWindow(viewWindow);
		
		callListenersWindowClosed(viewWindow);
		if (views.size() == 0)
		{
			callListenersLastWindowClosed();
		}
		
	}
	
	/**
	 * Closes all windows assigned to the document and
	 * forgets about that document
	 * @param document a document to close
	 */
	@Override
	public void closeDocument(TD document)
	{
		ArrayList<TVW> viewsForDoc = new ArrayList<TVW>(views.get(document));
		for (TVW view : viewsForDoc)
		{
			closeWindow(view);
		}
		
		views.remove(document);
	}
	
	/**
	 * Closes every window. After all windows are closed,
	 * if we are not in OS X, terminates the application.
	 */
	@Override
	public void closeAllWindows()
	{
		HashSet<TD> keys = new HashSet<TD>(views.keySet());
		for (TD doc : keys)
		{
			closeDocument(doc);
		}
	}
	
	/**
	 * This method should be overridden to create a new instance of {@link TVW} template
	 * parameter class, i.e. the new view window  
	 * @return a new view window
	 */
	protected abstract TVW createViewWindow();
	
	/**
	 * Opens a new window. If <code>fileName</code> argument isn't null, opens
	 * the selected file in that window. Otherwise it opens an empty window.
	 * @param fileName The file's name to open in the new window (can be null)
	 * @return The opened window
	 */
	protected TVW openNewWindow(TD document)
	{
		final TVW newWindow = createViewWindow();
		newWindow.setTitleSuffix(applicationTitle);
		
		newWindow.open();
		
		addWindowForDocument(document, newWindow);

		if (document != null)
		{
			newWindow.setDocument(document);
		}
		
		newWindow.addShellListener(new ShellListener() {
			
			@Override public void shellIconified(ShellEvent arg0) {}
			@Override public void shellDeiconified(ShellEvent arg0) {}
			@Override public void shellDeactivated(ShellEvent arg0) {}
			@Override public void shellActivated(ShellEvent arg0) {}
			
			@Override
			public void shellClosed(ShellEvent arg0) {
				closeWindow(newWindow);
			}
			
		});
		callListenersWindowOpened(newWindow);
		
		return newWindow;
	}
	
	/**
	 * Opens a file. If there's empty window, opens the file in it. 
	 * If there's no, empty windows, opens a new one.
	 * @param document The document to open. It shouldn't be null
	 * @return The window where the document is opened
	 */
	@Override
	public TVW openWindowForDocument(TD document)
	{
		if (document == null && views.containsKey(null)) throw new IllegalArgumentException("Document shouldn't be null");

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
		TVW vw = openNewWindow(document);
		return vw;
	}
	
	public Object[] openWindowsForDocuments(TD[] documents)
	{
		ArrayList<Object> res = new ArrayList<Object>();
		for (int i = 0; i < documents.length; i++)
		{
			res.add(openWindowForDocument(documents[i]));
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
	
	protected void callListenersLastWindowClosed()
	{
		for (ViewWindowsManagerListener<TVW> listener : listeners)
		{
			listener.lastWindowClosed();
		}
	}
	protected void callListenersWindowOpened(TVW window)
	{
		for (ViewWindowsManagerListener<TVW> listener : listeners)
		{
			listener.windowOpened(window);
		}
	}
	protected void callListenersWindowClosed(TVW window)
	{
		for (ViewWindowsManagerListener<TVW> listener : listeners)
		{
			listener.windowClosed(window);
		}
	}
	
	@Override
	public void addListener(ViewWindowsManagerListener<TVW> listener)
	{
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(ViewWindowsManagerListener<TVW> listener)
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
