package tinyviewer;

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
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import crossbase.ui.ViewWindowBase;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;
import crossbase.ui.abstracts.ViewWindowClosedListener;

public class ImageViewWindow extends ViewWindowBase
{
	private String fileName = null;
	private Composite imageContainerComposite;
	private ScrolledComposite scrolledComposite;
	private DropTarget imageContainerDropTarget, imageViewDropTarget;
	private ViewWindowClosedListener<ImageViewWindow> closedListener;
	private ImageView imageView;
	private MenuConstructor menuConstructor;
	

	protected static Image loadImage(InputStream stream) throws IOException {
		try {
			Display display = Display.getDefault();
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void setClosedListener(ViewWindowClosedListener<? extends ViewWindow> documentWindowClosedListener)
	{
		this.closedListener = (ViewWindowClosedListener<ImageViewWindow>)documentWindowClosedListener;		
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected void createContents()
	{
		super.createContents();
		getShell().addShellListener(new ShellListener()
		{
			
			@Override
			public void shellIconified(ShellEvent arg0)
			{
				ImageViewWindow.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellDeiconified(ShellEvent arg0)
			{
				ImageViewWindow.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellDeactivated(ShellEvent arg0)
			{
				ImageViewWindow.this.menuConstructor.updateMenus();
			}
			
			@Override
			public void shellClosed(ShellEvent arg0)
			{
				if (closedListener != null) closedListener.windowClosed(ImageViewWindow.this);
			}
			
			@Override
			public void shellActivated(ShellEvent arg0)
			{
				ImageViewWindow.this.menuConstructor.updateMenus();
			}
		});
		
		getShell().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				ImageViewWindow.this.menuConstructor.removeWindow(ImageViewWindow.this);
				Image oldImage = imageView.getImage();
				if (oldImage != null) 
				{
					oldImage.dispose();
				}
			}
		});
		
		getShell().setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		getShell().setMinimumSize(new Point(150, 200));
		getShell().setImage(SWTResourceManager.getImage(ImageViewWindow.class,
				"/crossbase/icon.png"));
	
		getShell().setText(Application.APP_NAME);
		getShell().setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(getShell(), SWT.BAR);
		getShell().setMenuBar(menu);

		menuConstructor.addWindow(this);
		
		scrolledComposite = new ScrolledComposite(getShell(), SWT.H_SCROLL | SWT.V_SCROLL);
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

	@Override
	public void loadFile(String fileName)
	{
		try
		{
			Image oldImage = imageView.getImage();
			imageView.setImage(loadImage(fileName));
			this.fileName = fileName;
			getShell().setText(fileName + " \u2013 " + Application.APP_NAME);
			imageView.setSize(imageView.getImage().getImageData().width, imageView.getImage().getImageData().height);
			imageView.setVisible(true);
			scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			getShell().forceActive();
			if (oldImage != null) oldImage.dispose();
			menuConstructor.updateMenus();
		}
		catch (IOException e)
		{
			MessageBox cantOpenFileMessageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
			cantOpenFileMessageBox.setMessage("Can't open file: " + fileName);
			cantOpenFileMessageBox.setText("Error");
			cantOpenFileMessageBox.open();
		}
		catch (SWTException e)
		{
			MessageBox cantOpenFileMessageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
			cantOpenFileMessageBox.setMessage("Incorrect image format: " + fileName);
			cantOpenFileMessageBox.setText("Error");
			cantOpenFileMessageBox.open();			
		}
	}
	
	@Override
	public boolean isOccupied()
	{
		return imageView.getImage() != null;
	}
	
	public boolean isDisposed()
	{
		return getShell().isDisposed();
	}

	@Override
	public String getDocumentTitle()
	{
		return fileName;
	}

	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}
	
	public MenuConstructor getMenuConstructor() {
		return menuConstructor;
	}

	public void setMenuConstructor(MenuConstructor menuConstructor) {
		this.menuConstructor = menuConstructor;
	}

	
}
