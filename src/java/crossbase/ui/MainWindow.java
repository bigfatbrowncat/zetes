package crossbase.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
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
	
	private boolean aboutInHelpMenu;
	private MenuItem helpMenuItem;

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
	
	public MainWindow(boolean aboutInHelpMenu)
	{
		this.aboutInHelpMenu = aboutInHelpMenu;
	}
	
	/**
	 * Open the window.
	 */
	public void open()
	{
		Display display = Display.getDefault();
		createContents();
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

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents()
	{
		shell = new Shell();
		shell.setMinimumSize(new Point(150, 200));
		shell.setImage(SWTResourceManager.getImage(MainWindow.class,
				"/crossbase/icon.png"));

		shell.setSize(450, 450);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmFile_1 = new MenuItem(menu, SWT.CASCADE);
		mntmFile_1.setText("File");

		Menu menu_1 = new Menu(mntmFile_1);
		mntmFile_1.setMenu(menu_1);

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
					try
					{
						Image oldImage = imageLabel.getImage();
						imageLabel.setImage(loadImage(fileName));
						imageLabel.setSize(imageLabel.getImage().getImageData().width, imageLabel.getImage().getImageData().height);
						scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
						if (oldImage != null) oldImage.dispose();
					} 
					catch (IOException e)
					{
						MessageBox cantOpenFileMessageBox = new MessageBox(shell);
						cantOpenFileMessageBox.setMessage("Can't open the specified file: " + fileName);
					}
				}
			}
		});
		mainMenuItemOpen.setText("Open...");

		MenuItem mainMenuItemExit = new MenuItem(menu_1, SWT.NONE);
		mainMenuItemExit.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				shell.close();
			}
		});

		mainMenuItemExit.setText("Exit");
		
		if (aboutInHelpMenu)
		{
			helpMenuItem = new MenuItem(menu, SWT.CASCADE);
			helpMenuItem.setText("Help");
			
			Menu menu_2 = new Menu(helpMenuItem);
			helpMenuItem.setMenu(menu_2);
			
			MenuItem mntmAbout = new MenuItem(menu_2, SWT.NONE);
			mntmAbout.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					AboutBox aboutBox = new AboutBox(shell);
					aboutBox.open();
				}
			});
			mntmAbout.setText("About...");
		}
		
		scrolledComposite = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		imageContainerComposite = new Composite(scrolledComposite, SWT.NONE);
		imageContainerComposite.setLayout(null);
		
		imageLabel = new Label(imageContainerComposite, SWT.NONE);
		imageLabel.setImage(null);
		imageLabel.setAlignment(SWT.CENTER);
		imageLabel.setBounds(0, 0, 49, 45);
		scrolledComposite.setContent(imageContainerComposite);
		scrolledComposite.setMinSize(imageContainerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}
}
