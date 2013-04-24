package tinyviewer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import crossbase.ui.MenuConstructorBase;
import crossbase.ui.HotKey;

public class TinyViewerMenuConstructor extends MenuConstructorBase
{
	private SelectionAdapter openSelectionAdapter;
	
	@Override
	protected void appendCustomFileMenuItems(Menu fileMenu)
	{
		// "Open" menu item
		MenuItem openMenuItem = new MenuItem(fileMenu, SWT.NONE);
		openMenuItem.addSelectionListener(openSelectionAdapter);
		
		HotKey openHotKey = new HotKey(HotKey.MOD1, 'O');
		openMenuItem.setText("&Open...\t" + openHotKey.toString());
		openMenuItem.setAccelerator(openHotKey.toAccelerator());
	}
	
	public SelectionAdapter getOpenSelectionAdapter()
	{
		return openSelectionAdapter;
	}

	public void setOpenSelectionAdapter(SelectionAdapter openSelectionAdapter)
	{
		this.openSelectionAdapter = openSelectionAdapter;
	}
}
