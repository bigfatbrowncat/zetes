package gltest;

import org.eclipse.swt.widgets.Shell;

import crossbase.ApplicationBase;
import crossbase.abstracts.MenuConstructor;
import crossbase.ui.DefaultAboutBox;
import crossbase.ui.MenuConstructorBase;
import crossbase.ui.ViewWindowsManager;

public class GLTestApplication extends ApplicationBase<DefaultAboutBox, GLDocument, GLViewWindow, MenuConstructor<GLDocument, GLViewWindow>>
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new GLTestApplication().run(args);
	}

	@Override
	public String getTitle()
	{
		return "GL Test";
	}

	@Override
	public DefaultAboutBox createAboutBox(Shell parent)
	{
		DefaultAboutBox defaultAboutBox = new DefaultAboutBox(parent);
		return defaultAboutBox;
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
		// TODO Auto-generated method stub
		return null;
	}

}
