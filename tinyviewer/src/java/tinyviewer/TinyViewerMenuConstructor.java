package tinyviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import crossbase.ui.HotKey;
import crossbase.ui.MenuConstructorBase;

public class TinyViewerMenuConstructor extends MenuConstructorBase<ImageDocument, ImageViewWindow>
{
	private SelectionAdapter fileOpenSelectionAdapter;
		
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

	public SelectionAdapter getFileOpenSelectionAdapter()
	{
		return fileOpenSelectionAdapter;
	}

	public void setFileOpenSelectionAdapter(SelectionAdapter fileOpenSelectionAdapter)
	{
		this.fileOpenSelectionAdapter = fileOpenSelectionAdapter;
	}

}
