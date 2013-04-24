package tinyviewer.ui;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;

import crossbase.ui.ViewWindowsManager;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindowFactory;

public class ImageViewWindowFactory implements ViewWindowFactory<ImageViewWindow>
{
	private MenuConstructor menuConstructor;
	private ViewWindowsManager<ImageViewWindow> imageViewWindowsManager;
	
	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				if (fileList.length > 0)
				{
					imageViewWindowsManager.openFile(fileList[0]);
				}
			}
		}
	};
	
	public ImageViewWindowFactory()
	{
	}
	
	public MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}

	public void setMenuConstructor(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}

	public ViewWindowsManager<ImageViewWindow> getImageViewWindowsManager()
	{
		return imageViewWindowsManager;
	}

	public void setImageViewWindowsManager(
			ViewWindowsManager<ImageViewWindow> imageViewWindowsManager)
	{
		this.imageViewWindowsManager = imageViewWindowsManager;
	}

	@Override
	public ImageViewWindow create()
	{
		ImageViewWindow vw = new ImageViewWindow();
		vw.open(menuConstructor);
		vw.addDropTargetListener(viewWindowDropTargetAdapter);
		return vw;
	}
	
	
}
