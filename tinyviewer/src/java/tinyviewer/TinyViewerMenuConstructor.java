package tinyviewer;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import crossbase.ui.HotKey;
import crossbase.ui.MenuConstructorBase;
import crossbase.ui.actions.Action;
import crossbase.ui.actions.Action.Handler;
import crossbase.ui.actions.ActionCategory;

public class TinyViewerMenuConstructor extends MenuConstructorBase<ImageDocument, ImageViewWindow>
{
	public static final int ACTION_FILE_OPEN = MenuConstructorBase.ACTION_FILE_CUSTOM + 1;
	
	private SelectionAdapter fileOpenSelectionAdapter;
	private Action<ImageDocument, ImageViewWindow> openAction;
	
	public TinyViewerMenuConstructor() {
		super();
		
		ActionCategory<ImageDocument, ImageViewWindow> fileActionCategory = (ActionCategory<ImageDocument, ImageViewWindow>)getActionsRoot().findActionByIdRecursively(ACTION_CATEGORY_FILE);

		openAction = new Action<>(ACTION_FILE_EXIT, "&Open");
		openAction.setHotKey(new HotKey(HotKey.MOD1, 'O'));
		fileActionCategory.addLastItem(openAction);
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
	public SelectionAdapter getFileOpenSelectionAdapter()
	{
		return fileOpenSelectionAdapter;
	}

	public void setFileOpenSelectionAdapter(SelectionAdapter fileOpenSelectionAdapter)
	{
		this.fileOpenSelectionAdapter = fileOpenSelectionAdapter;
		if (openAction.getHandlers().get(null) == null) {
			Handler h = new Handler();
			h.setListener(fileOpenSelectionAdapter);
			openAction.getHandlers().put(null, h);
		}
	}

}
