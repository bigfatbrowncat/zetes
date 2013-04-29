package crossbase.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import crossbase.ui.abstracts.Document;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;
import crossbase.ui.abstracts.ViewWindowClosedListener;

public abstract class ViewWindowBase implements ViewWindow
{
	private Shell shell;
	private MenuConstructor menuConstructor;
	private ViewWindowClosedListener closedListener;
	
	private void setCocoaFullscreenButton(boolean on)
	{
		try
		{
			Field field = Control.class.getDeclaredField("view");
			Object /*NSView*/ view = field.get(getShell());
	
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
	
	public ViewWindowBase()
	{

	}
	
	@Override
	public Shell getShell()
	{
		return shell;
	}
	
	@Override
	public void open()
	{
		createContents();
		
		shell.layout();
		shell.open();
	}
	
	protected void createContents()
	{
		shell = new Shell();
		Menu menu = new Menu(getShell(), SWT.BAR);
		getShell().setMenuBar(menu);

		if (SWT.getPlatform().equals("cocoa"))
		{
			setCocoaFullscreenButton(supportsFullscreen());
		}
		
		getShell().addShellListener(new ShellListener()
		{
			
			@Override
			public void shellIconified(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellDeiconified(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellDeactivated(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellClosed(ShellEvent arg0)
			{
				if (closedListener != null) closedListener.windowClosed(ViewWindowBase.this);
			}
			
			@Override
			public void shellActivated(ShellEvent arg0)
			{
				ViewWindowBase.this.menuConstructor.updateMenus();
			}
		});
		
		getShell().addDisposeListener(new DisposeListener()
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
		menuConstructor.addWindow(this);		
	}

	public ViewWindowClosedListener getClosedListener()
	{
		return closedListener;
	}

	@Override
	public void setClosedListener(ViewWindowClosedListener closedListener)
	{
		this.closedListener = closedListener;
	}
	
	public MenuConstructor getMenuConstructor()
	{
		return menuConstructor;
	}

	@Override
	public void setMenuConstructor(MenuConstructor menuConstructor)
	{
		this.menuConstructor = menuConstructor;
	}

}
