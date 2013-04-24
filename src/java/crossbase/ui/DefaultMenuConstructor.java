package crossbase.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class DefaultMenuConstructor implements MenuConstructor
{
	private SelectionAdapter exitSelectionAdapter, aboutSelectionAdapter;
	private Set<ViewWindow> viewWindows;
	private HashMap<ViewWindow, Set<MenuItem>> shellMenuItems;
	private Set<MenuItem> globalMenuItems;
	
	/**
	 * Adds a menu to the storage
	 * @param viewWindow The window the menu connected to. If it's null, the global menu will be assumed 
	 * @param menu The menu
	 */
	protected void addShellMenu(ViewWindow viewWindow, MenuItem menuItem)
	{
		if (viewWindow == null)
		{
			globalMenuItems.add(menuItem);
		}
		else
		{
			if (!shellMenuItems.containsKey(viewWindow))
			{
				shellMenuItems.put(viewWindow, new HashSet<MenuItem>());
			}
			shellMenuItems.get(viewWindow).add(menuItem);
		}
	}
	
	/**
	 * Erases all menus and menu items for the selected shell. 
	 * If the shell is null, erases all menus form global menu. 
	 * @param viewWindow The window to erase menus from
	 */
	protected void eraseAllMenusForShell(ViewWindow viewWindow)
	{
		if (viewWindow == null)
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
			if (shellMenuItems.containsKey(viewWindow))
			{
				while (shellMenuItems.get(viewWindow).size() > 0)
				{
					for (MenuItem mi : shellMenuItems.get(viewWindow))
					{
						if (!mi.isDisposed()) mi.dispose();
					}
					shellMenuItems.get(viewWindow).clear();
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
	
	private MenuItem createAndAppendWindowsMenu(Menu menu)
	{
		MenuItem windowsMenuItem = new MenuItem(menu, SWT.CASCADE);
		
		windowsMenuItem.setText("&Windows");

		Menu windowsMenu = new Menu(windowsMenuItem);
		windowsMenuItem.setMenu(windowsMenu);

		for (ViewWindow viewWindow : viewWindows)
		{
			// An item for the window
			MenuItem windowMenuItem = new MenuItem(windowsMenu, SWT.NONE);
			windowMenuItem.setData(viewWindow);
			windowMenuItem.setText(viewWindow.getShell().getText());
			windowMenuItem.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					ViewWindow targetWindow = (ViewWindow)arg0.widget.getData();
					targetWindow.getShell().setActive();
				}
			});
		}
		

		
		return windowsMenuItem;		
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
			addShellMenu(null, createAndAppendWindowsMenu(menu));
			addShellMenu(null, createAndAppendHelpMenu(menu));
		}
	}

	protected void addMenusToShell(ViewWindow viewWindow)
	{
		Menu menu = viewWindow.getShell().getMenuBar();
		addShellMenu(viewWindow, createAndAppendFileMenu(menu));
		addShellMenu(viewWindow, createAndAppendWindowsMenu(menu));
		addShellMenu(viewWindow, createAndAppendHelpMenu(menu));
	}

	@Override
	public void updateMenus()
	{
		for (ViewWindow viewWindow : viewWindows)
		{
			eraseAllMenusForShell(viewWindow);
			addMenusToShell(viewWindow);
		}
	}

	@Override
	public void addWindow(ViewWindow viewWindow)
	{
		viewWindows.add(viewWindow);
		updateMenus();
	}

	@Override
	public void removeWindow(ViewWindow viewWindow)
	{
		viewWindows.remove(viewWindow);
		updateMenus();
	}

	public DefaultMenuConstructor()
	{
		viewWindows = new HashSet<ViewWindow>();
		shellMenuItems = new HashMap<ViewWindow, Set<MenuItem>>();
		globalMenuItems = new HashSet<MenuItem>();
		
		addMenusToGlobalMenu();
	}
}
