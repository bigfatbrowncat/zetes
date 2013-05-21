package gltest;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import crossbase.abstracts.MenuConstructor;
import crossbase.ui.ViewWindowBase;
import crossbase.ui.ViewWindowsManager;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.CrossBaseGLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.wb.swt.SWTResourceManager;

public class GLViewWindow extends ViewWindowBase<GLDocument>
{
	private static native void initScene();
	private static native void resizeView(int width, int height);
	private static native void drawScene(double angle);
	
	private double angle = 0;
	
	public GLViewWindow(
			String applicationTitle,
			ViewWindowsManager<GLDocument, ? extends ViewWindowBase<GLDocument>> windowsManager,
			MenuConstructor<GLDocument, ? extends ViewWindowBase<GLDocument>> menuConstructor)
	{
		super(applicationTitle, windowsManager, menuConstructor);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE | SWT.MAX | SWT.RESIZE);
		
		Point size = shell.getSize();
		Point clientSize = new Point(shell.getClientArea().width, shell.getClientArea().height);
		
		shell.setSize(size.x - clientSize.x + 800, size.y - clientSize.y + 600);
		if (SWT.getPlatform().equals("win32"))
		{
			shell.setImage(SWTResourceManager.getImage(GLViewWindow.class, "/gltest/gltest16.png"));
		}
		else
		{
			shell.setImage(SWTResourceManager.getImage(GLViewWindow.class, "/gltest/gltest64.png"));
		}
		
		shell.setLayout(new FillLayout());
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new FillLayout());
		GLData data = new GLData ();
		data.doubleBuffer = true;
		
		final CrossBaseGLCanvas canvas = new CrossBaseGLCanvas(comp, SWT.NO_BACKGROUND, data);
		
		if (!canvas.isCurrent()) canvas.setCurrent();
		initScene();
		
		canvas.addControlListener(new ControlListener()
		{
			
			@Override
			public void controlResized(ControlEvent arg0)
			{
				if (!canvas.isCurrent()) canvas.setCurrent();
				Point size = canvas.getSize();
				resizeView(size.x, size.y);
			}
			
			@Override
			public void controlMoved(ControlEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		canvas.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent arg0)
			{
				if (!canvas.isCurrent()) canvas.setCurrent();
				drawScene(angle);
				canvas.swapBuffers();
			}
		});
		
		Runnable timerUpdateRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				if (canvas != null && !canvas.isDisposed())
				{
					canvas.redraw();
					angle += 0.01;
					Display.getCurrent().timerExec(20, this);
				}
			}
		};
		
		timerUpdateRunnable.run();			

		return shell;
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}
	
	@Override
	public boolean supportsMaximizing()
	{
		return true;
	}
}
