package crossbase.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import crossbase.abstracts.Application;
import crossbase.abstracts.Document;
import crossbase.abstracts.MenuConstructor;
import crossbase.abstracts.ViewWindow;

public abstract class ViewWindowBase implements ViewWindow
{
	private Application application;
	private ViewWindowsManager<? extends ViewWindowBase> windowsManager;
	
	private Shell shell;
	private MenuConstructor menuConstructor;
	
	@SuppressWarnings("rawtypes")
	private void setCocoaFullscreenButton(boolean on)
	{
		try
		{
			Field field = Control.class.getDeclaredField("view");
			Object /*NSView*/ view = field.get(shell);
	
			if (view != null)
			{
			    Class<?> c = Class.forName("org.eclipse.swt.internal.cocoa.NSView");
			    Object /*NSWindow*/ window = c.getDeclaredMethod("window").invoke(view);
	
			    c = Class.forName("org.eclipse.swt.internal.cocoa.NSWindow");
			    Method setCollectionBehavior = c.getDeclaredMethod(
			        "setCollectionBehavior", long.class);
			    setCollectionBehavior.invoke(window, on ? (1 << 7) : 0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected final Shell getShell()
	{
		return shell;
	}
	
	public ViewWindowBase(Application application, ViewWindowsManager<? extends ViewWindowBase> windowsManager)
	{
		this.application = application;
		this.windowsManager = windowsManager;
	}
	
	@Override
	public final Menu getMenu()
	{
		return shell.getMenuBar();
	}
	
	@Override
	public final void open()
	{
		constructShell();

		menuConstructor.addWindow(this);		
		
		shell.layout();
		shell.open();
	}
	
	@Override
	public final boolean isActive()
	{
		if (Display.getCurrent() != null && !Display.getCurrent().isDisposed())
		{
			return Display.getCurrent().getActiveShell() == shell;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public final void activate(boolean force) 
	{
		shell.setMinimized(false);
		if (force)
		{
			shell.forceActive();
		}
		else
		{
			shell.setActive();
		}
	}
	
	@Override
	public final void toggleMinimized()
	{
		shell.setMinimized(!shell.getMinimized());
	}
	
	@Override
	public final void toggleMaximized()
	{
		if (shell.getFullScreen()) shell.setFullScreen(false);
		shell.setMaximized(!shell.getMaximized());
	}
	
	@Override
	public final void toggleFullScreen()
	{
		if (shell.getMaximized()) shell.setMaximized(false);
		shell.setFullScreen(!shell.getFullScreen());
	}
	
	
	protected void constructShell()
	{
		shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);
		getShell().setText(application.getTitle());

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		if (SWT.getPlatform().equals("cocoa"))
		{
			setCocoaFullscreenButton(supportsFullscreen());
		}
		
		shell.addShellListener(new ShellListener()
		{
			
			@Override
			public void shellIconified(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
				// TODO Add custom event
			}
			
			@Override
			public void shellDeiconified(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
				// TODO Add custom event
			}
			
			@Override
			public void shellDeactivated(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
				// TODO Add custom event
			}
			
			@Override
			public void shellActivated(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
				// TODO Add custom event
			}
			
			@Override
			public void shellClosed(ShellEvent arg0)
			{
				// If we want this to work, we should guarantee that the generic parameter type of windowsManager equals to our type
				((ViewWindowsManager)windowsManager).closeWindow(ViewWindowBase.this);
			}
		});
		
		shell.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.removeWindow(ViewWindowBase.this);
				
				Document doc = getDocument();
				if (doc != null) 
				{
					doc.dispose();
				}
			}
		});
	}

	public final MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}

	@Override
	public final void setMenuConstructor(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}
	
	public Application getApplication()
	{
		return application;
	}
}
