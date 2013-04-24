package crossbase.ui;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.widgets.Shell;

public interface ViewWindow
{
	boolean isOccupied();
	void loadFile(String fileName);
	
	Shell getShell();
	String getDocumentTitle();
	
	void addDropTargetListener(DropTargetAdapter dropTargetAdapter);
	void setClosedListener(DocumentWindowClosedListener documentWindowClosedListener);
}
