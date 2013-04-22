package crossbase.ui;

import org.eclipse.swt.dnd.DropTargetAdapter;

public interface DocumentWindow
{
	boolean isOccupied();
	void loadFile(String fileName);
	
	void addDropTargetListener(DropTargetAdapter dropTargetAdapter);
	void setClosedListener(DocumentWindowClosedListener documentWindowClosedListener);
}
