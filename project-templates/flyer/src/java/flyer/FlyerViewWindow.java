package flyer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.wings.base.ViewWindowBase;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class FlyerViewWindow extends ViewWindowBase<FlyerDocument>
{
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		shell.setSize(461, 320);

		shell.setMinimumSize(new Point(250, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(FlyerViewWindow.class, "/flyer/flyer512.png"),		// Necessary in OS X
				SWTResourceManager.getImage(FlyerViewWindow.class, "/flyer/flyer64.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(FlyerViewWindow.class, "/flyer/flyer16.png")		// Necessary in Windows (for taskbar)
		});
		shell.setLayout(new GridLayout(1, false));
		
		Label descriptionLabel = new Label(shell, SWT.NONE);
		descriptionLabel.setImage(SWTResourceManager.getImage(FlyerViewWindow.class, "/flyer/flyer64.png"));
		descriptionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));
		descriptionLabel.setAlignment(SWT.CENTER);
		
		Label lblThisIsA = new Label(shell, SWT.NONE);
		lblThisIsA.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1));
		lblThisIsA.setText("This is a view window. Use it wise.");
	
			
		return shell;
	}

	@Override
	public void setDocument(FlyerDocument document)
	{
		super.setDocument(document);
		getShell().forceActive();
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}

	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
