package crossbase.ui;

public interface MenuConstructor
{
	void addWindow(ViewWindow shell);
	void removeWindow(ViewWindow shell);
	void updateMenus();
}
