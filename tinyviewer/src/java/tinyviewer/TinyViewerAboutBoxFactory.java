package tinyviewer;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import crossbase.ui.AboutBox;
import crossbase.ui.abstracts.AboutBoxFactory;

public class TinyViewerAboutBoxFactory implements AboutBoxFactory<AboutBox>
{

	@Override
	public AboutBox create(Shell shell)
	{
		AboutBox res = new AboutBox(shell);
		res.setApplicationName(Application.APP_NAME);
		res.setIconResourceName("/crossbase/icon.png");
		res.setDescriptionText("A simple image file viewer.\nThis application demonstrates the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2013, Ilya Mizus");
		res.setWindowSize(new Point(370, 180));
		return res;
	}

}
