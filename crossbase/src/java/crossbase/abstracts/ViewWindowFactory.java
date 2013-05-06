package crossbase.abstracts;

import crossbase.abstracts.ViewWindow;


public interface ViewWindowFactory<T extends ViewWindow>
{
	T create();
}
