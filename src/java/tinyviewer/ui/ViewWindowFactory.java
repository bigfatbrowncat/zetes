package tinyviewer.ui;

import crossbase.ui.DocumentWindowFactory;
import crossbase.ui.MenuConstructor;

public class ViewWindowFactory implements DocumentWindowFactory<ImageViewWindow>
{
	private MenuConstructor menuConstructor;
	
	public ViewWindowFactory(MenuConstructor menuConstructor)
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
