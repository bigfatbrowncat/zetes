package crossbase.abstracts;

import org.eclipse.swt.events.SelectionAdapter;

import crossbase.abstracts.ViewWindow;

public interface MenuConstructor<TD extends Document, TVW extends ViewWindow<TD>>
{
	void addWindow(TVW shell);
	void removeWindow(TVW shell);

	void setExitSelectionAdapter(SelectionAdapter exitSelectionAdapter);
	void setAboutSelectionAdapter(SelectionAdapter aboutSelectionAdapter);

	void updateMenus();
}
