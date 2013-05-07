package crossbase.abstracts;

import org.eclipse.swt.widgets.Shell;

import crossbase.ui.AboutBox;
import crossbase.ui.ViewWindowsManager;

public interface Application<TAB extends AboutBox, TD extends Document, TVW extends ViewWindow<TD>, TMC extends MenuConstructor<TD, ? extends ViewWindow<TD>>>
{
	String getTitle();
	
	TAB createAboutBox(Shell parent);
	
	ViewWindowsManager<TD, TVW> createViewWindowsManager();
	
	TMC createMenuConstructor();
	
	/**
	 * This function creates a document object from a file.
	 * @param fileName The name of the file to create a document object from
	 * @return The new object. 
	 * It can be null if the object can't be created from that particular file or 
	 * if it can't be created from a file at all. 
	 */
	TD loadFromFile(String fileName);
}
