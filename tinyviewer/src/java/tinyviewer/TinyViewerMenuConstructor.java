package tinyviewer;

import zetes.ui.HotKey;
import zetes.ui.MenuConstructorBase;
import zetes.ui.actions.Action;
import zetes.ui.actions.Handler;

public class TinyViewerMenuConstructor extends MenuConstructorBase<ImageViewWindow>
{
	private Handler<ImageViewWindow> fileOpenHandler;
	private Action<ImageViewWindow> openAction;
	
	public TinyViewerMenuConstructor(ImageViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		
		openAction = new Action<>("&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		getFileActionCategory().addFirstItem(openAction);
	}
	
	public Handler<ImageViewWindow> getFileOpenHandler()
	{
		return fileOpenHandler;
	}

	public void setFileOpenHandler(Handler<ImageViewWindow> fileOpenHandler)
	{
		this.fileOpenHandler = fileOpenHandler;
		if (openAction.getHandlers().get(null) == null) {

			openAction.getHandlers().put(null, fileOpenHandler);
		}
	}

}
