package zetes.wings.abstracts;

import zetes.wings.abstracts.Application;
import zetes.wings.abstracts.Document;
import zetes.wings.abstracts.ViewWindow;
import zetes.wings.abstracts.ViewWindowsManagerListener;

public interface ViewWindowsManager<TD extends Document, TVW extends ViewWindow<TD>> {

	void addListener(ViewWindowsManagerListener<TVW> listener);
	void removeListener(ViewWindowsManagerListener<TVW> listener);

	TVW openWindowForDocument(TD document);
	void closeWindow(TVW viewWindow);

	void closeAllWindows();
	void closeDocument(TD document);

	String getApplicationTitle();
	void setApplicationTitle(String applicationTitle);
	
	TVW getActiveWindow();
	
	/**
	 * This function should open an empty window if no windows are present.
	 * It's useful in Windows and Linux where a GUI application should
	 * have at least one window to show its menu. Additionally this feature
	 * is used by application's {@code needsAtLeastOneView()} feature
	 * 
	 * @see Application
	 */
	public void ensureThereIsOpenedWindow();
}
