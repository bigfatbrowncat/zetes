package tinyviewer;

import java.io.IOException;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;

import zetes.ui.ViewWindowsManagerBase;


public class ImageViewWindowsManager extends ViewWindowsManagerBase<ImageDocument, ImageViewWindow>
{
	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				for (int i = 0; i < fileList.length; i++)
				{
					ImageDocument document;
					try
					{
						document = new ImageDocument(fileList[i]);
						openWindowForDocument(document);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	@Override
	protected ImageViewWindow createViewWindow()
	{
		ImageViewWindow vw = new ImageViewWindow();
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}

	public DropTargetAdapter getViewWindowDropTargetAdapter()
	{
		return viewWindowDropTargetAdapter;
	}
}
