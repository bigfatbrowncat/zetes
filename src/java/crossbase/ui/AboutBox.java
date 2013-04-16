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
	protected Shell shell;

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

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open()
	{
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents()
	{
		shell = new Shell(getParent(), getStyle());
		shell.setSize(360, 148);
		shell.setText("About CrossBase SWT Application...");
		shell.setLayout(new FormLayout());
		
        // Move the dialog to the center of the top level shell.
        Rectangle shellBounds = getParent().getBounds();
        Point dialogSize = shell.getSize();

        shell.setLocation(
          shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
          shellBounds.y + (shellBounds.height - dialogSize.y) / 2);	
        
        
		Label iconLabel = new Label(shell, SWT.NONE);
		iconLabel.setAlignment(SWT.CENTER);
		iconLabel.setImage(SWTResourceManager.getImage(AboutBox.class, "/crossbase/icon.png"));
		FormData fd_iconLabel = new FormData();
		fd_iconLabel.top = new FormAttachment(0, 10);
		fd_iconLabel.left = new FormAttachment(0, 10);
		fd_iconLabel.bottom = new FormAttachment(0, 74);
		fd_iconLabel.right = new FormAttachment(0, 74);
		iconLabel.setLayoutData(fd_iconLabel);
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		FormData fd_okButton = new FormData();
		fd_okButton.left = new FormAttachment(100, -95);
		fd_okButton.bottom = new FormAttachment(100, -10);
		fd_okButton.right = new FormAttachment(100, -10);
		okButton.setLayoutData(fd_okButton);
		okButton.setText("OK");
		
		Label descriptionLabel = new Label(shell, SWT.NONE);
		FormData fd_descriptionLabel = new FormData();
		fd_descriptionLabel.right = new FormAttachment(okButton, 0, SWT.RIGHT);
		fd_descriptionLabel.left = new FormAttachment(iconLabel, 6);
		descriptionLabel.setLayoutData(fd_descriptionLabel);
		descriptionLabel.setText("This app demonstrates Avian + SWT power");
		
		Label copyrightLabel = new Label(shell, SWT.NONE);
		fd_descriptionLabel.bottom = new FormAttachment(copyrightLabel, -13);
		copyrightLabel.setText("Copyright Ilya Mizus, 2013");
		FormData fd_copyrightLabel = new FormData();
		fd_copyrightLabel.bottom = new FormAttachment(iconLabel, 0, SWT.BOTTOM);
		fd_copyrightLabel.right = new FormAttachment(okButton, 0, SWT.RIGHT);
		fd_copyrightLabel.left = new FormAttachment(iconLabel, 6);
		fd_copyrightLabel.top = new FormAttachment(0, 59);
		copyrightLabel.setLayoutData(fd_copyrightLabel);
		
		Label titleLabel = new Label(shell, SWT.NONE);
		fd_descriptionLabel.top = new FormAttachment(titleLabel, 6);
		titleLabel.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		titleLabel.setText("CrossBase SWT Application");
		FormData fd_titleLabel = new FormData();
		fd_titleLabel.right = new FormAttachment(okButton, 0, SWT.RIGHT);
		fd_titleLabel.top = new FormAttachment(iconLabel, 0, SWT.TOP);
		fd_titleLabel.left = new FormAttachment(iconLabel, 6);
		titleLabel.setLayoutData(fd_titleLabel);

	}

}
