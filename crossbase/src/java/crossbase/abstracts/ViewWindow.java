package crossbase.abstracts;

import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Menu;

public interface ViewWindow<TD extends Document>
{
	void setDocument(TD document);
	
	Menu getMenu();
	TD getDocument();
	
	boolean isActive();
	void activate(boolean force);
	
	void toggleMinimized();
	void toggleMaximized();
	void toggleFullScreen();
	
	void open();
	
	boolean supportsFullscreen();
	boolean supportsMaximizing();
	
	void addShellListener(ShellListener shellListener);
	
	String getTitle();
}
