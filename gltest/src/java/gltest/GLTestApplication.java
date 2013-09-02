package gltest;

import org.eclipse.swt.graphics.Point;

import crossbase.ApplicationBase;
import crossbase.NullDocument;
import crossbase.ui.DefaultAboutBox;
import crossbase.ui.MenuConstructorBase;

public class GLTestApplication extends ApplicationBase<DefaultAboutBox, NullDocument, GLViewWindow, MenuConstructorBase<GLViewWindow>, GLViewWindowsManager>
{
	private GLViewWindow glViewWindow;
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		final GLTestApplication app = new GLTestApplication();
		
		app.run(args);
	}

	public void setViewWindow(GLViewWindow window)
	{
		this.glViewWindow = window;
	}
	
	@Override
	protected void onIdle()
	{
		if (glViewWindow == null)
		{
			if (getViewWindowsManager().getViewsForDocument(null).size() > 0)
			{
				glViewWindow = getViewWindowsManager().getViewsForDocument(null).get(0);
			}
		}
		glViewWindow.updateFrame();

	}
	
	@Override
	public String getTitle()
	{
		return "OpenGL Demo";
	}

	@Override
	public DefaultAboutBox createAboutBox(GLViewWindow parentWindow)
	{
		DefaultAboutBox res = new DefaultAboutBox(parentWindow);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/gltest/wingcube64.png");
		res.setDescriptionText("A graphics demonstration which uses OpenGL canvas.\nThis application shows the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2013, Ilya Mizus");
		res.setWindowSize(new Point(410, 180));

		return res;
	}

	@Override
	public GLViewWindowsManager createViewWindowsManager()
	{
		return new GLViewWindowsManager();
	}

	@Override
	public MenuConstructorBase<GLViewWindow> createMenuConstructor()
	{
		return new MenuConstructorBase<GLViewWindow>();
	}

	@Override
	public NullDocument loadFromFile(String fileName)
	{
		return null;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return true;
	}

}
