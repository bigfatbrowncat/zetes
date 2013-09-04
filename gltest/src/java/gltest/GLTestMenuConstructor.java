package gltest;

import zetes.abstracts.ViewWindowsManagerListener;
import zetes.ui.HotKey;
import zetes.ui.MenuConstructorBase;
import zetes.ui.actions.Action;
import zetes.ui.actions.ActionList;
import zetes.ui.actions.Separator;

public class GLTestMenuConstructor extends MenuConstructorBase<GLViewWindow> {
	private Action<GLViewWindow> viewModelCubeAction; 
	private Action<GLViewWindow> viewModelMonkeyAction; 
	private Action<GLViewWindow> viewModelMonkeySubdivAction; 
	
	private ViewWindowsManagerListener<GLViewWindow> viewWindowsManagerListener = new ViewWindowsManagerListener<GLViewWindow>() {
		
		@Override
		public void windowOpened(GLViewWindow window) {
			viewModelCubeAction.getHandlers().put(window, window.getViewCubeActionHandler());
			viewModelMonkeyAction.getHandlers().put(window, window.getViewMonkeyActionHandler());
			viewModelMonkeySubdivAction.getHandlers().put(window, window.getViewMonkeySubdivActionHandler());
			updateMenus(window);
		}
		
		@Override
		public void windowClosed(GLViewWindow window) {
			viewModelCubeAction.getHandlers().remove(window);
			viewModelMonkeyAction.getHandlers().remove(window);
			viewModelMonkeySubdivAction.getHandlers().remove(window);
		}
		
		@Override public void lastWindowClosed() {}
	};
	
	public GLTestMenuConstructor(GLViewWindowsManager viewWindowsManager) {
		super(viewWindowsManager);
		viewWindowsManager.addListener(viewWindowsManagerListener);
		
		ActionList<GLViewWindow> viewModelActionList = new ActionList<>();

		viewModelCubeAction = new Action<>("Cube");
		viewModelCubeAction.setHotKey(new HotKey(0, '1'));
		viewModelActionList.addLastItem(viewModelCubeAction);

		viewModelMonkeyAction = new Action<>("Monkey simple");
		viewModelMonkeyAction.setHotKey(new HotKey(0, '2'));
		viewModelActionList.addLastItem(viewModelMonkeyAction);

		viewModelMonkeySubdivAction = new Action<>("Monkey subdivided");
		viewModelMonkeySubdivAction.setHotKey(new HotKey(0, '3'));
		viewModelActionList.addLastItem(viewModelMonkeySubdivAction);
		
		getViewActionCategory().addFirstItem(new Separator<GLViewWindow>());
		getViewActionCategory().addFirstItem(viewModelActionList);
	}

	public Action<GLViewWindow> getViewModelCubeAction() {
		return viewModelCubeAction;
	}

	public Action<GLViewWindow> getViewModelMonkeyAction() {
		return viewModelMonkeyAction;
	}

	public Action<GLViewWindow> getViewModelMonkeySubdivAction() {
		return viewModelMonkeySubdivAction;
	}
}
