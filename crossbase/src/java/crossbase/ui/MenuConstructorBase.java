package crossbase.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
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
import crossbase.ui.actions.Separator;

public class MenuConstructorBase<TVW extends ViewWindow<?>> implements MenuConstructor<TVW>
{
	public final static int ACTION_CATEGORY_ROOT = 0;

	public final static int ACTION_CATEGORY_FILE = 1000;
	public final static int ACTION_FILE_EXIT = 1001;
	public final static int ACTION_FILE_CUSTOM = 1100;

	public final static int ACTION_EDIT_PREFERENCES = 2001;

	public final static int ACTION_CATEGORY_VIEW = 3000;
	public final static int ACTION_VIEW_FULLSCREEN = 3001;
	
	public final static int ACTION_CATEGORY_WINDOW = 4000;
	public final static int ACTION_WINDOW_MINIMIZE = 4001;
	public final static int ACTION_WINDOW_ZOOM = 4002;
	public final static int ACTION_LIST_WINDOWS_LIST = 4100;

	public final static int ACTION_CATEGORY_HELP = 5000;
	public final static int ACTION_HELP_ABOUT = 5001;
	
	private Handler<TVW> exitHandler, aboutHandler, preferencesHandler;
	
	private ActionList<TVW> actionsRoot = new ActionList<TVW>(ACTION_CATEGORY_ROOT);
	private ActionList<TVW> windowsListActionList;
	private HashMap<TVW, Action<TVW>> viewWindowSelectionActions = new HashMap<TVW, Action<TVW>>();
	
	private ViewWindowsManager<?, TVW> viewWindowsManager;
	
	private ViewWindowsManagerListener<TVW> viewWindowsManagerListener = new ViewWindowsManagerListener<TVW>() {

		@Override
		public void lastWindowClosed() {
			// do nothing
		}

		@Override
		public void windowOpened(final TVW window) {
			
			// Creating Fullscreen handler for the window just opened
			final Action<TVW> fullscreenAction = (Action<TVW>)(actionsRoot.findActionByIdRecursively(MenuConstructorBase.ACTION_VIEW_FULLSCREEN));
			if (fullscreenAction != null) {
				Handler<TVW> fullscreenHandler = new Handler<TVW>() {

					@Override
					public void execute(TVW window) {
						window.toggleFullScreen();
					}
				};
				
				fullscreenAction.getHandlers().put(window, fullscreenHandler);
			}

			// Creating Minimize handler for the window just opened
			final Action<TVW> minimizeAction = (Action<TVW>)(actionsRoot.findActionByIdRecursively(MenuConstructorBase.ACTION_WINDOW_MINIMIZE));
			if (minimizeAction != null) {
				Handler<TVW> minimizeHandler = new Handler<TVW>() {

					@Override
					public void execute(TVW window) {
						window.toggleMinimized();
					}
					
					@Override
					public boolean isEnabled() {
						return Display.getCurrent().getActiveShell() != null;
					}
					
				};
				minimizeAction.getHandlers().put(window, minimizeHandler);
			}
			
			// Creating Zoom handler for the window just opened
			final Action<TVW> zoomAction = (Action<TVW>)(actionsRoot.findActionByIdRecursively(MenuConstructorBase.ACTION_WINDOW_ZOOM));
			if (zoomAction != null) {
				Handler<TVW> zoomHandler = new Handler<TVW>() {

					@Override
					public void execute(TVW window) {
						window.toggleMaximized();
					}
					
					@Override
					public boolean isEnabled() {
						return Display.getCurrent().getActiveShell() != null;
					}

					
				};
				zoomAction.getHandlers().put(window, zoomHandler);
			}
			
			
			
			// Creating an action for window selection
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
			
			window.addShellListener(new ShellListener() {
				
				@Override public void shellIconified(ShellEvent arg0) {}
				@Override public void shellDeiconified(ShellEvent arg0) {}
				@Override public void shellClosed(ShellEvent arg0)
				{
					if (fullscreenAction != null) {
						fullscreenAction.getHandlers().remove(window);
					}
					if (minimizeAction != null) {
						minimizeAction.getHandlers().remove(window);
					}
					if (zoomAction != null) {
						zoomAction.getHandlers().remove(window);
					}
				}
				
				@Override
				public void shellDeactivated(ShellEvent arg0) {
					updateMenus(window);
				}
				
				
				@Override
				public void shellActivated(ShellEvent arg0) {
					updateMenus(window);
				}
			});
			
			updateMenus(window);
		}

		@Override
		public void windowClosed(TVW window) {
			Action<TVW> thisWindowAction = viewWindowSelectionActions.get(window);
			windowsListActionList.removeItem(thisWindowAction);
		}
	};
	
	protected ActionList<TVW> getActionsRoot() {
		return actionsRoot;
	}
	
	public ViewWindowsManager<?, TVW> getViewWindowsManager() {
		return viewWindowsManager;
	}

	public void setViewWindowsManager(ViewWindowsManager<?, TVW> viewWindowsManager) {
		if (this.viewWindowsManager != null) {
			this.viewWindowsManager.removeListener(viewWindowsManagerListener);
		}
		
		this.viewWindowsManager = viewWindowsManager;
		
		if (this.viewWindowsManager != null) {
			viewWindowsManager.addListener(viewWindowsManagerListener);
		}
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

		Action<TVW> minimizeWindowAction = new Action<TVW>(ACTION_WINDOW_MINIMIZE, "&Minimize");
		minimizeWindowAction.setHotKey(new HotKey(HotKey.MOD1, 'M'));
		windowActionCategory.addLastItem(minimizeWindowAction);
		
		Action<TVW> zoomWindowAction = new Action<TVW>(ACTION_WINDOW_ZOOM, "&Zoom");
		windowActionCategory.addLastItem(zoomWindowAction);
		
		windowActionCategory.addLastItem(new Separator<TVW>(Action.NO_ID));
		
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
		
		boolean previousWasSeparator = true;
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
						previousWasSeparator = false;
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
							previousWasSeparator = false;
						}
					}
				}
			} else if (category.getItem(i) instanceof Separator) {
				//Separator<TVW> separatorItem = (Separator<TVW>)category.getItem(i);
				if (!previousWasSeparator) {
					new MenuItem(currentMenu, SWT.SEPARATOR);
					previousWasSeparator = true;
				}
			}
		}
		
		if (currentMenu.getItemCount() > 0) {
			// If the last item is separator, we remove it
			MenuItem lastMenuItem = currentMenu.getItem(currentMenu.getItemCount() - 1);
			if ((lastMenuItem.getStyle() & SWT.SEPARATOR) != 0) {
				lastMenuItem.dispose();
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
						@Override public void widgetDefaultSelected(SelectionEvent arg0) {}
						
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							aboutHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				case OSX_SYSTEM_MENU_PREFERENCES:
					item.addSelectionListener(new SelectionListener() {
						@Override public void widgetDefaultSelected(SelectionEvent arg0) {}

						@Override
						public void widgetSelected(SelectionEvent arg0) {
							preferencesHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				case OSX_SYSTEM_MENU_QUIT:
					item.addSelectionListener(new SelectionListener() {
						@Override public void widgetDefaultSelected(SelectionEvent arg0) {}

						@Override
						public void widgetSelected(SelectionEvent arg0) {
							exitHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				}
			}
		}
	}

}
