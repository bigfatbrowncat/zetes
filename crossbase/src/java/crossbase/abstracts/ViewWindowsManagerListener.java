package crossbase.abstracts;

public interface ViewWindowsManagerListener<TVW extends ViewWindow<?>>
{
	void lastWindowClosed();
	void windowOpened(TVW window);
	void windowClosed(TVW window);
}
