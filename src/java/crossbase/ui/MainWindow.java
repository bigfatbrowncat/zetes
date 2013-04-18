package crossbase.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

public class MainWindow
{

	protected Shell shell;
	private Label imageLabel;
	private Composite imageContainerComposite;
	private ScrolledComposite scrolledComposite;
	
	private MenuItem helpMenuItem;
	
	private AboutBox aboutBox;

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
	public void open(boolean fullscreenEnabled)
	{
		Display display = Display.getDefault();
		createContents();
		
		if (SWT.getPlatform().equals("cocoa"))
		{
			setCocoaFullscreenButton(fullscreenEnabled);
		}
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private DropTargetAdapter fileDropTargetAdapter = new DropTargetAdapter() {
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				if (fileList.length > 0)
				{
					openImageFile(fileList[0]);
				}
			}
		}
	};
	
	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setMinimumSize(new Point(150, 200));
		shell.setImage(SWTResourceManager.getImage(MainWindow.class,
				"/crossbase/icon.png"));
		
		aboutBox = new AboutBox(shell);

		//shell.setSize(480, 360);
		
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmFile_1 = new MenuItem(menu, SWT.CASCADE);
		mntmFile_1.setText("&File");

		Menu menu_1 = new Menu(mntmFile_1);
		mntmFile_1.setMenu(menu_1);

		// "Open" menu item
		MenuItem mainMenuItemOpen = new MenuItem(menu_1, SWT.NONE);
		mainMenuItemOpen.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
				fileDialog.setText("Open image");
				fileDialog.setFilterNames(new String[] { "Image (*.png; *.bmp; *.jpg; *.jpeg)", "All files" });
				fileDialog.setFilterExtensions(new String[] { "*.png; *.bmp; *.jpg; *.jpeg", "*.*" });
				String fileName = fileDialog.open();
				if (fileName != null)
				{
					openImageFile(fileName);
				}
			}
		});
		
		HotKey openHotKey = new HotKey(HotKey.MOD1, 'O');
		mainMenuItemOpen.setText("&Open...\t" + openHotKey.toString());
		mainMenuItemOpen.setAccelerator(openHotKey.toAccelerator());
		
		if (!SWT.getPlatform().equals("cocoa"))
		{
			new MenuItem(menu_1, SWT.SEPARATOR);

			// "Exit" menu item
			MenuItem mainMenuItemExit = new MenuItem(menu_1, SWT.NONE);
			mainMenuItemExit.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					userClose();
				}
			});

			mainMenuItemExit.setText("E&xit");
			
			// "Help" menu item
			helpMenuItem = new MenuItem(menu, SWT.CASCADE);
			helpMenuItem.setText("&Help");
			
			Menu menu_2 = new Menu(helpMenuItem);
			helpMenuItem.setMenu(menu_2);
			
			// "About" menu item
			MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
			mntmAbout.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					userAbout();
				}
			});
			mntmAbout.setText("&About...");
		}
		
		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		imageContainerComposite = new Composite(scrolledComposite, SWT.NONE);
		imageContainerComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		imageContainerComposite.setLayout(null);
		DropTarget imageContainerDropTarget = new DropTarget(imageContainerComposite, DND.DROP_DEFAULT	| DND.DROP_MOVE);
		imageContainerDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		imageContainerDropTarget.addDropListener(fileDropTargetAdapter);
		
		
		imageLabel = new Label(imageContainerComposite, SWT.NONE);
		imageLabel.setImage(null);
		imageLabel.setAlignment(SWT.CENTER);
		imageLabel.setBounds(0, 0, 50, 50);
		imageLabel.setVisible(false);
		DropTarget imageLabelDropTarget = new DropTarget(imageLabel, DND.DROP_DEFAULT	| DND.DROP_MOVE);
		imageLabelDropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		imageLabelDropTarget.addDropListener(fileDropTargetAdapter);

		
		scrolledComposite.setContent(imageContainerComposite);
		scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	public void openImageFile(String fileName)
	{
		try
		{
			System.out.println("Opening " + fileName);
			Image oldImage = imageLabel.getImage();
			imageLabel.setImage(loadImage(fileName));
			imageLabel.setSize(imageLabel.getImage().getImageData().width, imageLabel.getImage().getImageData().height);
			imageLabel.setVisible(true);
			scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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

	public void userClose()
	{
		shell.close();
	}

	public void userAbout()
	{
		aboutBox.open();		
	}
	
	public void userPreferences()
	{
		// TODO Implement preferences window
	}
}
