/*******************************************************************************
 * Copyright (c) 2008, 2012 Adobe Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Adobe Systems, Inc. - initial API and implementation
 *     IBM Corporation - cleanup
 *     EclipseSource Inc - modified to run without workbench
 *******************************************************************************/
package crossbase.ui.extensions.cocoa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSApplication;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSControl;
import org.eclipse.swt.internal.cocoa.NSMenu;
import org.eclipse.swt.internal.cocoa.NSMenuItem;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSToolbar;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.misc.StatusUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The CocoaUIEnhancer provides the standard "About" and "Preference" menu items
 * and links them to the corresponding workbench commands. This must be done in
 * a MacOS X fragment because SWT doesn't provide an abstraction for the (MacOS
 * X only) application menu and we have to use MacOS specific natives. The
 * fragment is for the org.eclipse.ui plug-in because we need access to the
 * Workbench "About" and "Preference" actions.
 * 
 * @noreference this class is not intended to be referenced by any client.
 * @since 1.0
 */
public class CocoaUIEnhancer extends CocoaUtil implements IStartup {

	private static final int kAboutMenuItem = 0;
	private static final int kPreferencesMenuItem = 2;
	private static final int kHideApplicationMenuItem = 6;
	private static final int kQuitMenuItem = 10;

	static long sel_toolbarButtonClicked_;
	static long sel_preferencesMenuItemSelected_;
	static long sel_aboutMenuItemSelected_;

	private static final long NSWindowToolbarButton = 3;

	/* This callback is not freed */
	static Callback proc3Args;
	static final byte[] SWT_OBJECT = { 'S', 'W', 'T', '_', 'O', 'B', 'J', 'E', 'C', 'T', '\0' };

	private void init() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
	IllegalAccessException, InvocationTargetException, NoSuchFieldException {
		// TODO: These should either move out of Display or be accessible to
		// this class.
		byte[] types = { '*', '\0' };
		int size = C.PTR_SIZEOF;
		int align = C.PTR_SIZEOF == 4 ? 2 : 3;

		Class clazz = CocoaUIEnhancer.class;

		proc3Args = new Callback(clazz, "actionProc", 3); //$NON-NLS-1$
		// call getAddress
		Method getAddress = Callback.class.getMethod("getAddress", new Class[0]);
		Object object = getAddress.invoke(proc3Args, null);
		long proc3 = convertToLong(object);
		if (proc3 == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);

		// call objc_allocateClassPair
		Field field = OS.class.getField("class_NSObject");
		Object fieldObj = field.get(OS.class);

		Object[] args = makeArgs(fieldObj, "SWTCocoaEnhancerDelegate", wrapPointer(0));
		object = invokeMethod(OS.class, "objc_allocateClassPair", args);

		long cls = convertToLong(object);

		args = makeArgs(wrapPointer(cls), SWT_OBJECT, wrapPointer(size), new Byte((byte) align),
				types);
		invokeMethod(OS.class, "class_addIvar", args);

		// Add the action callback
		args = makeArgs(wrapPointer(cls), wrapPointer(sel_toolbarButtonClicked_),
				wrapPointer(proc3), "@:@");
		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		args = makeArgs(wrapPointer(cls), wrapPointer(sel_preferencesMenuItemSelected_),
				wrapPointer(proc3), "@:@");

		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		args = makeArgs(wrapPointer(cls), wrapPointer(sel_aboutMenuItemSelected_),
				wrapPointer(proc3), "@:@");

		invokeMethod(OS.class, "class_addMethod", args); //$NON-NLS-1$

		invokeMethod(OS.class, "objc_registerClassPair", makeArgs(cls));
	}

	SWTCocoaEnhancerDelegate delegate;
	private long delegateJniRef;

	/**
	 * Class that is able to intercept and handle OS events from the toolbar and
	 * menu.
	 * 
	 * @since 3.1
	 */

	private static final String RESOURCE_BUNDLE = CocoaUIEnhancer.class.getPackage().getName()
			+ ".Messages"; //$NON-NLS-1$

	private String fAboutActionName;
	private String fQuitActionName;
	private String fHideActionName;

	/**
	 * Default constructor
	 */
	public CocoaUIEnhancer() {

		String productName = getProductName();

		ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
		try {
			if (productName != null) {
				String format = resourceBundle.getString("AboutAction.format"); //$NON-NLS-1$
				if (format != null)
					fAboutActionName = MessageFormat.format(format, new Object[] { productName });
			}
			if (fAboutActionName == null)
				fAboutActionName = resourceBundle.getString("AboutAction.name"); //$NON-NLS-1$
		} catch (MissingResourceException e) {
		}

		if (fAboutActionName == null)
			fAboutActionName = "About"; //$NON-NLS-1$

		if (productName != null) {
			try {
				// prime the format Hide <app name>
				String format = resourceBundle.getString("HideAction.format"); //$NON-NLS-1$
				if (format != null)
					fHideActionName = MessageFormat.format(format, new Object[] { productName });

			} catch (MissingResourceException e) {
			}

			try {
				// prime the format Quit <app name>
				String format = resourceBundle.getString("QuitAction.format"); //$NON-NLS-1$
				if (format != null)
					fQuitActionName = MessageFormat.format(format, new Object[] { productName });

			} catch (MissingResourceException e) {
			}
		}

		try {
			if (sel_toolbarButtonClicked_ == 0) {
				sel_toolbarButtonClicked_ = registerName("toolbarButtonClicked:"); //$NON-NLS-1$
				sel_preferencesMenuItemSelected_ = registerName("preferencesMenuItemSelected:"); //$NON-NLS-1$
				sel_aboutMenuItemSelected_ = registerName("aboutMenuItemSelected:"); //$NON-NLS-1$
				init();
			}
		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private String getProductName() {
		if (Platform.getProduct() != null)
			return Platform.getProduct().getName();
		return Application.APP_NAME;
	}

	private long registerName(String name) throws IllegalArgumentException, SecurityException,
	IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class clazz = OS.class;
		Object object = invokeMethod(clazz, "sel_registerName", new Object[] { name });
		return convertToLong(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				startupInUI();
			}
		});
	}

	void log(Exception e) {
		StatusUtil.handleStatus(e, StatusManager.LOG);
	}

	/**
	 * Hooks a listener that tweaks newly opened workbench window shells with
	 * the proper OS flags.
	 * 
	 * @since 3.2
	 */
	protected void hookWorkbenchListener() {
		try {
			PlatformUI.getWorkbench().addWindowListener(new IWindowListener() {

				public void windowActivated(IWorkbenchWindow window) {
					// no-op
				}

				public void windowDeactivated(IWorkbenchWindow window) {
					// no-op
				}

				public void windowClosed(IWorkbenchWindow window) {
					// no-op
				}

				public void windowOpened(IWorkbenchWindow window) {
					modifyWindowShell(window);
				}
			});
		} catch (IllegalStateException exc) {
			// ignore - running an SWT app with no workbench
		}
	}

	/**
	 * Modify the given workbench window shell bits to show the tool bar toggle
	 * button.
	 * 
	 * @param window
	 *            the window to modify
	 * @since 3.2
	 */
	protected void modifyWindowShell(final IWorkbenchWindow window) {
		// only add the button when either the cool bar or perspective bar
		// is initially visible. This is so that RCP applications can choose to
		// use
		// this fragment without fear that their explicitly invisible bars
		// can't be shown.
		boolean coolBarInitiallyVsible = ((WorkbenchWindow) window).getCoolBarVisible();
		boolean perspectiveBarInitiallyVsible = ((WorkbenchWindow) window)
				.getPerspectiveBarVisible();

		if (coolBarInitiallyVsible || perspectiveBarInitiallyVsible) {
			createDummyToolbar(window);
		} else {
			// add the dummby toolbar when its shown for the first time
			if (!(window instanceof WorkbenchWindow))
				return;
			final WorkbenchWindow workbenchWindow = (WorkbenchWindow) window;
			workbenchWindow.addPropertyChangeListener(new IPropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent event) {
					if (WorkbenchWindow.PROP_COOLBAR_VISIBLE.equals(event.getProperty())) {
						createDummyToolbar(window);
						workbenchWindow.removePropertyChangeListener(this);
					}
				}
			});
		}
	}

	/**
	 * Add an empty, hidden tool bar to the window. Without this the tool bar
	 * button at the top right of the window will not appear even when
	 * setShowsToolbarButton(true) is called.
	 * 
	 * @param window
	 */
	private void createDummyToolbar(IWorkbenchWindow window) {
		NSToolbar dummyBar = new NSToolbar();
		dummyBar.alloc();
		dummyBar.initWithIdentifier(NSString.stringWith("SWTToolbar")); //$NON-NLS-1$
		dummyBar.setVisible(false);

		Shell shell = window.getShell();
		NSWindow nsWindow = shell.view.window();
		nsWindow.setToolbar(dummyBar);
		dummyBar.release();
		nsWindow.setShowsToolbarButton(true);

		// Override the target and action of the toolbar button so we can
		// control it.
		try {
			Object fieldValue = wrapPointer(NSWindowToolbarButton);
			NSButton toolbarButton = (NSButton) invokeMethod(NSWindow.class, nsWindow,
					"standardWindowButton", new Object[] { fieldValue });
			if (toolbarButton != null) {
				toolbarButton.setTarget(delegate);
				invokeMethod(NSControl.class, toolbarButton, "setAction",
						new Object[] { wrapPointer(sel_toolbarButtonClicked_) });
			}
		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void hookApplicationMenu() {
		try {
			// create About Eclipse menu command
			NSMenu mainMenu = NSApplication.sharedApplication().mainMenu();
			NSMenuItem mainMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, mainMenu,
					"itemAtIndex", new Object[] { wrapPointer(0) });
			NSMenu appMenu = mainMenuItem.submenu();

			// add the about action
			NSMenuItem aboutMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu,
					"itemAtIndex", new Object[] { wrapPointer(kAboutMenuItem) });
			aboutMenuItem.setTitle(NSString.stringWith(fAboutActionName));

			// rename the hide action if we have an override string
			if (fHideActionName != null) {
				NSMenuItem hideMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu,
						"itemAtIndex", new Object[] { wrapPointer(kHideApplicationMenuItem) });
				hideMenuItem.setTitle(NSString.stringWith(fHideActionName));
			}

			// rename the quit action if we have an override string
			if (fQuitActionName != null) {
				NSMenuItem quitMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu,
						"itemAtIndex", new Object[] { wrapPointer(kQuitMenuItem) });
				quitMenuItem.setTitle(NSString.stringWith(fQuitActionName));
			}

			// enable pref menu
			NSMenuItem prefMenuItem = (NSMenuItem) invokeMethod(NSMenu.class, appMenu,
					"itemAtIndex", new Object[] { wrapPointer(kPreferencesMenuItem) });
			prefMenuItem.setEnabled(true);

			// Register as a target on the prefs and quit items.
			prefMenuItem.setTarget(delegate);
			invokeMethod(NSMenuItem.class, prefMenuItem, "setAction",
					new Object[] { wrapPointer(sel_preferencesMenuItemSelected_) });
			aboutMenuItem.setTarget(delegate);
			invokeMethod(NSMenuItem.class, aboutMenuItem, "setAction",
					new Object[] { wrapPointer(sel_aboutMenuItemSelected_) });
		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void runCommand(String commandId) {

		IWorkbench workbench = PlatformUI.getWorkbench();
		IHandlerService service = (IHandlerService) workbench.getService(IHandlerService.class);

		try {
			service.executeCommand(commandId, null);
		} catch (ExecutionException e) {
			log(e);
		} catch (NotDefinedException e) {
			log(e);
		} catch (NotEnabledException e) {
			log(e);
		} catch (NotHandledException e) {
			log(e);
		}
	}

	/*
	 * Action implementations for the toolbar button and preferences and about
	 * menu items
	 */
	void toolbarButtonClicked(NSControl source) {
		try {
			NSWindow window = source.window();
			Field idField = NSWindow.class.getField("id");
			Object idValue = idField.get(window);

			Display display = Display.getCurrent();
			Widget widget = (Widget) invokeMethod(Display.class, display, "findWidget",
					new Object[] { idValue });

			if (!(widget instanceof Shell)) {
				return;
			}
			Shell shell = (Shell) widget;
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				if (windows[i].getShell() == shell) {
					runCommand("org.eclipse.ui.ToggleCoolbarAction"); //$NON-NLS-1$
				}
			}
		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void createDelegate() throws SecurityException, NoSuchMethodException,
	IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		delegate = new SWTCocoaEnhancerDelegate();
		delegate.alloc().init();
		// call OS.NewGlobalRef
		Method method = OS.class.getMethod("NewGlobalRef", new Class[] { Object.class });
		Object object = method.invoke(OS.class, new Object[] { CocoaUIEnhancer.this });
		delegateJniRef = convertToLong(object);
	}

	private Runnable createDisposer() {
		return new Runnable() {
			public void run() {
				if (delegateJniRef != 0) {
					try {
						invokeMethod(OS.class, "DeleteGlobalRef",
								new Object[] { CocoaUtil.wrapPointer(delegateJniRef) });
					} catch (Exception e) {
						// theoretically, one of
						// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
						// not expected to happen at all.
						log(e);
					}
				}
				delegateJniRef = 0;

				if (delegate != null)
					delegate.release();
				delegate = null;

			}
		};
	}

	/**
	 * 
	 */
	private void modifyShells() {
		try {
			// modify all shells opened on startup
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				modifyWindowShell(windows[i]);
			}
		} catch (IllegalStateException exc) {
			// ignore - running an SWT app with no Workbench
		}
	}

	private void startupInUI() {


		try {
			createDelegate();

			if (delegateJniRef == 0)
				SWT.error(SWT.ERROR_NO_HANDLES);

			setDelegate();

			hookApplicationMenu();
			hookWorkbenchListener();

			// schedule disposal of callback object
			Runnable disposer = createDisposer();
			Display.getDefault().disposeExec(disposer);

			modifyShells();

		} catch (Exception e) {
			// theoretically, one of
			// SecurityException,Illegal*Exception,InvocationTargetException,NoSuch*Exception
			// not expected to happen at all.
			log(e);
		}
	}

	private void setDelegate() throws NoSuchFieldException, IllegalAccessException,
	InvocationTargetException, NoSuchMethodException {
		Field idField = SWTCocoaEnhancerDelegate.class.getField("id");
		Object idValue = idField.get(delegate);

		Object[] args = makeArgs(idValue, SWT_OBJECT, wrapPointer(delegateJniRef));
		invokeMethod(OS.class, "object_setInstanceVariable", args);
	}

	static int actionProc(int id, int sel, int arg0) throws Exception {
		return (int) actionProc((long) id, (long) sel, (long) arg0);
	}

	static long actionProc(long id, long sel, long arg0) throws Exception {
		long[] jniRef = OS_object_getInstanceVariable(id, SWT_OBJECT);
		if (jniRef[0] == 0)
			return 0;

		CocoaUIEnhancer delegate = (CocoaUIEnhancer) invokeMethod(OS.class, "JNIGetObject",
				new Object[] { wrapPointer(jniRef[0]) });

		if (sel == sel_toolbarButtonClicked_) {
			NSControl source = new_NSControl(arg0);
			delegate.toolbarButtonClicked(source);
		} else if (sel == sel_preferencesMenuItemSelected_) {
			showPreferences();
		} else if (sel == sel_aboutMenuItemSelected_) {
			showAbout();
		}

		return 0;
	}

	private static void showAbout() {
		MessageDialog.openInformation(null, "About...", "Replace with a proper about text  / dialog");
		// delegate.runCommand(ActionFactory.ABOUT.getCommandId());
	}

	private static void showPreferences() {
		System.out.println("Preferences...");
		PreferenceManager manager = new PreferenceManager();
		PreferenceDialog dialog = new PreferenceDialog(null, manager);
		dialog.open();
		// delegate.runCommand(ActionFactory.PREFERENCES.getCommandId());
	}
	
}
