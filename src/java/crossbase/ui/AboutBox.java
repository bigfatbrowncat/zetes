package crossbase.ui;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class AboutBox extends Dialog
{

	protected Object result;
	protected Shell aboutBoxShell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutBox(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
		setText("SWT Dialog");
	}

	private void centerWindow()
	{
        // Move the dialog to the center of the top level shell.
        Rectangle shellBounds = getParent().getBounds();
        Point dialogSize = aboutBoxShell.getSize();

        aboutBoxShell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
        					shellBounds.y + (shellBounds.height - dialogSize.y) / 2);	
   	}
	
	private void fixButtonFont(Control button)
	{
		if (SWT.getPlatform().equals("cocoa"))
		{
			// Making the button text a bit more like on native buttons
			Font defaultFont = button.getFont();
			FontData fontData = defaultFont.getFontData()[0];
			fontData.height ++;
			
			Font newFont = new Font(button.getDisplay(), fontData);
			button.setFont(newFont);
		}		
	}

	private void updateTitleFont(Control label)
	{
		// Making the button
		Font defaultFont = label.getFont();
		FontData fontData = defaultFont.getFontData()[0];
		fontData.height ++;
		fontData.setStyle(fontData.getStyle() | SWT.BOLD);
		
		Font newFont = new Font(label.getDisplay(), fontData);
		label.setFont(newFont);
	}
	
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		if (this.aboutBoxShell == null)
		{
			createContents();
			centerWindow();
	
			aboutBoxShell.open();
			aboutBoxShell.layout();
			Display display = getParent().getDisplay();
			while (!aboutBoxShell.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
			aboutBoxShell = null;
			return result;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void createContents()
	{
		aboutBoxShell = new Shell(getParent(), getStyle());
		aboutBoxShell.setSize(360, 147);
		aboutBoxShell.setText("About SWT Application");
		aboutBoxShell.setLayout(new FormLayout());
		

 		Label iconLabel = new Label(aboutBoxShell, SWT.NONE);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(SWTResourceManager.getImage(AboutBox.class, "/crossbase/icon.png"));
		FormData fd_iconLabel = new FormData();
		fd_iconLabel.top = new FormAttachment(0, 10);
		fd_iconLabel.left = new FormAttachment(0, 10);
		iconLabel.setLayoutData(fd_iconLabel);
		iconLabel.setSize(64, 64);
		
		Button okButton = new Button(aboutBoxShell, SWT.NONE);
		fixButtonFont(okButton);
		
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				aboutBoxShell.close();
			}
		});
		FormData fd_okButton = new FormData();
		fd_okButton.width = 90;
		fd_okButton.right = new FormAttachment(100, -10);
		fd_okButton.bottom = new FormAttachment(100, -10);
		okButton.setLayoutData(fd_okButton);
		okButton.setText("OK");
		aboutBoxShell.setDefaultButton(okButton);
		
		Label descriptionLabel = new Label(aboutBoxShell, SWT.NONE);
		FormData fd_descriptionLabel = new FormData();
		fd_descriptionLabel.left = new FormAttachment(iconLabel, 10);
		fd_descriptionLabel.right = new FormAttachment(100, -10);
		descriptionLabel.setLayoutData(fd_descriptionLabel);
		descriptionLabel.setText("This app demonstrates Avian + SWT power");
		
		Label copyrightLabel = new Label(aboutBoxShell, SWT.NONE);
		fd_descriptionLabel.bottom = new FormAttachment(copyrightLabel, -6);
		copyrightLabel.setText("Copyright Ilya Mizus, 2013");
		FormData fd_copyrightLabel = new FormData();
		fd_copyrightLabel.bottom = new FormAttachment(okButton, -6);
		fd_copyrightLabel.left = new FormAttachment(0, 44);
		fd_copyrightLabel.right = new FormAttachment(100, -10);
		copyrightLabel.setLayoutData(fd_copyrightLabel);
		
		Label titleLabel = new Label(aboutBoxShell, SWT.NONE);
		fd_descriptionLabel.top = new FormAttachment(titleLabel, 6);
		//titleLabel.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.BOLD));
		updateTitleFont(titleLabel);
		titleLabel.setText("CrossBase SWT Application");
		FormData fd_titleLabel = new FormData();
		fd_titleLabel.left = new FormAttachment(iconLabel, 10);
		fd_titleLabel.right = new FormAttachment(100, -10);
		fd_titleLabel.top = new FormAttachment(0, 10);
		titleLabel.setLayoutData(fd_titleLabel);

		if (SWT.getPlatform().equals("gtk"))
		{
			Point size = aboutBoxShell.getSize();
			
			aboutBoxShell.setSize(new Point((int)(size.x * 1.2f), (int)(size.y * 1.2f)));
		}
	}
}
