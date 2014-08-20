package flyer;

import zetes.wings.base.ViewWindowsManagerBase;


public class FlyerViewWindowsManager extends ViewWindowsManagerBase<FlyerDocument, FlyerViewWindow>
{

	@Override
	protected FlyerViewWindow createViewWindow() {
		return new FlyerViewWindow();
	}

}
