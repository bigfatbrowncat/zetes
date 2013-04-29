package crossbase.ui.abstracts;

import org.eclipse.swt.widgets.Shell;


public interface ViewWindow
{
	boolean documentIsLoaded();
	void loadDocument(Document document);
	
	Shell getShell();
	Document getDocument();
	
	void setClosedListener(ViewWindowClosedListener documentWindowClosedListener);
	void setMenuConstructor(MenuConstructor menuConstructor);
	
	void open();
	
	boolean supportsFullscreen();
}
