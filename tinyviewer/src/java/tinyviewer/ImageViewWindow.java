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
import crossbase.ui.abstracts.Document;
import crossbase.ui.abstracts.MenuConstructor;
import crossbase.ui.abstracts.ViewWindow;
import crossbase.ui.abstracts.ViewWindowClosedListener;

public class ImageViewWindow extends ViewWindowBase
{
	private Composite imageContainerComposite;
	private ScrolledComposite scrolledComposite;
	private DropTarget imageContainerDropTarget, imageViewDropTarget;
	private ImageView imageView;
	private ImageDocument imageDocument;

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
	
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected void createContents()
	{
		super.createContents();
		
		getShell().setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		getShell().setMinimumSize(new Point(150, 200));
		getShell().setImage(SWTResourceManager.getImage(ImageViewWindow.class,
				"/crossbase/icon.png"));
	
		getShell().setText(Application.APP_NAME);
		getShell().setLayout(new FillLayout(SWT.HORIZONTAL));

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
	public void loadDocument(Document document)
	{
		this.imageDocument = (ImageDocument)document;
		
		imageView.setImage(imageDocument.getImage());
		getShell().setText(imageDocument.getTitle() + " \u2013 " + Application.APP_NAME);
		imageView.setSize(imageView.getImage().getImageData().width, imageView.getImage().getImageData().height);
		imageView.setVisible(true);
		scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		getShell().forceActive();
		getMenuConstructor().updateMenus();
	}
	
	@Override
	public boolean documentIsLoaded()
	{
		return imageView.getImage() != null;
	}
	
	public boolean isDisposed()
	{
		return getShell().isDisposed();
	}

	@Override
	public ImageDocument getDocument()
	{
		return imageDocument;
	}

	@Override
	public boolean supportsFullscreen()
	{
		return true;
	}
}
