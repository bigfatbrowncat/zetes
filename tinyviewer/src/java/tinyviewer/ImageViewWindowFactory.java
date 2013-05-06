package tinyviewer;

import org.eclipse.swt.dnd.DropTargetAdapter;

import crossbase.abstracts.ViewWindowFactory;
import crossbase.ui.ViewWindowsManager;

public class ImageViewWindowFactory implements ViewWindowFactory<ImageViewWindow>
{
	private TinyViewerApplication application;

	private DropTargetAdapter viewWindowDropTargetAdapter;
	private ViewWindowsManager<ImageViewWindow> viewWindowsManager;
	
	public ImageViewWindowFactory(TinyViewerApplication application)
	{
		this.application = application;
	}
	
	@Override
	public ImageViewWindow create()
	{
		ImageViewWindow vw = new ImageViewWindow(application, viewWindowsManager);
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
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

	public ViewWindowsManager<ImageViewWindow> getViewWindowsManager()
	{
		return viewWindowsManager;
	}

	public void setViewWindowsManager(ViewWindowsManager<ImageViewWindow> viewWindowsManager)
	{
		this.viewWindowsManager = viewWindowsManager;
	}
	
	
}
