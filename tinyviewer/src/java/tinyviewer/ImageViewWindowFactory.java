package tinyviewer;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;

import crossbase.ui.ViewWindowsManager;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ImageViewWindowFactory implements ViewWindowFactory<ImageViewWindow>
{
	private DropTargetAdapter viewWindowDropTargetAdapter;
	
	public ImageViewWindowFactory()
	{
	}
	
	@Override
	public ImageViewWindow create()
	{
		ImageViewWindow vw = new ImageViewWindow();
		//vw.open();
		//vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}

	public DropTargetAdapter getViewWindowDropTargetAdapter()
	{
		return viewWindowDropTargetAdapter;
	}

	public void setViewWindowDropTargetAdapter(
			DropTargetAdapter viewWindowDropTargetAdapter)
	{
		this.viewWindowDropTargetAdapter = viewWindowDropTargetAdapter;
	}
	
	
}
