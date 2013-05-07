package tinyviewer;

import org.eclipse.swt.dnd.DropTargetAdapter;

import crossbase.ui.ViewWindowsManager;

public class ImageViewWindowsManager extends ViewWindowsManager<ImageDocument, ImageViewWindow>
{
	private String applicationTitle;
	private DropTargetAdapter viewWindowDropTargetAdapter;
	
	@Override
	protected ImageViewWindow createViewWindow()
	{
		ImageViewWindow vw = new ImageViewWindow(applicationTitle, this, (TinyViewerMenuConstructor)getMenuConstructor());
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}

	public DropTargetAdapter getViewWindowDropTargetAdapter()
	{
		return viewWindowDropTargetAdapter;
	}

	public ImageViewWindowsManager(String applicationTitle, DropTargetAdapter viewWindowDropTargetAdapter)
	{
		this.applicationTitle = applicationTitle;
		this.viewWindowDropTargetAdapter = viewWindowDropTargetAdapter;
	}
}
