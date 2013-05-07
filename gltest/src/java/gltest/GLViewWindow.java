package gltest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import crossbase.abstracts.MenuConstructor;
import crossbase.ui.ViewWindowBase;
import crossbase.ui.ViewWindowsManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TabFolder;

public class GLViewWindow extends ViewWindowBase<GLDocument>
{
	public GLViewWindow(
			String applicationTitle,
			ViewWindowsManager<GLDocument, ? extends ViewWindowBase<GLDocument>> windowsManager,
			MenuConstructor<GLDocument, ? extends ViewWindowBase<GLDocument>> menuConstructor)
	{
		super(applicationTitle, windowsManager, menuConstructor);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);

		Point size = shell.getSize();
		Point clientSize = new Point(shell.getClientArea().width, shell.getClientArea().height);
		
		shell.setSize(size.x - clientSize.x + 640, size.y - clientSize.y + 480);
		
		return shell;
	}
	
	@Override
	public boolean documentIsLoaded()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadDocument(GLDocument document)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public GLDocument getDocument()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsFullscreen()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
