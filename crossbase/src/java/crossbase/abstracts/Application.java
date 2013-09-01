package crossbase.abstracts;

import crossbase.ui.ViewWindowsManager;

public interface Application<TAB extends AboutBox,
                             TD extends Document, 
                             TVW extends ViewWindow<TD>, 
                             TMC extends MenuConstructor<TVW>,
                             TVWM extends ViewWindowsManager<TD, TVW>>
{
	String getTitle();
	
	TAB createAboutBox(TVW parentWindow);
	
	TVWM createViewWindowsManager();
	
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
	 * (not necessarily the same) is closed, the application will be terminated (yes, even on OS X).
	 */
	boolean needsAtLeastOneView();
}
