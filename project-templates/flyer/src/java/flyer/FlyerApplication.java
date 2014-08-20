package flyer;

import java.io.IOException;

import org.eclipse.swt.graphics.Point;

import zetes.wings.DefaultAboutBox;
import zetes.wings.base.ApplicationBase;


public class FlyerApplication extends ApplicationBase<DefaultAboutBox, FlyerDocument, FlyerViewWindow, FlyerMenuConstructor, FlyerViewWindowsManager>
{
	@Override
	public String getTitle()
	{
		return "Flyer";
	}

	@Override
	public DefaultAboutBox createAboutBox(FlyerViewWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/flyer/flyer64.png");
		res.setDescriptionText("A simple ZetesWings demo application.\nUse it as a template for your works");
		res.setCopyrightText("Copyright \u00a9 2014, John Smith");
		res.setWindowSize(new Point(370, 180));
		return res;
	}
	
	@Override
	public FlyerDocument loadFromFile(String fileName)
	{
		try
		{
			return new FlyerDocument(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public FlyerViewWindowsManager createViewWindowsManager()
	{
		return new FlyerViewWindowsManager();
	}

	@Override
	public FlyerMenuConstructor createMenuConstructor(FlyerViewWindowsManager viewWindowsManager)
	{
		FlyerMenuConstructor menuConstructor = new FlyerMenuConstructor(viewWindowsManager);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}

	public static void main(String... args)
	{
		new FlyerApplication().run(args);
	}
}
