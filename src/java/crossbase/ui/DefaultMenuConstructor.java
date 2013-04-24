package crossbase.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class DefaultMenuConstructor implements MenuConstructor
{
	private SelectionAdapter exitSelectionAdapter, aboutSelectionAdapter;
	private Set<Shell> shells;
	private HashMap<Shell, Set<MenuItem>> shellMenuItems;
	private Set<MenuItem> globalMenuItems;
	
	/**
	 * Adds a menu to the storage
	 * @param shell The shell the menu connected to. If it's null, the global menu will be assumed 
	 * @param menu The menu
	 */
	protected void addShellMenu(Shell shell, MenuItem menuItem)
	{
		if (shell == null)
		{
			globalMenuItems.add(menuItem);
		}
		else
		{
			if (!shellMenuItems.containsKey(shell))
			{
				shellMenuItems.put(shell, new HashSet<MenuItem>());
			}
			shellMenuItems.get(shell).add(menuItem);
		}
	}
	
	/**
	 * Erases all menus and menu items for the selected shell. 
	 * If the shell is null, erases all menus form global menu. 
	 * @param shell Shell to erase menus from
	 */
	protected void eraseAllMenusForShell(Shell shell)
	{
		if (shell == null)
		{
			while (globalMenuItems.size() > 0)
			{
				for (MenuItem mi : globalMenuItems)
				{
					if (!mi.isDisposed()) mi.dispose();
				}
				globalMenuItems.clear();
			}
		}
		else
		{
			if (shellMenuItems.containsKey(shell))
			{
				while (shellMenuItems.get(shell).size() > 0)
				{
					for (MenuItem mi : shellMenuItems.get(shell))
					{
						if (!mi.isDisposed()) mi.dispose();
					}
					shellMenuItems.get(shell).clear();
				}				
			}
		}
	}
	
	private MenuItem createAndAppendFileMenu(Menu menu)
	{
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		
		fileMenuItem.setText("&File");

		Menu fileMenu = new Menu(fileMenuItem);
		fileMenuItem.setMenu(fileMenu);

		appendCustomFileMenuItems(fileMenu);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			if (fileMenu.getItemCount() > 0) 
			{
				// If the menu isn't empty yet, adding the new item
				new MenuItem(fileMenu, SWT.SEPARATOR);
			}

			// "Exit" menu item
			MenuItem mainMenuItemExit = new MenuItem(fileMenu, SWT.NONE);
			mainMenuItemExit.addSelectionListener(exitSelectionAdapter);

			mainMenuItemExit.setText("E&xit");
		}
		
		return fileMenuItem;		
	}

	protected void appendCustomFileMenuItems(Menu fileMenu)
	{
		// To be overridden in the inherited classes
	}
	
	private MenuItem createAndAppendHelpMenu(Menu menu)
	{
		// "Help" menu item
		MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
		helpMenuItem.setText("&Help");
		
		Menu helpMenu = new Menu(helpMenuItem);
		helpMenuItem.setMenu(helpMenu);

		appendCustomHelpMenuItems(helpMenu);
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			if (helpMenu.getItemCount() > 0) 
			{
				// If the menu isn't empty yet, adding the new item
				new MenuItem(helpMenu, SWT.SEPARATOR);
			}

			// "About" menu item
			MenuItem aboutMenuItem = new MenuItem(helpMenu, SWT.NONE);
			aboutMenuItem.addSelectionListener(aboutSelectionAdapter);
			aboutMenuItem.setText("&About...");
		}
		
		return helpMenuItem;
	}
	
	protected void appendCustomHelpMenuItems(Menu helpMenu)
	{
		// To be overridden in the inherited classes
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
	}

	protected void addMenusToGlobalMenu()
	{
		if (SWT.getPlatform().equals("cocoa"))
		{
			Display display = Display.getDefault();
			Menu menu = display.getMenuBar();
			addShellMenu(null, createAndAppendFileMenu(menu));
			addShellMenu(null, createAndAppendHelpMenu(menu));
		}
	}

	protected void addMenusToShell(Shell shell)
	{
		Menu menu = shell.getMenuBar();
		addShellMenu(shell, createAndAppendFileMenu(menu));
		addShellMenu(shell, createAndAppendHelpMenu(menu));
	}

	@Override
	public void updateMenus()
	{
		for (Shell shell : shells)
		{
			eraseAllMenusForShell(shell);
			addMenusToShell(shell);
		}
	}

	@Override
	public void addShell(Shell shell)
	{
		shells.add(shell);
		updateMenus();
	}

	@Override
	public void removeShell(Shell shell)
	{
		eraseAllMenusForShell(shell);
		updateMenus();
	}

	public DefaultMenuConstructor()
	{
		shells = new HashSet<Shell>();
		shellMenuItems = new HashMap<Shell, Set<MenuItem>>();
		globalMenuItems = new HashSet<MenuItem>();
		
		addMenusToGlobalMenu();
	}
}
