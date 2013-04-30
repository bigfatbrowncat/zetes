package crossbase.ui.abstracts;

import org.eclipse.swt.widgets.Menu;

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
	
	void addClosedListener(ViewWindowClosedListener viewWindowClosedListener);
	void removeClosedListener(ViewWindowClosedListener viewWindowClosedListener);

	void addMinimizedListener(ViewWindowMinimizedListener viewWindowMinimizedListener);
	void removeMinimizedListener(ViewWindowMinimizedListener viewWindowMinimizedListener);
	
	void addMaximizedListener(ViewWindowMaximizedListener viewWindowMaximizedListener);
	void removeMaximizedListener(ViewWindowMaximizedListener viewWindowMaximizedListener);

	void setMenuConstructor(MenuConstructor menuConstructor);
	
	void open();
	
	boolean supportsFullscreen();
}
