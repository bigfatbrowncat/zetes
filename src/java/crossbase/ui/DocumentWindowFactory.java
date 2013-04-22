package crossbase.ui;

public interface DocumentWindowFactory<T extends DocumentWindow>
{
	T create();
}
