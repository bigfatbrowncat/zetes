package tinyviewer.ui;

import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ImageViewWindowFactory implements ViewWindowFactory<ImageViewWindow>
{
	private MenuConstructor menuConstructor;
	
	public ImageViewWindowFactory(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	@Override
	public ImageViewWindow create()
	{
		ImageViewWindow vw = new ImageViewWindow();
		vw.open(menuConstructor);
		return vw;
	}
}
