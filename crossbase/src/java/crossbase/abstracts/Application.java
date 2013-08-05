package crossbase.abstracts;

import org.eclipse.swt.widgets.Shell;

import crossbase.ui.DefaultAboutBox;
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
	
	/**
	 * Behaviour controlling function. Should return constant. 
	 * @return If the overridden function returns true, when we start the application
	 * it creates a new empty view window (even on OS X). When the last view window
	 * (not necessarily the same) is closed, the application will close (yes, even on OS X too).
	 */
	boolean needsAtLeastOneView();
}
