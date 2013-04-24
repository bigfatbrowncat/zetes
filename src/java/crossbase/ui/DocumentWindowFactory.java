package crossbase.ui;

public interface DocumentWindowFactory<T extends ViewWindow>
{
	T create();
}
