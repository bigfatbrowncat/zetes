package gltest;

import crossbase.NullDocument;
import crossbase.ui.ViewWindowsManagerBase;

public class GLViewWindowsManager extends ViewWindowsManagerBase<NullDocument, GLViewWindow>
{
	@Override
	protected GLViewWindow createViewWindow()
	{
		return new GLViewWindow();
	}
}
