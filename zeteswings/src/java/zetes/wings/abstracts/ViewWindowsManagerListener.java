package zetes.wings.abstracts;

import zetes.wings.abstracts.ViewWindow;

public interface ViewWindowsManagerListener<TVW extends ViewWindow<?>>
{
	void windowOpened(TVW window);
	void windowClosed(TVW window);
	void lastWindowClosed();
}
