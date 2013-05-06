package tinyviewer;

import org.eclipse.swt.dnd.DropTargetAdapter;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ImageViewWindowFactory implements ViewWindowFactory<ImageViewWindow>
{
	private DropTargetAdapter viewWindowDropTargetAdapter;
	private TinyViewerApplication application;
	
	public ImageViewWindowFactory(TinyViewerApplication application)
	{
		this.application = application;
	}
	
	@Override
	public ImageViewWindow create()
	{
		ImageViewWindow vw = new ImageViewWindow(application);
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
	
	
}
