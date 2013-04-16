package crossbase.ui;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class AboutBox extends Dialog
{

	protected Object result;
	protected Shell shlAboutSwtApplication;

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
        Point dialogSize = shlAboutSwtApplication.getSize();

        shlAboutSwtApplication.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
        					shellBounds.y + (shellBounds.height - dialogSize.y) / 2);	
   	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		if (this.shlAboutSwtApplication == null)
		{
			createContents();
			centerWindow();
	
			shlAboutSwtApplication.open();
			shlAboutSwtApplication.layout();
			Display display = getParent().getDisplay();
			while (!shlAboutSwtApplication.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
			shlAboutSwtApplication = null;
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
		shlAboutSwtApplication = new Shell(getParent(), getStyle());
		shlAboutSwtApplication.setSize(360, 147);
		shlAboutSwtApplication.setText("About SWT Application");
		shlAboutSwtApplication.setLayout(new FormLayout());
		

        
        
		Label iconLabel = new Label(shlAboutSwtApplication, SWT.NONE);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(SWTResourceManager.getImage(AboutBox.class, "/crossbase/icon.png"));
		FormData fd_iconLabel = new FormData();
		fd_iconLabel.top = new FormAttachment(0, 10);
		fd_iconLabel.left = new FormAttachment(0, 10);
		fd_iconLabel.bottom = new FormAttachment(0, 74);
		fd_iconLabel.right = new FormAttachment(0, 74);
		iconLabel.setLayoutData(fd_iconLabel);
		
		Button okButton = new Button(shlAboutSwtApplication, SWT.NONE);
		okButton.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.NORMAL));
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shlAboutSwtApplication.close();
			}
		});
		FormData fd_okButton = new FormData();
		fd_okButton.top = new FormAttachment(100, -38);
		fd_okButton.left = new FormAttachment(100, -107);
		fd_okButton.bottom = new FormAttachment(100, -10);
		fd_okButton.right = new FormAttachment(100, -10);
		okButton.setLayoutData(fd_okButton);
		okButton.setText("OK");
		shlAboutSwtApplication.setDefaultButton(okButton);
		
		Label descriptionLabel = new Label(shlAboutSwtApplication, SWT.NONE);
		FormData fd_descriptionLabel = new FormData();
		fd_descriptionLabel.left = new FormAttachment(iconLabel, 6);
		fd_descriptionLabel.right = new FormAttachment(100, -10);
		descriptionLabel.setLayoutData(fd_descriptionLabel);
		descriptionLabel.setText("This app demonstrates Avian + SWT power");
		
		Label copyrightLabel = new Label(shlAboutSwtApplication, SWT.NONE);
		fd_descriptionLabel.bottom = new FormAttachment(copyrightLabel, -2);
		copyrightLabel.setText("Copyright Ilya Mizus, 2013");
		FormData fd_copyrightLabel = new FormData();
		fd_copyrightLabel.right = new FormAttachment(100, -10);
		fd_copyrightLabel.left = new FormAttachment(iconLabel, 6);
		fd_copyrightLabel.bottom = new FormAttachment(iconLabel, 0, SWT.BOTTOM);
		fd_copyrightLabel.top = new FormAttachment(0, 59);
		copyrightLabel.setLayoutData(fd_copyrightLabel);
		
		Label titleLabel = new Label(shlAboutSwtApplication, SWT.NONE);
		fd_descriptionLabel.top = new FormAttachment(titleLabel, 3);
		titleLabel.setFont(SWTResourceManager.getFont("Lucida Grande", 12, SWT.BOLD));
		titleLabel.setText("CrossBase SWT Application");
		FormData fd_titleLabel = new FormData();
		fd_titleLabel.right = new FormAttachment(100, -10);
		fd_titleLabel.left = new FormAttachment(iconLabel, 6);
		fd_titleLabel.top = new FormAttachment(iconLabel, 0, SWT.TOP);
		titleLabel.setLayoutData(fd_titleLabel);

	}

}
