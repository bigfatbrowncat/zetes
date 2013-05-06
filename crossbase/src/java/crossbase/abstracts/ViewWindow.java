package crossbase.abstracts;

import org.eclipse.swt.widgets.Menu;

import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;

public interface ViewWindow
{
	boolean documentIsLoaded();
	void loadDocument(Document document);
	
	Menu getMenu();
	Document getDocument();
	
	boolean isActive();
	void activate(boolean force);
	
	void toggleMinimized();
	void toggleMaximized();
	void toggleFullScreen();
	
	void setMenuConstructor(MenuConstructor menuConstructor);
	
	void open();
	
	boolean supportsFullscreen();
}
