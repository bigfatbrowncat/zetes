package crossbase.ui.abstracts;

import org.eclipse.swt.widgets.Shell;


public interface ViewWindow
{
	boolean isOccupied();
	void loadFile(String fileName);
	
	Shell getShell();
	String getDocumentTitle();
	
	void setClosedListener(ViewWindowClosedListener<? extends ViewWindow> documentWindowClosedListener);
}
