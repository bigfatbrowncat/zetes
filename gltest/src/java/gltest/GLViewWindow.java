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
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.wb.swt.SWTResourceManager;

public class GLViewWindow extends ViewWindowBase<GLDocument>
{
	private static native void initCube();
	private static native void redrawCube(double angle);
	
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
		Shell shell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		
		Point size = shell.getSize();
		Point clientSize = new Point(shell.getClientArea().width, shell.getClientArea().height);
		
		shell.setSize(size.x - clientSize.x + 640, size.y - clientSize.y + 480);
		
		shell.setLayout(new FillLayout());
		Composite comp = new Composite(shell, SWT.NONE);
		comp.setLayout(new FillLayout());
		GLData data = new GLData ();
		data.doubleBuffer = true;
		final GLCanvas canvas = new GLCanvas(comp, SWT.NONE, data);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));


		/*canvas.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent arg0) {
				//canvas.redraw();
				
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		canvas.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent arg0) {

				if (!canvas.isCurrent()) canvas.setCurrent();
				initCube();
				redrawCube(angle);
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

		/*try {
			GLContext.useContext(canvas);
		} catch(LWJGLException e) { e.printStackTrace(); }

		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				Rectangle bounds = canvas.getBounds();
				float fAspect = (float) bounds.width / (float) bounds.height;
				canvas.setCurrent();
				try {
					GLContext.useContext(canvas);
				} catch(LWJGLException e) { e.printStackTrace(); }
				GL11.glViewport(0, 0, bounds.width, bounds.height);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GLU.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
			}
		});

		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		GL11.glClearDepth(1.0);
		GL11.glLineWidth(2);
		GL11.glEnable(GL11.GL_DEPTH_TEST);*/
		
		return shell;
	}
	
	@Override
	public boolean documentIsLoaded()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadDocument(GLDocument document)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public GLDocument getDocument()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsFullscreen()
	{
		return false;
	}
	
	@Override
	public boolean supportsMaximizing()
	{
		return false;
	}
}
