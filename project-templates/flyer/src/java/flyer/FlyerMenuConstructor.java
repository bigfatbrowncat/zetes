package flyer;

import zetes.wings.HotKey;
import zetes.wings.base.MenuConstructorBase;
import zetes.wings.actions.Action;
import zetes.wings.actions.Handler;

public class FlyerMenuConstructor extends MenuConstructorBase<FlyerViewWindow>
{
	private Handler<FlyerViewWindow> fileOpenHandler;
	private Action<FlyerViewWindow> openAction;
	
	public FlyerMenuConstructor(FlyerViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		
		openAction = new Action<>("&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		getFileActionCategory().addFirstItem(openAction);
	}
	
	public Handler<FlyerViewWindow> getFileOpenHandler()
	{
		return fileOpenHandler;
	}

	public void setFileOpenHandler(Handler<FlyerViewWindow> fileOpenHandler)
	{
		this.fileOpenHandler = fileOpenHandler;
		if (openAction.getHandlers().get(null) == null) {

			openAction.getHandlers().put(null, fileOpenHandler);
		}
	}

}
