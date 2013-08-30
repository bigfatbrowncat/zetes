package crossbase.abstracts;

import crossbase.ui.actions.ActionList;

public interface MenuConstructor<TVW extends ViewWindow<?>>
{
	public ActionList<TVW> getActionsRoot();
	/**
	 * This function should update menus for the specified window. 
	 * If no window is specified, it should update the global menu 
	 * (this feature is OS X only) 
	 * @param window the window to update the menu for
	 */
	void updateMenus(TVW window);
}
