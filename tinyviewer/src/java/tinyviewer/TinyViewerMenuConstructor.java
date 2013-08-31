package tinyviewer;

import crossbase.ui.HotKey;
import crossbase.ui.MenuConstructorBase;
import crossbase.ui.actions.Action;
import crossbase.ui.actions.ActionList;
import crossbase.ui.actions.Handler;

public class TinyViewerMenuConstructor extends MenuConstructorBase<ImageViewWindow>
{
	public static final int ACTION_FILE_OPEN = MenuConstructorBase.ACTION_FILE_CUSTOM + 1;
	
	private Handler<ImageViewWindow> fileOpenHandler;
	private Action<ImageViewWindow> openAction;
	
	public TinyViewerMenuConstructor() {
		super();
		
		ActionList<ImageViewWindow> fileActionCategory = (ActionList<ImageViewWindow>)getActionsRoot().findActionByIdRecursively(ACTION_CATEGORY_FILE);

		openAction = new Action<>(ACTION_FILE_OPEN, "&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		fileActionCategory.addFirstItem(openAction);
	}
	
/*		
	@Override
	protected void appendCustomFileMenuItems(Menu fileMenu)
	{
		// "Open" menu item
		MenuItem openMenuItem = new MenuItem(fileMenu, SWT.NONE);
		openMenuItem.addSelectionListener(fileOpenSelectionAdapter);
		
		HotKey openHotKey = new HotKey(HotKey.MOD1, 'O');
		openMenuItem.setText("&Open...\t" + openHotKey.toString());
		openMenuItem.setAccelerator(openHotKey.toAccelerator());
	}
*/
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
