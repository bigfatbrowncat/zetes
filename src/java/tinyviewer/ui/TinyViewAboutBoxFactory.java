package tinyviewer.ui;

import org.eclipse.swt.widgets.Shell;

import crossbase.ui.AboutBox;
import crossbase.ui.abstracts.AboutBoxFactory;

public class TinyViewAboutBoxFactory implements AboutBoxFactory<AboutBox>
{

	@Override
	public AboutBox create(Shell shell)
	{
		AboutBox res = new AboutBox(shell);
		res.setApplicationName(TinyViewerApplication.APP_NAME);
		res.setIconResourceName("/crossbase/icon.png");
		res.setDescriptionText("This application demonstrates the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 Ilya Mizus, 2013");
		return res;
	}

}
