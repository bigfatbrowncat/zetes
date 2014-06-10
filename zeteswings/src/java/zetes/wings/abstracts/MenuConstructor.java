package zetes.wings.abstracts;

import zetes.wings.abstracts.ViewWindow;

public interface MenuConstructor<TVW extends ViewWindow<?>>
{
	/**
	 * This function should update menu for the specified window. 
	 * If no window is specified, it should update the global menu 
	 * (this feature is OS X only) 
	 * @param window the window to update the menu for
	 */
	void updateMenus(TVW window);
}
