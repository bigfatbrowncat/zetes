package zetes.wings.base;

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

import zetes.wings.HotKey;
import zetes.wings.abstracts.MenuConstructor;
import zetes.wings.abstracts.ViewWindow;
import zetes.wings.abstracts.ViewWindowsManager;
import zetes.wings.abstracts.ViewWindowsManagerListener;
import zetes.wings.actions.Action;
import zetes.wings.actions.ActionList;
import zetes.wings.actions.Handler;
import zetes.wings.actions.Separator;

public class MenuConstructorBase<TVW extends ViewWindow<?>> implements MenuConstructor<TVW>
{
	private ViewWindowsManager<?, TVW> viewWindowsManager;

	// Global handlers
	private Handler<TVW> exitGlobalHandler, aboutGlobalHandler, preferencesGlobalHandler;

	// Actions
	private ActionList<TVW> actionsRoot = new ActionList<TVW>();
	
	private ActionList<TVW> fileActionCategory;
	private Action<TVW> exitFileAction;

	private ActionList<TVW> editActionCategory;
	private Action<TVW> preferencesEditAction;
	
	private ActionList<TVW> viewActionCategory;
	private Action<TVW> fullscreenViewAction;

	private ActionList<TVW> windowActionCategory;
	private Action<TVW> minimizeWindowAction;
	private Action<TVW> zoomWindowAction;
	private ActionList<TVW> windowsActionList;
	private HashMap<TVW, Action<TVW>> viewWindowSelectionActions = new HashMap<TVW, Action<TVW>>();
	
	private ActionList<TVW> helpActionCategory;
	private Action<TVW> aboutHelpAction;
	
	private ViewWindowsManagerListener<TVW> viewWindowsManagerListener = new ViewWindowsManagerListener<TVW>() {

		@Override
		public void lastWindowClosed() {
			// do nothing
		}

		@Override
		public void windowOpened(final TVW window) {
			
			// Creating Fullscreen handler for the window just opened
			if (window.supportsFullscreen()) {				
				Handler<TVW> fullscreenHandler = new Handler<TVW>() {

					@Override
					public void execute(TVW window) {
						window.toggleFullScreen();
					}
				};
				fullscreenViewAction.getHandlers().put(window, fullscreenHandler);
			} 				

			// Creating Minimize handler for the window just opened
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
			minimizeWindowAction.getHandlers().put(window, minimizeHandler);
			
			// Creating Zoom handler for the window just opened
			if (window.supportsMaximizing()) {
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
				zoomWindowAction.getHandlers().put(window, zoomHandler);
			}
			
			
			
			// Creating an action for window selection
			final Action<TVW> thisWindowSelectionAction = new Action<>();
			thisWindowSelectionAction.setTitle(window.getViewTitle());
			thisWindowSelectionAction.getHandlers().put(null, new Handler<TVW>() {

				@Override
				public void execute(TVW win) {
					window.activate(false);
				}
				
				public String getTitle() {
					return window.getViewTitle();
				}
				
				public HotKey getHotKey() {
					int index = windowsActionList.indexOf(thisWindowSelectionAction);
					if (index < 9) {
						return new HotKey(HotKey.MOD1, (char)('1' + index));
					}
					else return null;
				}
				
				public boolean isChecked() {
					return window.isActive();
				}
			});
			viewWindowSelectionActions.put(window, thisWindowSelectionAction);
			windowsActionList.addLastItem(thisWindowSelectionAction);
			
			window.addShellListener(new ShellListener() {
				
				@Override public void shellIconified(ShellEvent arg0) {}
				@Override public void shellDeiconified(ShellEvent arg0) {}
				@Override public void shellClosed(ShellEvent arg0)
				{
					if (fullscreenViewAction != null) {
						fullscreenViewAction.getHandlers().remove(window);
					}
					if (minimizeWindowAction != null) {
						minimizeWindowAction.getHandlers().remove(window);
					}
					if (zoomWindowAction != null) {
						zoomWindowAction.getHandlers().remove(window);
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
			windowsActionList.removeItem(thisWindowAction);
		}
	};
	
	public MenuConstructorBase(ViewWindowsManager<?, TVW> viewWindowsManager) {

		// Starting listening to viewWindowsManager events
		this.viewWindowsManager = viewWindowsManager;
		viewWindowsManager.addListener(viewWindowsManagerListener);
		
		// *** Creating the basic menu actions ***
		
		// File action list
		fileActionCategory = new ActionList<>("&File");
		actionsRoot.addLastItem(fileActionCategory);

		// Edit action list
		editActionCategory = new ActionList<>("&Edit");
		actionsRoot.addLastItem(editActionCategory);
		
		// View action list
		viewActionCategory = new ActionList<>("&View");
		actionsRoot.addLastItem(viewActionCategory);

		fullscreenViewAction = new Action<TVW>("&Fullscreen");
		if (SWT.getPlatform().equals("cocoa")) {
			fullscreenViewAction.setHotKey(new HotKey(HotKey.MOD1 | HotKey.CTRL, 'F'));
		} else {
			fullscreenViewAction.setHotKey(new HotKey(HotKey.MOD1 | HotKey.SHIFT, 'F'));
		}
		viewActionCategory.addLastItem(fullscreenViewAction);
		
		// Window action list
		windowActionCategory = new ActionList<>("&Window");
		actionsRoot.addLastItem(windowActionCategory);

		minimizeWindowAction = new Action<TVW>("&Minimize");
		minimizeWindowAction.setHotKey(new HotKey(HotKey.MOD1, 'M'));
		windowActionCategory.addLastItem(minimizeWindowAction);
		
		zoomWindowAction = new Action<TVW>("&Zoom");
		windowActionCategory.addLastItem(zoomWindowAction);
		
		windowActionCategory.addLastItem(new Separator<TVW>());
		
		// Windows list action list
		windowsActionList = new ActionList<>();
		windowsActionList.setSubMenu(false);
		windowsActionList.setRadioItems(true);
		windowActionCategory.addLastItem(windowsActionList);

		// Help action list
		helpActionCategory = new ActionList<>("&Help");
		actionsRoot.addLastItem(helpActionCategory);

		
		if (!SWT.getPlatform().equals("cocoa")) {
	
			exitFileAction = new Action<TVW>("E&xit");
			fileActionCategory.addLastItem(exitFileAction);
			
			preferencesEditAction = new Action<TVW>("&Preferences");
			preferencesEditAction.setHotKey(new HotKey(HotKey.MOD1, 'P'));
			editActionCategory.addLastItem(preferencesEditAction);
	
			aboutHelpAction = new Action<TVW>("&About");
			helpActionCategory.addLastItem(aboutHelpAction);
		}
		
		// Setting the default handlers
		
		Handler<TVW> noFullscreenHandler = new Handler<TVW>() {
			@Override
			public void execute(TVW window) {
				// Do nothing
			}
			@Override
			public boolean isVisible() {
				return false;
			}
		};
		fullscreenViewAction.getHandlers().put(null, noFullscreenHandler);

		Handler<TVW> noZoomHandler = new Handler<TVW>() {

			@Override
			public void execute(TVW window) {
				// Do nothing
			}
			
			@Override
			public boolean isVisible() {
				return false;
			}

			
		};
		zoomWindowAction.getHandlers().put(null, noZoomHandler);
		
		Handler<TVW> noPreferencesHandler = new Handler<TVW>() {
			@Override
			public void execute(TVW window) {
				// Do nothing
			}
			
			@Override
			public boolean isEnabled() {
				return false;
			}
			
			@Override
			public boolean isVisible() {
				return false;
			}
		};
		setPreferencesGlobalHandler(noPreferencesHandler);
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
							aboutGlobalHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				case OSX_SYSTEM_MENU_PREFERENCES:
					item.setEnabled(preferencesGlobalHandler.isEnabled());
					item.addSelectionListener(new SelectionListener() {
						@Override public void widgetDefaultSelected(SelectionEvent arg0) {}

						@Override
						public void widgetSelected(SelectionEvent arg0) {
							preferencesGlobalHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				case OSX_SYSTEM_MENU_QUIT:
					item.addSelectionListener(new SelectionListener() {
						@Override public void widgetDefaultSelected(SelectionEvent arg0) {}

						@Override
						public void widgetSelected(SelectionEvent arg0) {
							exitGlobalHandler.execute(viewWindowsManager.getActiveWindow());
							
						}
						
					});
					break;
				}
			}
		}
	}

	
	public Handler<TVW> getExitGlobalHandler()
	{
		return exitGlobalHandler;
	}

	public void setExitGlobalHandler(Handler<TVW> exitGlobalHandler)
	{
		this.exitGlobalHandler = exitGlobalHandler;

		if (exitFileAction != null) {
			Map<TVW, Handler<TVW>> handlers = exitFileAction.getHandlers();
			handlers.put(null, exitGlobalHandler);
		}
	}

	public Handler<TVW> getPreferencesGlobalHandler()
	{
		return preferencesGlobalHandler;
	}

	public void setPreferencesGlobalHandler(Handler<TVW> exitHandler)
	{
		this.preferencesGlobalHandler = exitHandler;

		if (preferencesEditAction != null) {
			Map<TVW, Handler<TVW>> handlers = preferencesEditAction.getHandlers();
			handlers.put(null, preferencesGlobalHandler);
		}
	}
	
	
	public Handler<TVW> getAboutGlobalHandler()
	{
		return aboutGlobalHandler;
	}

	public void setAboutGlobalHandler(Handler<TVW> aboutGlobalHandler)
	{
		this.aboutGlobalHandler = aboutGlobalHandler;

		if (aboutHelpAction != null) {
			Map<TVW, Handler<TVW>> handlers = aboutHelpAction.getHandlers();
			handlers.put(null, aboutGlobalHandler);
		}
	}

	// Actions are available for descendants only
	
	protected ActionList<TVW> getActionsRoot() {
		return actionsRoot;
	}
	
	protected ActionList<TVW> getFileActionCategory() {
		return fileActionCategory;
	}

	protected Action<TVW> getExitFileAction() {
		return exitFileAction;
	}

	protected ActionList<TVW> getEditActionCategory() {
		return editActionCategory;
	}

	protected Action<TVW> getPreferencesEditAction() {
		return preferencesEditAction;
	}

	protected ActionList<TVW> getViewActionCategory() {
		return viewActionCategory;
	}

	protected Action<TVW> getFullscreenViewAction() {
		return fullscreenViewAction;
	}

	protected ActionList<TVW> getWindowActionCategory() {
		return windowActionCategory;
	}

	protected Action<TVW> getMinimizeWindowAction() {
		return minimizeWindowAction;
	}

	protected Action<TVW> getZoomWindowAction() {
		return zoomWindowAction;
	}

	protected ActionList<TVW> getWindowsActionList() {
		return windowsActionList;
	}

	protected ActionList<TVW> getHelpActionCategory() {
		return helpActionCategory;
	}

	protected Action<TVW> getAboutHelpAction() {
		return aboutHelpAction;
	}
	
	public ViewWindowsManager<?, TVW> getViewWindowsManager() {
		return viewWindowsManager;
	}


}
