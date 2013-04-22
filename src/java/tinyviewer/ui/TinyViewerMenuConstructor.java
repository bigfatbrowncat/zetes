package tinyviewer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import crossbase.ui.HotKey;
import crossbase.ui.MenuConstructor;

public class TinyViewerMenuConstructor extends MenuConstructor
{
	private SelectionAdapter openSelectionAdapter;
	
	@Override
	protected void appendCustomFileMenuItems(Menu fileMenu, Display display, Shell shell)
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
