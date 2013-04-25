package crossbase.ui.abstracts;


public interface MenuConstructor
{
	void addWindow(ViewWindow shell);
	void removeWindow(ViewWindow shell);
	void updateMenus();
}
