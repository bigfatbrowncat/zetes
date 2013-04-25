package crossbase.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;

public abstract class ViewWindowBase implements ViewWindow
{
	private Shell shell;
	
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
	
	public void open()
	{
		createContents();
		
		shell.layout();
		shell.open();
	}
	
	protected void createContents()
	{
		shell = new Shell();
		if (SWT.getPlatform().equals("cocoa"))
		{
			setCocoaFullscreenButton(supportsFullscreen());
		}
	}
}
