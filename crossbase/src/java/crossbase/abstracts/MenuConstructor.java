package crossbase.abstracts;

import crossbase.abstracts.ViewWindow;


public interface MenuConstructor
{
	void addWindow(ViewWindow shell);
	void removeWindow(ViewWindow shell);
	void updateMenus();
}
