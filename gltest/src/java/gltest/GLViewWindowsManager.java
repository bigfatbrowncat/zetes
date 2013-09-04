package gltest;

import zetes.NullDocument;
import zetes.ui.ViewWindowsManagerBase;

public class GLViewWindowsManager extends ViewWindowsManagerBase<NullDocument, GLViewWindow>
{
	@Override
	protected GLViewWindow createViewWindow()
	{
		return new GLViewWindow();
	}
}
