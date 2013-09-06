package tinyviewer;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.ui.ViewWindowBase;


public class ImageViewWindow extends ViewWindowBase<ImageDocument>
{
	private ScrolledComposite scrolledComposite;
	private DropTarget scrolledCompositeDropTarget, imageViewDropTarget;
	private ImageView imageView;
	private HashSet<DropTargetAdapter> dropTargetAdapters = new HashSet<DropTargetAdapter>();

	public void addDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.add(dropTargetAdapter);
		
		if (imageViewDropTarget != null && !imageViewDropTarget.isDisposed())
		{
			scrolledCompositeDropTarget.addDropListener(dropTargetAdapter);
			imageViewDropTarget.addDropListener(dropTargetAdapter);
		}
	}
	
	public void removeDropTargetListener(DropTargetAdapter dropTargetAdapter)
	{
		dropTargetAdapters.remove(dropTargetAdapter);
		if (imageViewDropTarget != null && !imageViewDropTarget.isDisposed())
		{
			scrolledCompositeDropTarget.removeDropListener(dropTargetAdapter);
			imageViewDropTarget.removeDropListener(dropTargetAdapter);
		}
	}
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.BORDER | SWT.DOUBLE_BUFFERED);

		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		shell.setMinimumSize(new Point(150, 200));

		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(ImageViewWindow.class, "/tinyviewer/wingphotos16.png"),		// Necessary in Windows (for taskbar)
				SWTResourceManager.getImage(ImageViewWindow.class, "/tinyviewer/wingphotos32.png"),		// Necessary in Windows (for Alt-Tab)
				SWTResourceManager.getImage(ImageViewWindow.class, "/tinyviewer/wingphotos512.png")		// Necessary in OS X
		});
	
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_BACKGROUND);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		imageView = new ImageView(scrolledComposite, SWT.NONE);
		imageView.setBounds(0, 0, 200, 127);
		imageView.setVisible(false);
		imageView.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));

		// Drop targets
		scrolledCompositeDropTarget = new DropTarget(scrolledComposite, DND.DROP_MOVE);
		scrolledCompositeDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		imageViewDropTarget = new DropTarget(imageView, DND.DROP_MOVE);
		imageViewDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		for (DropTargetAdapter adapter : dropTargetAdapters)
		{
			scrolledCompositeDropTarget.addDropListener(adapter);
			imageViewDropTarget.addDropListener(adapter);
		}
		
		scrolledComposite.setContent(imageView);
		scrolledComposite.setMinSize(imageView.desiredSize());
		scrolledComposite.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		scrolledComposite.addControlListener(new ControlListener()
		{
			@Override
			public void controlResized(ControlEvent arg0)
			{
				updateImageViewSize();
				
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) { }
		});
			
		return shell;
	}

	private void updateImageViewSize()
	{
		Point desired = imageView.desiredSize();
		Point clientAreaSize = new Point(scrolledComposite.getClientArea().width, scrolledComposite.getClientArea().height); 
		
		int width = Math.max(clientAreaSize.x, desired.x); 
		int height = Math.max(clientAreaSize.y, desired.y);
		Point newSize = new Point(width, height);
		
		Point oldSize = imageView.getSize();
		
		if (!oldSize.equals(newSize))
		{
			imageView.setSize(newSize);
		}
	}
	
	@Override
	public void setDocument(ImageDocument document)
	{
		super.setDocument(document);
		
		imageView.setImage(getDocument().getImage());
		scrolledComposite.setMinSize(imageView.desiredSize());
		updateImageViewSize();
		imageView.setVisible(true);
		getShell().forceActive();
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
