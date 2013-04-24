package crossbase.ui;

import org.eclipse.swt.widgets.Shell;

public interface MenuConstructor
{
	void addShell(Shell shell);
	void removeShell(Shell shell);
	void updateMenus();
	
/*	private SelectionAdapter exitSelectionAdapter, aboutSelectionAdapter;
	
	private void appendFileMenu(Menu menu, Display display, Shell shell)
	{
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("&File");

		Menu fileMenu = new Menu(fileMenuItem);
		fileMenuItem.setMenu(fileMenu);

		appendCustomFileMenuItems(fileMenu, display, shell);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			new MenuItem(fileMenu, SWT.SEPARATOR);

			// "Exit" menu item
			MenuItem mainMenuItemExit = new MenuItem(fileMenu, SWT.NONE);
			mainMenuItemExit.addSelectionListener(exitSelectionAdapter);

			mainMenuItemExit.setText("E&xit");
		}
		
	}

	private void appendHelpMenu(Menu menu, Display display, Shell shell)
	{
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
		Display display = Display.getDefault();
		Menu menu = display.getMenuBar();
		appendFileMenu(menu, display, null);
		appendHelpMenu(menu, display, null);
	}
	public void appendMenusToShell(Shell shell)
	{
		Menu menu = shell.getMenuBar();
		appendFileMenu(menu, null, shell);
		appendHelpMenu(menu, null, shell);
	}

	public SelectionAdapter getExitSelectionAdapter()
	{
		return exitSelectionAdapter;
	}

	public void setExitSelectionAdapter(SelectionAdapter exitSelectionAdapter)
	{
		this.exitSelectionAdapter = exitSelectionAdapter;
	}

	public SelectionAdapter getAboutSelectionAdapter()
	{
		return aboutSelectionAdapter;
	}

	public void setAboutSelectionAdapter(SelectionAdapter aboutSelectionAdapter)
	{
		this.aboutSelectionAdapter = aboutSelectionAdapter;
	}*/
}
