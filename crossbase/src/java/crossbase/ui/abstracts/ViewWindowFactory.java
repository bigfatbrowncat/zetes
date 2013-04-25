package crossbase.ui.abstracts;


public interface ViewWindowFactory<T extends ViewWindow>
{
	T create();
}
