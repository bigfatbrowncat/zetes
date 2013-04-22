package crossbase.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import crossbase.ui.AboutBox;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.DragDetectEvent;

public class ViewWindow
{
	protected Shell shell;
	private Composite imageContainerComposite;
	private ScrolledComposite scrolledComposite;
	private DropTarget imageContainerDropTarget, imageViewDropTarget;
	private ViewWindowClosedListener closedListener;
	private ImageView imageView;
		
	protected static Image loadImage(InputStream stream) throws IOException {
		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}
	
	protected static Image loadImage(String fileName) throws IOException
	{
		return loadImage(new FileInputStream(fileName));
	}
	
	
	/**
	 * Open the window.
	 */
	public void open(MenuConstructor menuConstructor)
	{
		Display display = Display.getDefault();
		createContents(menuConstructor);
		
		if (SWT.getPlatform().equals("cocoa"))
		{
			setCocoaFullscreenButton(true);
		}
		
		shell.open();
		shell.layout();
	}

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
	
	public void addDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		imageContainerDropTarget.addDropListener(dropTargetAdapter);
		imageViewDropTarget.addDropListener(dropTargetAdapter);
	}
	
	public void removeDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		imageContainerDropTarget.removeDropListener(dropTargetAdapter);
		imageViewDropTarget.removeDropListener(dropTargetAdapter);
	}
	
	public void setClosedListener(ViewWindowClosedListener closedListener)
	{
		this.closedListener = closedListener;
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents(MenuConstructor menuConstructor)
	{
		shell = new Shell();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				Image oldImage = imageView.getImage();
				if (oldImage != null) 
				{
					oldImage.dispose();
				}
				if (closedListener != null) closedListener.windowClosed(ViewWindow.this);
			}
		});
		
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setMinimumSize(new Point(150, 200));
		shell.setImage(SWTResourceManager.getImage(ViewWindow.class,
				"/crossbase/icon.png"));
	
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		// Creating "File" menu
		menuConstructor.appendMenusToShell(shell);
		
		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		imageContainerComposite = new Composite(scrolledComposite, SWT.NONE);
		imageContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		imageContainerComposite.setLayout(null);
		imageContainerDropTarget = new DropTarget(imageContainerComposite, DND.DROP_MOVE);
		imageContainerDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		imageView = new ImageView(imageContainerComposite, SWT.NONE);
		imageView.setBounds(0, 0, 200, 127);
		imageView.setVisible(false);
		imageViewDropTarget = new DropTarget(imageView, DND.DROP_MOVE);
		imageViewDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });

		scrolledComposite.setContent(imageContainerComposite);
		scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void openImageFile(String fileName)
	{
		try
		{
			System.out.println("Opening " + fileName);
			Image oldImage = imageView.getImage();
			imageView.setImage(loadImage(fileName));
			shell.setText(fileName);
			imageView.setSize(imageView.getImage().getImageData().width, imageView.getImage().getImageData().height);
			imageView.setVisible(true);
			scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			shell.forceActive();
			if (oldImage != null) oldImage.dispose();
		}
		catch (IOException e)
		{
			MessageBox cantOpenFileMessageBox = new MessageBox(shell, SWT.ICON_ERROR);
			cantOpenFileMessageBox.setMessage("Can't open file: " + fileName);
			cantOpenFileMessageBox.setText("Error");
			cantOpenFileMessageBox.open();
		}
		catch (SWTException e)
		{
			MessageBox cantOpenFileMessageBox = new MessageBox(shell, SWT.ICON_ERROR);
			cantOpenFileMessageBox.setMessage("Incorrect image format: " + fileName);
			cantOpenFileMessageBox.setText("Error");
			cantOpenFileMessageBox.open();			
		}
	}
	
	public boolean isOccupied()
	{
		return imageView.getImage() != null;
	}
	
	public boolean isDisposed()
	{
		return shell.isDisposed();
	}
}
