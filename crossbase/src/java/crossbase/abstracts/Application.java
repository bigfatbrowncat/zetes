package crossbase.abstracts;

public interface Application<TAB extends AboutBox,
                             TD extends Document, 
                             TVW extends ViewWindow<TD>, 
                             TMC extends MenuConstructor<TVW>,
                             TVWM extends ViewWindowsManager<TD, TVW>>
{
	/**
	 * @return the application title which will be printed in view windows titles,
	 * on title bar and so on.
	 */
	String getTitle();
	
	/**
	 * <p>This function should be defined in descendant class in order to provide about
	 * box window. The implementation should create and return an about box.</p>
	 * @param parentWindow in platforms other than OS X the about box is modal and it's being shown up
	 * upon a window
	 * @return a new about box
	 */
	TAB createAboutBox(TVW parentWindow);
	
	/**
	 * <p>This function should be defined in descendant class in order to provide a specific 
	 * view windows manager for the application.</p>
	 * @return a new {@link ViewWindowsManager} interface implementation
	 */
	TVWM createViewWindowsManager();
	
	/**
	 * <p>This function should be defined in descendant class in order to provide a specific
	 * menu constructor for the application.</p>
	 * @return a new {@link MenuConstructor} interface implementation
	 */
	TMC createMenuConstructor();
	
	/**
	 * This function creates a document object from a file.
	 * @param fileName The name of the file to create a document object from
	 * @return The new {@link Document} object. 
	 * It can be null if the object can't be created from that particular file or 
	 * if it can't be created from a file at all. 
	 */
	TD loadFromFile(String fileName);
	
	/**
	 * Behaviour controlling function. Should return constant. 
	 * @return If the overridden function returns true, then after the application is started
	 * it creates a new empty view window (even on OS X). When the last view window
	 * (not necessarily the same) is closed, the application will be terminated (yes, even on OS X).
	 */
	boolean needsAtLeastOneView();
}
