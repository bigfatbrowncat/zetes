package crossbase.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuConstructor
{
	private SelectionAdapter openSelectionAdapter, exitSelectionAdapter, aboutSelectionAdapter;
	
	private void appendFileMenu(Display display, Shell shell)
	{
		Menu menu;
		
		if (shell != null) 
			menu = shell.getMenuBar();
		else if (display != null) 
			menu = display.getMenuBar();
		else 
			throw new RuntimeException("Strange case");
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&File");

		Menu fileMenu = new Menu(fileMenuItem);
		fileMenuItem.setMenu(fileMenu);

		// "Open" menu item
		MenuItem openMenuItem = new MenuItem(fileMenu, SWT.NONE);
		openMenuItem.addSelectionListener(openSelectionAdapter);
		
		HotKey openHotKey = new HotKey(HotKey.MOD1, 'O');
		openMenuItem.setText("&Open...\t" + openHotKey.toString());
		openMenuItem.setAccelerator(openHotKey.toAccelerator());

		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			new MenuItem(fileMenu, SWT.SEPARATOR);

			// "Exit" menu item
			MenuItem mainMenuItemExit = new MenuItem(fileMenu, SWT.NONE);
			mainMenuItemExit.addSelectionListener(exitSelectionAdapter);

			mainMenuItemExit.setText("E&xit");
		}
		
		// "Help" menu item
		MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("&Help");
		
		Menu helpMenu = new Menu(helpMenuItem);
		helpMenuItem.setMenu(helpMenu);

		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			// "About" menu item
			MenuItem aboutMenuItem = new MenuItem(helpMenu, SWT.NONE);
			aboutMenuItem.addSelectionListener(aboutSelectionAdapter);
			aboutMenuItem.setText("&About...");
		}
	}
	
	public void appendMenusToGlobalMenu()
	{
		appendFileMenu(Display.getDefault(), null);
	}
	public void appendMenusToShell(Shell shell)
	{
		appendFileMenu(null, shell);
	}

	public SelectionAdapter getOpenSelectionAdapter() {
		return openSelectionAdapter;
	}

	public void setOpenSelectionAdapter(SelectionAdapter openSelectionAdapter) {
		this.openSelectionAdapter = openSelectionAdapter;
	}

	public SelectionAdapter getExitSelectionAdapter() {
		return exitSelectionAdapter;
	}

	public void setExitSelectionAdapter(SelectionAdapter exitSelectionAdapter) {
		this.exitSelectionAdapter = exitSelectionAdapter;
	}

	public SelectionAdapter getAboutSelectionAdapter() {
		return aboutSelectionAdapter;
	}

	public void setAboutSelectionAdapter(SelectionAdapter aboutSelectionAdapter) {
		this.aboutSelectionAdapter = aboutSelectionAdapter;
	}
}
