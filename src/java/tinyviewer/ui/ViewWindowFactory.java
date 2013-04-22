package tinyviewer.ui;

import crossbase.ui.DocumentWindowFactory;
import crossbase.ui.MenuConstructor;

public class ViewWindowFactory implements DocumentWindowFactory<ViewWindow>
{
	private MenuConstructor menuConstructor;
	
	public ViewWindowFactory(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	@Override
	public ViewWindow create()
	{
		ViewWindow vw = new ViewWindow();
		vw.open(menuConstructor);
		return vw;
	}
}
