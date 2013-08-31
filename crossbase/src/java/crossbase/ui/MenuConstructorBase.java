package crossbase.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;
import crossbase.abstracts.ViewWindowsManagerListener;
import crossbase.ui.actions.Action;
import crossbase.ui.actions.ActionHierarchyMember;
import crossbase.ui.actions.ActionList;
import crossbase.ui.actions.Handler;

public class MenuConstructorBase<TVW extends ViewWindow<?>> implements MenuConstructor<TVW>
{
	public final static int ACTION_CATEGORY_ROOT = 0;

	public final static int ACTION_CATEGORY_FILE = 1000;
	public final static int ACTION_FILE_EXIT = 1001;
	public final static int ACTION_FILE_CUSTOM = 1100;

	public final static int ACTION_EDIT_PREFERENCES = 2001;

	public final static int ACTION_CATEGORY_VIEW = 3000;
	
	public final static int ACTION_CATEGORY_WINDOW = 4000;
	public final static int ACTION_VIEW_FULLSCREEN = 4001;
	public final static int ACTION_LIST_WINDOWS_LIST = 4100;
	public final static int ACTION_WINDOW_CUSTOM = 4200;

	public final static int ACTION_CATEGORY_HELP = 5000;
	public final static int ACTION_HELP_ABOUT = 5001;
	
	private Handler<TVW> exitHandler, aboutHandler, preferencesHandler;
	
	private ActionList<TVW> actionsRoot = new ActionList<TVW>(ACTION_CATEGORY_ROOT);
	private ActionList<TVW> windowsListActionList;
	private HashMap<TVW, Action<TVW>> viewWindowSelectionActions = new HashMap<TVW, Action<TVW>>();
	
	private ViewWindowsManager<?, TVW, ? extends MenuConstructorBase<TVW>> viewWindowsManager;
	
	private ViewWindowsManagerListener<TVW> viewWindowsManagerListener = new ViewWindowsManagerListener<TVW>() {

		@Override
		public void lastWindowClosed() {
			// do nothing
		}

		@Override
		public void windowOpened(final TVW window) {
			final Action<TVW> thisWindowAction = new Action<>(ActionHierarchyMember.NO_ID);
			thisWindowAction.setTitle(window.getTitle());
			thisWindowAction.getHandlers().put(null, new Handler<TVW>() {

				@Override
				public void execute(TVW win) {
					window.activate(false);
				}
				
				public String getTitle() {
					if (window.getDocument() == null) {
						return null;
					} else {
						return window.getDocument().getTitle();
					}
				}
				
				public HotKey getHotKey() {
					int index = windowsListActionList.indexOf(thisWindowAction);
					if (index < 9) {
						return new HotKey(HotKey.MOD1, (char)('1' + index));
					}
					else return null;
				}
				
				public boolean isChecked() {
					return window.isActive();
				}
			});
			viewWindowSelectionActions.put(window, thisWindowAction);
			windowsListActionList.addLastItem(thisWindowAction);
		}

		@Override
		public void windowClosed(TVW window) {
			Action<TVW> thisWindowAction = viewWindowSelectionActions.get(window);
			windowsListActionList.removeItem(thisWindowAction);
		}
	};
	
	public ViewWindowsManager<?, TVW, ? extends MenuConstructorBase<TVW>> getViewWindowsManager() {
		return viewWindowsManager;
	}

	public void setViewWindowsManager(ViewWindowsManager<?, TVW, ? extends MenuConstructorBase<TVW>> viewWindowsManager) {
		if (this.viewWindowsManager != null) {
			this.viewWindowsManager.removeListener(viewWindowsManagerListener);
		}
		
		this.viewWindowsManager = viewWindowsManager;
		
		if (this.viewWindowsManager != null) {
			viewWindowsManager.addListener(viewWindowsManagerListener);
		}
	}

	public ActionList<TVW> getActionsRoot() {
		return actionsRoot;
	}
	
	public MenuConstructorBase() {

		// File action list
		ActionList<TVW> fileActionCategory = new ActionList<>(ACTION_CATEGORY_FILE, "&File");
		actionsRoot.addLastItem(fileActionCategory);

		// Window action list
		ActionList<TVW> viewActionCategory = new ActionList<>(ACTION_CATEGORY_VIEW, "&View");
		actionsRoot.addLastItem(viewActionCategory);

		Action<TVW> fullscreenAction = new Action<TVW>(ACTION_VIEW_FULLSCREEN, "&Fullscreen");
		if (SWT.getPlatform().equals("cocoa")) {
			fullscreenAction.setHotKey(new HotKey(HotKey.MOD1 | HotKey.CTRL, 'F'));
		} else {
			fullscreenAction.setHotKey(new HotKey(HotKey.MOD1 | HotKey.SHIFT, 'F'));
		}
		viewActionCategory.addLastItem(fullscreenAction);
		
		// Window action list
		ActionList<TVW> windowActionCategory = new ActionList<>(ACTION_CATEGORY_WINDOW, "&Window");
		actionsRoot.addLastItem(windowActionCategory);
		// Windows list action list
		windowsListActionList = new ActionList<>(ACTION_LIST_WINDOWS_LIST);
		windowsListActionList.setSubMenu(false);
		windowsListActionList.setRadioItems(true);
		windowActionCategory.addLastItem(windowsListActionList);

		// Help action list
		ActionList<TVW> helpActionCategory = new ActionList<>(ACTION_CATEGORY_HELP, "&Help");
		actionsRoot.addLastItem(helpActionCategory);

		
		if (!SWT.getPlatform().equals("cocoa")) {
	
			Action<TVW> exitAction = new Action<TVW>(ACTION_FILE_EXIT, "E&xit");
			fileActionCategory.addLastItem(exitAction);
	
			Action<TVW> aboutAction = new Action<TVW>(ACTION_HELP_ABOUT, "&About");
			helpActionCategory.addLastItem(aboutAction);
		}
	}

	
	public Handler<TVW> getExitHandler()
	{
		return exitHandler;
	}

	public void setExitHandler(Handler<TVW> exitHandler)
	{
		this.exitHandler = exitHandler;

		Action<TVW> action = ((Action<TVW>)actionsRoot.findActionByIdRecursively(ACTION_FILE_EXIT));
		if (action != null) {
			Map<TVW, Handler<TVW>> handlers = action.getHandlers();
			handlers.put(null, exitHandler);
		}
	}

	public Handler<TVW> getPreferencesHandler()
	{
		return preferencesHandler;
	}

	public void setPreferencesHandler(Handler<TVW> exitHandler)
	{
		this.preferencesHandler = exitHandler;

		Action<TVW> action = ((Action<TVW>)actionsRoot.findActionByIdRecursively(ACTION_EDIT_PREFERENCES));
		if (action != null) {
			Map<TVW, Handler<TVW>> handlers = action.getHandlers();
			handlers.put(null, preferencesHandler);
		}
	}
	
	
	public Handler<TVW> getAboutHandler()
	{
		return aboutHandler;
	}

	public void setAboutHandler(Handler<TVW> aboutHandler)
	{
		this.aboutHandler = aboutHandler;

		Action<TVW> action = ((Action<TVW>)actionsRoot.findActionByIdRecursively(ACTION_HELP_ABOUT));
		if (action != null) {
			Map<TVW, Handler<TVW>> handlers = action.getHandlers();
			handlers.put(null, aboutHandler);
		}
	}

	private boolean addMenusForActionList(final TVW window, ActionList<TVW> category, Menu currentMenu) {
		boolean addedAnyActions = false;
		
		for (int i = 0; i < category.getItemsCount(); i++) {
			if (category.getItem(i) instanceof Action) {
				
				Action<TVW> actionItem = (Action<TVW>)category.getItem(i);
				
				boolean dontShowItem = false;
				if (category.getItem(i) instanceof ActionList) {
					ActionList<TVW> actionCategoryItem = (ActionList<TVW>)category.getItem(i);
					
					Menu menu = currentMenu;
					MenuItem menuItem = null;
					
					if (actionCategoryItem.isSubMenu()) {
						menuItem = new MenuItem(currentMenu, SWT.CASCADE);
						menuItem.setText(actionCategoryItem.getTitle());
						menu = new Menu(menuItem);
						menuItem.setMenu(menu);
						dontShowItem = true;
					}
					
					if (addMenusForActionList(window, actionCategoryItem, menu)) {
						addedAnyActions = true;
					} else {
						if (menuItem != null) menuItem.dispose();
					}
				}			

				if (!dontShowItem) {
					// If item is globally supported or if it has a specific handler for this window
					if (actionItem.getHandlers().containsKey(null) || actionItem.getHandlers().containsKey(window)) {
	
						// Getting the handler. A specific one should override the default one
						final Handler<TVW> usingHandler = actionItem.getHandlers().get(window) != null ? actionItem.getHandlers().get(window) : actionItem.getHandlers().get(null);
						
						// If we have any handler
						if (usingHandler != null && usingHandler.isVisible()) {
							
							int flags = SWT.NONE;
							if (category.isRadioItems()) flags |= SWT.RADIO;
							
							MenuItem menuItem = new MenuItem(currentMenu, flags);

							HotKey hotHey = usingHandler.getHotKey();
							if (hotHey == null) hotHey = actionItem.getHotKey();
							
							String title = usingHandler.getTitle();
							if (title == null) title = actionItem.getTitle();

							if (hotHey == null) {
								menuItem.setText(title);
							} else {
								menuItem.setText(title + "\t" + hotHey.toString());
								menuItem.setAccelerator(hotHey.toAccelerator());
							}

							menuItem.setSelection(usingHandler.isChecked());
							menuItem.setEnabled(usingHandler.isEnabled());
							menuItem.addSelectionListener(new SelectionListener() {
								
								@Override
								public void widgetSelected(SelectionEvent arg0) {
									usingHandler.execute(window);
								}
								
								@Override
								public void widgetDefaultSelected(SelectionEvent arg0) {
									// Doing nothing here
								}
							});
							addedAnyActions = true;
						}
					}
				}
			} 
		}
		return addedAnyActions;
	}
	
	@Override
	public void updateMenus(TVW window) {
		Menu windowMenu;
		
		if (window != null)
		{
			windowMenu = window.getMenu();
		} else {
			Display display = Display.getDefault();
			windowMenu = display.getMenuBar();
		}
		
		if (windowMenu != null) {
			while (windowMenu.getItems().length > 0) {
				windowMenu.getItems()[0].dispose();
			}
			
			addMenusForActionList(window, actionsRoot, windowMenu);
		}
		
		final int OSX_SYSTEM_MENU_ABOUT = -1;
		final int OSX_SYSTEM_MENU_PREFERENCES = -2;
		final int OSX_SYSTEM_MENU_QUIT = -6;
		
		// Adding OS X system menu handlers
		if (SWT.getPlatform().equals("cocoa"))
		{
			for (int i = 0; i < Display.getDefault().getSystemMenu().getItems().length; i++)
			{
				MenuItem item = Display.getDefault().getSystemMenu().getItems()[i];
				
				switch (item.getID())
				{
				case OSX_SYSTEM_MENU_ABOUT:
					item.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							aboutHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					break;
				case OSX_SYSTEM_MENU_PREFERENCES:
					item.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							preferencesHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					break;
				case OSX_SYSTEM_MENU_QUIT:
					item.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							exitHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					break;
				}
			}
		}
	}
	
	
//
//	private ArrayList<TVW> viewWindows;
//	private HashMap<TVW, Set<MenuItem>> shellMenuItems;
//	private Set<MenuItem> globalMenuItems;
//	
//	protected TVW getActiveViewWindow()
//	{
//		for (TVW viewWindow : viewWindows)
//		{
//			if (viewWindow.isActive())
//			{
//				return viewWindow;
//			}
//		}			
//		return null;
//	}
//	
//	/**
//	 * Adds a menu to the storage
//	 * @param viewWindow The window the menu connected to. If it's null, the global menu will be assumed 
//	 * @param menu The menu
//	 */
//	private void addShellMenu(TVW viewWindow, MenuItem menuItem)
//	{
//		if (menuItem == null) 
//		{
//			return;
//		}
//		
//		if (viewWindow == null)
//		{
//			globalMenuItems.add(menuItem);
//		}
//		else
//		{
//			if (!shellMenuItems.containsKey(viewWindow))
//			{
//				shellMenuItems.put(viewWindow, new HashSet<MenuItem>());
//			}
//			shellMenuItems.get(viewWindow).add(menuItem);
//		}
//	}
//	
//	/**
//	 * Erases all menus and menu items for the selected shell. 
//	 * If the shell is null, erases all menus form global menu. 
//	 * @param viewWindow The window to erase menus from
//	 */
//	protected void eraseAllMenusForWindow(TVW viewWindow)
//	{
//		if (viewWindow == null)
//		{
//			while (globalMenuItems.size() > 0)
//			{
//				for (MenuItem mi : globalMenuItems)
//				{
//					if (mi != null && !mi.isDisposed()) mi.dispose();
//				}
//				globalMenuItems.clear();
//			}
//		}
//		else
//		{
//			if (shellMenuItems.containsKey(viewWindow))
//			{
//				while (shellMenuItems.get(viewWindow).size() > 0)
//				{
//					for (MenuItem mi : shellMenuItems.get(viewWindow))
//					{
//						if (!mi.isDisposed()) mi.dispose();
//					}
//					shellMenuItems.get(viewWindow).clear();
//				}				
//			}
//		}
//	}
//	
//	private void appendItems(Menu menu, List<MenuItem> items)
//	{
//		if (items == null) return;
//		for (MenuItem item : items)
//		{
//			item.setMenu(menu);
//		}
//	}
//	
//	private MenuItem createAndAppendFileMenu(Menu menu)
//	{
//		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
//		
//		fileMenuItem.setText("&File");
//
//		Menu fileMenu = new Menu(fileMenuItem);
//		fileMenuItem.setMenu(fileMenu);
//
//		appendCustomFileMenuItems(fileMenu);
//		
//		if (!SWT.getPlatform().equals("cocoa"))
//		{
//			if (fileMenu.getItemCount() > 0) 
//			{
//				// If the menu isn't empty yet, adding the new item
//				new MenuItem(fileMenu, SWT.SEPARATOR);
//			}
//
//			// "Exit" menu item
//			MenuItem mainMenuItemExit = new MenuItem(fileMenu, SWT.NONE);
//			mainMenuItemExit.addSelectionListener(exitSelectionAdapter);
//
//			mainMenuItemExit.setText("E&xit");
//		}
//		
//		if (fileMenu.getItemCount() > 0)
//		{
//			return fileMenuItem;
//		}
//		else
//		{
//			fileMenuItem.dispose();
//			return null;
//		}
//		
//	}
//
//	protected void appendCustomFileMenuItems(Menu fileMenu)
//	{
//		// To be overridden in the inherited classes
//	}
//	
//	/**
//	 * The implementation of this methid in a subclass should add menu items 
//	 * between <b>File</b> and <b>Window</b> menus.
//	 * @param menu Target menu to add to
//	 * @return Array of the added menu items
//	 */
//	protected MenuItem[] appendCustomMenus(Menu menu) {
//		// To be overridden in the inherited classes
//		return null;
//	}
//
//	private MenuItem createAndAppendWindowsMenu(Menu menu, final boolean addFullscreen, final boolean addMaximize)
//	{
//		MenuItem windowMenuItem = new MenuItem(menu, SWT.CASCADE);
//		
//		windowMenuItem.setText("&Window");
//
//		Menu windowMenu = new Menu(windowMenuItem);
//		windowMenuItem.setMenu(windowMenu);
//		
//		// "Minimize" menu item
//		final MenuItem minimizeMenuItem = new MenuItem(windowMenu, SWT.NONE);
//		minimizeMenuItem.addSelectionListener(new SelectionAdapter()
//		{
//			@Override
//			public void widgetSelected(SelectionEvent arg0)
//			{
//				if (getActiveViewWindow() != null)
//				{
//					getActiveViewWindow().toggleMinimized();
//				}
//			}
//		});
//		HotKey minimizeHotKey = new HotKey(HotKey.MOD1, 'M');
//		minimizeMenuItem.setAccelerator(minimizeHotKey.toAccelerator());
//		minimizeMenuItem.setText("&Minimize\t" + minimizeHotKey.toString());
//		
//		final MenuItem maximizeMenuItem;
//		if (addMaximize)
//		{
//			// "Maximize" menu item
//			maximizeMenuItem = new MenuItem(windowMenu, SWT.NONE);
//			maximizeMenuItem.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent arg0)
//				{
//					if (getActiveViewWindow() != null)
//					{
//						getActiveViewWindow().toggleMaximized();
//					}
//				}
//			});
//			maximizeMenuItem.setText("Zoom");
//		}
//		else
//		{
//			maximizeMenuItem = null;
//		}
//		
//		// "Fullscreen" menu item
//		final MenuItem fullscreenMenuItem;
//		if (addFullscreen)
//		{
//			fullscreenMenuItem = new MenuItem(windowMenu, SWT.NONE);
//			fullscreenMenuItem.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent arg0)
//				{
//					if (Display.getCurrent() != null && !Display.getCurrent().isDisposed())
//					{
//						getActiveViewWindow().toggleFullScreen();
//					}
//				}
//			});
//			HotKey fullscreenHotKey = new HotKey(HotKey.MOD1 | HotKey.SHIFT, 'F');
//			fullscreenMenuItem.setAccelerator(fullscreenHotKey.toAccelerator());
//			fullscreenMenuItem.setText("Fullscreen\t" + fullscreenHotKey.toString());
//		}
//		else
//		{
//			fullscreenMenuItem = null;
//		}
//		
//		// Custom items
//		List<MenuItem> customItems = createCustomWindowMenuItems();
//
//		if (customItems != null && customItems.size() > 0) 
//		{
//			// If the menu isn't empty yet, adding the new item
//			new MenuItem(windowMenu, SWT.SEPARATOR);
//		}
//		
//		appendItems(windowMenu, customItems);
//		
//		// Window items
//		boolean anyWindowsToAdd = false;
//		for (TVW viewWindow : viewWindows)
//		{
//			if (viewWindow.getDocument() != null)
//			{
//				anyWindowsToAdd = true;
//				break;
//			}
//		}
//		
//		if (anyWindowsToAdd)
//		{
//			new MenuItem(windowMenu, SWT.SEPARATOR);
//		}
//
//		char hotKey = '1';
//		for (TVW viewWindow : viewWindows)
//		{
//			if (viewWindow.getDocument() != null)
//			{
//				// An item for the window
//				MenuItem windowItemMenuItem = new MenuItem(windowMenu, SWT.RADIO);
//				HotKey windowHotKey = new HotKey(HotKey.MOD1, hotKey);
//				windowItemMenuItem.setData(viewWindow);
//				if (hotKey <= '9')
//				{
//					// Cmd+1, Cmd+2, ..., Cmd+9
//					windowItemMenuItem.setAccelerator(windowHotKey.toAccelerator());
//					windowItemMenuItem.setText(viewWindow.getDocument().getTitle() + "\t" + windowHotKey.toString());
//					hotKey ++;
//				}
//				else
//				{
//					// No hotkey
//					windowItemMenuItem.setText(viewWindow.getDocument().getTitle());
//				}
//
//				windowItemMenuItem.setSelection(viewWindow.isActive());
//				windowItemMenuItem.addSelectionListener(new SelectionAdapter()
//				{
//					@Override
//					public void widgetSelected(SelectionEvent arg0)
//					{
//						TVW targetWindow = (TVW)arg0.widget.getData();
//						targetWindow.activate(false);
//					}
//				});
//			}
//		}
//		
//		// Add global listener to "Window" menu
//		windowMenu.addMenuListener(new MenuListener()
//		{
//			@Override
//			public void menuShown(MenuEvent arg0)
//			{
//				minimizeMenuItem.setEnabled(getActiveViewWindow() != null);
//				if (addMaximize) maximizeMenuItem.setEnabled(getActiveViewWindow() != null);
//				if (addFullscreen) fullscreenMenuItem.setEnabled(getActiveViewWindow() != null && getActiveViewWindow().supportsFullscreen());
//			}
//			
//			@Override
//			public void menuHidden(MenuEvent arg0)
//			{
//			}
//		});
//
//		
//		if (windowMenu.getItemCount() > 0)
//		{
//			return windowMenuItem;
//		}
//		else
//		{
//			windowMenuItem.dispose();
//			return null;
//		}
//	}
//	
//	/**
//	 * Override this method to create custom menu items in 
//	 * the middle of <i>Window</i> menu (between Minimize/Maximize and
//	 * windows list)
//	 * @return Create a list, add all the items to it and return it.
//	 */
//	protected List<MenuItem> createCustomWindowMenuItems()
//	{
//		return null;
//		// To be overridden in the inherited classes
//	}
//	
//	
//	private MenuItem createAndAppendHelpMenu(Menu menu)
//	{
//		// "Help" menu item
//		MenuItem helpMenuItem = new MenuItem(menu, SWT.CASCADE);
//		helpMenuItem.setText("&Help");
//		
//		Menu helpMenu = new Menu(helpMenuItem);
//		helpMenuItem.setMenu(helpMenu);
//
//		boolean customItemsAppended = appendCustomHelpMenuItems(helpMenu);
//		
//		if (!SWT.getPlatform().equals("cocoa"))
//		{
//			if (customItemsAppended) 
//			{
//				// If the menu isn't empty yet, adding the new item
//				new MenuItem(helpMenu, SWT.SEPARATOR);
//			}
//
//			// "About" menu item
//			MenuItem aboutMenuItem = new MenuItem(helpMenu, SWT.NONE);
//			aboutMenuItem.addSelectionListener(aboutSelectionAdapter);
//			aboutMenuItem.setText("&About...");
//		}
//		
//		if (helpMenu.getItemCount() > 0)
//		{
//			return helpMenuItem;
//		}
//		else
//		{
//			helpMenuItem.dispose();
//			return null;
//		}
//	}
//	
//	/**
//	 * Override this method to create custom menu items in 
//	 * the top of <i>Help</i> menu (before About on Windows and Linux)
//	 * @param helpMenu The <i>Help</i> menu &#151; you should add your new items here
//	 * @return If you added any items, you should return true. Otherwise you should return false.
//	 */
//	protected boolean appendCustomHelpMenuItems(Menu helpMenu)
//	{
//		// To be overridden in the inherited classes
//		return false;
//	}
//	
//	
//
//	private void addMenusToGlobalMenu()
//	{
//		if (SWT.getPlatform().equals("cocoa"))
//		{
//			Display display = Display.getDefault();
//			Menu menu = display.getMenuBar();
//			
//			addShellMenu(null, createAndAppendFileMenu(menu));
//			
//			MenuItem[] customMenuItems = appendCustomMenus(menu);
//			if (customMenuItems != null) {
//				for (int i = 0; i < customMenuItems.length; i++) {
//					addShellMenu(null, customMenuItems[i]);
//				}
//			}
//			
//			MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu, false, false);
//			if (windowsMenuItem != null)
//			{
//				addShellMenu(null, windowsMenuItem);
//			}
//			addShellMenu(null, createAndAppendHelpMenu(menu));
//		}
//	}
//
//
//	private void addMenusToWindow(TVW viewWindow, boolean addFullscreen, boolean addMaximize)
//	{
//		Menu menu = viewWindow.getMenu();
//		addShellMenu(viewWindow, createAndAppendFileMenu(menu));
//		
//		MenuItem[] customMenuItems = appendCustomMenus(menu);
//		if (customMenuItems != null) {
//			for (int i = 0; i < customMenuItems.length; i++) {
//				addShellMenu(viewWindow, customMenuItems[i]);
//			}
//		}
//		
//		MenuItem windowsMenuItem = createAndAppendWindowsMenu(menu, addFullscreen, addMaximize);
//		if (windowsMenuItem != null)
//		{
//			addShellMenu(viewWindow, windowsMenuItem);
//		}
//		addShellMenu(viewWindow, createAndAppendHelpMenu(menu));
//	}
//
//	@Override
//	public void updateMenus()
//	{
//		eraseAllMenusForWindow(null);
//		addMenusToGlobalMenu();
//		for (TVW viewWindow : viewWindows)
//		{
//			eraseAllMenusForWindow(viewWindow);
//			addMenusToWindow(viewWindow, viewWindow.supportsFullscreen(), viewWindow.supportsMaximizing());
//		}
//	}
//
//	@Override
//	public void addWindow(TVW viewWindow)
//	{
//		viewWindows.add(viewWindow);
//		updateMenus();
//	}
//
//	@Override
//	public void removeWindow(TVW viewWindow)
//	{
//		viewWindows.remove(viewWindow);
//		eraseAllMenusForWindow(viewWindow);
//		updateMenus();
//	}
//
//	public MenuConstructorBase()
//	{
//		viewWindows = new ArrayList<TVW>();
//		shellMenuItems = new HashMap<TVW, Set<MenuItem>>();
//		globalMenuItems = new HashSet<MenuItem>();
//	}
}
