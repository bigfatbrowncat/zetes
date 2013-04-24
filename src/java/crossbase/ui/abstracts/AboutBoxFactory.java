package crossbase.ui.abstracts;

import org.eclipse.swt.widgets.Shell;

import crossbase.ui.AboutBox;

public interface AboutBoxFactory<T extends AboutBox>
{
	T create(Shell shell);
}
