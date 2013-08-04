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
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class DefaultAboutBox extends Dialog
{
	protected Shell aboutBoxShell;
	private String applicationName = "", descriptionText = "", copyrightText = "", iconResourceName = "";
	private Point windowSize = new Point(370, 160);

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public DefaultAboutBox(Shell parent)
	{
		super(parent, SWT.DIALOG_TRIM | SWT.CENTER | SWT.NO_BACKGROUND);
	}

	private void centerWindow()
	{
		Rectangle shellBounds;
	
		if (getParent().isVisible())
		{
			// Move the dialog to the center of the parent shell.
			shellBounds = getParent().getBounds();
		}
		else
		{
			// Move the dialog to the center of the display
			shellBounds = getParent().getDisplay().getBounds();
		}
		
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
	
	private void updateTextFont(Control label)
	{
		if (SWT.getPlatform().equals("cocoa"))
		{
			// Making the button
			Font defaultFont = label.getFont();
			FontData fontData = defaultFont.getFontData()[0];
			fontData.height -= 2;
		
			Font newFont = new Font(label.getDisplay(), fontData);
			label.setFont(newFont);
		}
	}
	
	
	/**
	 * Open the dialog.
	 * @return true if it has been opened, false if it had been opened when <code>open</code> was called
	 */
	public boolean open()
	{
		if (this.aboutBoxShell == null)
		{
			if (!SWT.getPlatform().equals("cocoa"))
			{
				createContents();
			}
			else
			{
				createContentsCocoa();
			}
			centerWindow();
	
			aboutBoxShell.layout();
			aboutBoxShell.open();
			Display display = getParent().getDisplay();
			while (!aboutBoxShell.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
			aboutBoxShell = null;
			return true;
		}
		else
		{
			return false;
		}
	}

	private void createContents()
	{
		
		aboutBoxShell = new Shell(getParent(), getStyle() | SWT.NO_BACKGROUND);
		aboutBoxShell.setSize(368, 150);
		aboutBoxShell.setText("About " + applicationName /*"SWT Application"*/);
		aboutBoxShell.setLayout(new FormLayout());
		

 		Label iconLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(SWTResourceManager.getImage(DefaultAboutBox.class, iconResourceName /*"/crossbase/icon.png"*/));
		FormData fd_iconLabel = new FormData();
		fd_iconLabel.top = new FormAttachment(0, 15);
		fd_iconLabel.left = new FormAttachment(0, 15);
		iconLabel.setLayoutData(fd_iconLabel);
		iconLabel.setSize(64, 64);
		
		Button okButton = new Button(aboutBoxShell, SWT.NO_BACKGROUND);
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
		
		Label descriptionLabel = new Label(aboutBoxShell, SWT.WRAP | SWT.NO_BACKGROUND);
		FormData fd_descriptionLabel = new FormData();
		fd_descriptionLabel.left = new FormAttachment(iconLabel, 15);
		fd_descriptionLabel.right = new FormAttachment(100, -15);
		descriptionLabel.setLayoutData(fd_descriptionLabel);
		descriptionLabel.setText(descriptionText /* "This app demonstrates Avian + SWT power"*/);
		updateTextFont(descriptionLabel);
		
		Label copyrightLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		fd_descriptionLabel.bottom = new FormAttachment(copyrightLabel, -6);
		copyrightLabel.setText(copyrightText /*"Copyright Ilya Mizus, 2013"*/);
		FormData fd_copyrightLabel = new FormData();
		fd_copyrightLabel.left = new FormAttachment(iconLabel, 15);
		fd_copyrightLabel.bottom = new FormAttachment(okButton, -6);
		fd_copyrightLabel.right = new FormAttachment(100, -15);
		copyrightLabel.setLayoutData(fd_copyrightLabel);
		updateTextFont(copyrightLabel);
		
		Label titleLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		fd_descriptionLabel.top = new FormAttachment(titleLabel, 6);
		updateTitleFont(titleLabel);
		titleLabel.setText(applicationName);
		FormData fd_titleLabel = new FormData();
		fd_titleLabel.left = new FormAttachment(iconLabel, 15);
		fd_titleLabel.right = new FormAttachment(100, -15);
		fd_titleLabel.top = new FormAttachment(0, 15);
		titleLabel.setLayoutData(fd_titleLabel);

		if (SWT.getPlatform().equals("gtk"))
		{
			aboutBoxShell.setSize(new Point((int)(windowSize.x * 1.2f), (int)(windowSize.y * 1.2f)));
		}
		else
		{
			aboutBoxShell.setSize(windowSize);
			
		}
	}
	
	/**
	 * Create contents of the dialog.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void createContentsCocoa()
	{
		
		aboutBoxShell = new Shell(getParent(), getStyle() | SWT.NO_BACKGROUND);
		aboutBoxShell.setSize(368, 280);
		aboutBoxShell.setText("About " + applicationName /*"SWT Application"*/);
		aboutBoxShell.setLayout(new FormLayout());
		

 		Label iconLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(SWTResourceManager.getImage(DefaultAboutBox.class, iconResourceName /*"/crossbase/icon.png"*/));
		FormData fd_iconLabel = new FormData();
		fd_iconLabel.left = new FormAttachment(0, 10);
		fd_iconLabel.right = new FormAttachment(100, -10);
		fd_iconLabel.top = new FormAttachment(0, 10);
		//fd_iconLabel.right = new FormAttachment(0, 15);
		iconLabel.setLayoutData(fd_iconLabel);
		iconLabel.setSize(64, 64);
		
		Label titleLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		updateTitleFont(titleLabel);
		titleLabel.setAlignment(SWT.CENTER);
		titleLabel.setText(applicationName);
		FormData fd_titleLabel = new FormData();
		fd_titleLabel.left = new FormAttachment(0, 10);
		fd_titleLabel.right = new FormAttachment(100, -10);
		fd_titleLabel.top = new FormAttachment(iconLabel, 15);
		titleLabel.setLayoutData(fd_titleLabel);

		Label descriptionLabel = new Label(aboutBoxShell, SWT.WRAP | SWT.NO_BACKGROUND);
		FormData fd_descriptionLabel = new FormData();
		fd_descriptionLabel.left = new FormAttachment(0, 10);
		fd_descriptionLabel.right = new FormAttachment(100, -10);
		fd_descriptionLabel.top = new FormAttachment(titleLabel, 15);
		descriptionLabel.setLayoutData(fd_descriptionLabel);
		descriptionLabel.setAlignment(SWT.CENTER);
		descriptionLabel.setText(descriptionText /* "This app demonstrates Avian + SWT power"*/);
		updateTextFont(descriptionLabel);
		
		Label copyrightLabel = new Label(aboutBoxShell, SWT.NO_BACKGROUND);
		copyrightLabel.setText(copyrightText /*"Copyright Ilya Mizus, 2013"*/);
		FormData fd_copyrightLabel = new FormData();
		fd_copyrightLabel.left = new FormAttachment(0, 10);
		fd_copyrightLabel.right = new FormAttachment(100, -10);
		fd_copyrightLabel.top = new FormAttachment(descriptionLabel, 15);
		copyrightLabel.setLayoutData(fd_copyrightLabel);
		copyrightLabel.setAlignment(SWT.CENTER);

		updateTextFont(copyrightLabel);
		aboutBoxShell.layout();
		
		aboutBoxShell.setSize(windowSize.x, iconLabel.getSize().y + 30 + titleLabel.getSize().y + 30 + descriptionLabel.getSize().y + 30 + copyrightLabel.getSize().y);
		
		aboutBoxShell.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeiconified(ShellEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeactivated(ShellEvent arg0) {
				aboutBoxShell.close();
			}
			
			@Override
			public void shellClosed(ShellEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellActivated(ShellEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	public boolean isDisposed()
	{
		return aboutBoxShell == null || aboutBoxShell.isDisposed();
	}

	public String getApplicationName()
	{
		return applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public String getDescriptionText()
	{
		return descriptionText;
	}

	public void setDescriptionText(String descriptionText)
	{
		this.descriptionText = descriptionText;
	}

	public String getCopyrightText()
	{
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText)
	{
		this.copyrightText = copyrightText;
	}

	public String getIconResourceName()
	{
		return iconResourceName;
	}

	public void setIconResourceName(String iconResourceName)
	{
		this.iconResourceName = iconResourceName;
	}

	public Point getWindowSize()
	{
		return windowSize;
	}

	public void setWindowSize(Point windowSize)
	{
		this.windowSize = windowSize;
	}
}
