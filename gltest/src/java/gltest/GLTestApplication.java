package gltest;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import crossbase.ApplicationBase;
import crossbase.abstracts.MenuConstructor;
import crossbase.ui.DefaultAboutBox;
import crossbase.ui.MenuConstructorBase;
import crossbase.ui.ViewWindowsManager;

public class GLTestApplication extends ApplicationBase<DefaultAboutBox, GLDocument, GLViewWindow, MenuConstructor<GLDocument, GLViewWindow>>
{
	private GLViewWindow glViewWindow;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final GLTestApplication app = new GLTestApplication();
		
		app.run(args, new Runnable()
		{
			public void run()
			{
				app.glViewWindow = (GLViewWindow)app.getViewWindowsManager().openViewForDocument(new GLDocument());
			}
		});
	}

	@Override
	protected void onIdle()
	{
		glViewWindow.updateFrame();
	}
	
	@Override
	public String getTitle()
	{
		return "OpenGL Demo";
	}

	@Override
	public DefaultAboutBox createAboutBox(Shell parent)
	{
		DefaultAboutBox res = new DefaultAboutBox(parent);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/gltest/gltest64.png");
		res.setDescriptionText("A graphics demonstration which uses OpenGL canvas.\nThis application shows the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2013, Ilya Mizus");
		res.setWindowSize(new Point(410, 180));

		return res;
	}

	@Override
	public ViewWindowsManager<GLDocument, GLViewWindow> createViewWindowsManager()
	{
		return new GLViewWindowsManager(getTitle());
	}

	@Override
	public MenuConstructor<GLDocument, GLViewWindow> createMenuConstructor()
	{
		return new MenuConstructorBase<GLDocument, GLViewWindow>();
	}

	@Override
	public GLDocument loadFromFile(String fileName)
	{
		return null;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return true;
	}

}
