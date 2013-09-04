package zetes.abstracts;

import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Menu;

import zetes.abstracts.Document;

public interface ViewWindow<TD extends Document>
{
	Menu getMenu();
	
	boolean isActive();
	void activate(boolean force);
	
	void toggleMinimized();
	void toggleMaximized();
	void toggleFullScreen();
	
	void open();
	
	boolean supportsFullscreen();
	boolean supportsMaximizing();
	
	void addShellListener(ShellListener shellListener);

	TD getDocument();
	void setDocument(TD document);

	/**
	 * <p>Title suffix is a part of the window title after "&#151;" (em dash).
	 * In most cases it should be equal to the application title.</p>
	 * 
	 * <p>When the window doesn't provide any special document (for example it's
	 * an empty window which appears in Windows after the application has launched),
	 * the whole title is equal to {@code titleSuffix} value</p>
	 * 
	 * @param titleSuffix the new value for the {@code titleSuffix} variable.
	 */
	void setTitleSuffix(String titleSuffix); 
	
	String getWindowTitle();
	String getViewTitle();
}
