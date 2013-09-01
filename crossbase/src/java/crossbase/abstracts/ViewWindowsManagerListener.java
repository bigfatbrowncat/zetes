package crossbase.abstracts;

public interface ViewWindowsManagerListener<TVW extends ViewWindow<?>>
{
	void windowOpened(TVW window);
	void windowClosed(TVW window);
	void lastWindowClosed();
}
