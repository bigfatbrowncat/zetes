package crossbase.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;

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
	protected void eraseAllMenusForWindow(ViewWindow viewWindow)
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
		
		char hotKey = '1';
		for (ViewWindow viewWindow : viewWindows)
		{
			if (viewWindow.getDocumentTitle() != null)
			{
				// An item for the window
				MenuItem windowMenuItem = new MenuItem(windowsMenu, SWT.RADIO);
				HotKey windowHotKey = new HotKey(HotKey.MOD1, hotKey);
				windowMenuItem.setData(viewWindow);
				if (hotKey <= '9')
				{
					// Cmd+1, Cmd+2, ..., Cmd+9
					windowMenuItem.setAccelerator(windowHotKey.toAccelerator());
					windowMenuItem.setText(viewWindow.getDocumentTitle() + "\t" + windowHotKey.toString());
					hotKey ++;
				}
				else
				{
					// No hotkey
					windowMenuItem.setText(viewWindow.getDocumentTitle());
				}

				windowMenuItem.setSelection(Display.getDefault().getActiveShell() == viewWindow.getShell());
				windowMenuItem.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent arg0)
					{
						ViewWindow targetWindow = (ViewWindow)arg0.widget.getData();
						targetWindow.getShell().setMinimized(false);
						targetWindow.getShell().setActive();
					}
				});
			}
		}
		
		if (windowsMenu.getItemCount() > 0)
		{
			return windowsMenuItem;
		}
		else
		{
			windowsMenuItem.dispose();
			return null;
		}
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
			MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu);
			if (windowsMenuItem != null)
			{
				addShellMenu(null, windowsMenuItem);
			}
			addShellMenu(null, createAndAppendHelpMenu(menu));
		}
	}

	protected void addMenusToWindow(ViewWindow viewWindow)
	{
		Menu menu = viewWindow.getShell().getMenuBar();
		addShellMenu(viewWindow, createAndAppendFileMenu(menu));
		MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu);
		if (windowsMenuItem != null)
		{
			addShellMenu(viewWindow, windowsMenuItem);
		}
		addShellMenu(viewWindow, createAndAppendHelpMenu(menu));
	}

	@Override
	public void updateMenus()
	{
		for (ViewWindow viewWindow : viewWindows)
		{
			eraseAllMenusForWindow(viewWindow);
			addMenusToWindow(viewWindow);
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
