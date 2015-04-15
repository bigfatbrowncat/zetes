package zetes.wings;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class UnhandledExceptionBox extends ZetesDialog {

	private String iconResourceName;
	private static Shell emptyShell;
	protected Object result;
	protected Shell shlZetesFalls;
	private Text text;
	
	private Throwable exception;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UnhandledExceptionBox(final Shell parent) {
		super(new Object() {
			
			public Shell getShell() {
				if (SWT.getPlatform().equals("cocoa")) {
					if (emptyShell == null) emptyShell = new Shell(Display.getCurrent());
					return emptyShell;
				} else {
					return parent.getShell();
				}
			}

		}.getShell(), SWT.DIALOG_TRIM | SWT.CENTER | SWT.DOUBLE_BUFFERED);

		setText("SWT Dialog");
	}
	
	public void setException(Throwable e) {
		this.exception = e;
	}
	public void setIconResourceName(String resourceName) {
		this.iconResourceName = resourceName;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlZetesFalls.open();
		shlZetesFalls.layout();
		centerWindow(shlZetesFalls);
		new Label(shlZetesFalls, SWT.NONE);
		new Label(shlZetesFalls, SWT.NONE);
		new Label(shlZetesFalls, SWT.NONE);
		
		Button button = new Button(shlZetesFalls, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shlZetesFalls.close();
			}
		});
		GridData gd_button = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 78;
		button.setLayoutData(gd_button);
		button.setText("Close");
		Display display = getParent().getDisplay();
		while (!shlZetesFalls.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlZetesFalls = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shlZetesFalls.setSize(506, 250);
		shlZetesFalls.setText("Zetes falls");
		GridLayout gl_shlZetesFalls = new GridLayout(4, false);
		gl_shlZetesFalls.marginLeft = 5;
		gl_shlZetesFalls.marginRight = 5;
		gl_shlZetesFalls.marginTop = 5;
		gl_shlZetesFalls.horizontalSpacing = 1;
		shlZetesFalls.setLayout(gl_shlZetesFalls);
		
		Label iconLabel = new Label(shlZetesFalls, SWT.NONE);
		iconLabel.setImage(SWTResourceManager.getImage(DefaultAboutBox.class, iconResourceName /*"/crossbase/icon.png"*/));
		
		Label lblAnUnhandledException = new Label(shlZetesFalls, SWT.WRAP);
		GridData gd_lblAnUnhandledException = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		gd_lblAnUnhandledException.widthHint = 412;
		lblAnUnhandledException.setLayoutData(gd_lblAnUnhandledException);
		lblAnUnhandledException.setText("An unhandled exception occured in the application. It means that, unfortunately, the application has crashed. If you know the developers, let them know.");
		
		Label lblStackTrace = new Label(shlZetesFalls, SWT.NONE);
		lblStackTrace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1));
		lblStackTrace.setText("Stack trace:");
		
		text = new Text(shlZetesFalls, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Courier New", 11, SWT.NORMAL));
		text.setEditable(false);
		text.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_text.widthHint = 366;
		text.setLayoutData(gd_text);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		text.setText(sw.toString());

	}

}
