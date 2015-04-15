package zetes.wings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class ZetesDialog extends Dialog {
	private static Shell emptyShell;

	public ZetesDialog(final Shell parent, int flags) {
		super(new Object() {
					
					public Shell getShell() {
						if (SWT.getPlatform().equals("cocoa") || parent == null) {
							if (emptyShell == null) emptyShell = new Shell(Display.getCurrent());
							return emptyShell;
						} else {
							return parent.getShell();
						}
					}
		
				}.getShell(), flags);
	}
	
	protected void centerWindow(Shell shell)
	{
		Rectangle shellBounds;
	
		if (SWT.getPlatform().equals("cocoa"))
		{
			// Move the dialog to he center horizontally and 1/4 vertically
			shellBounds = getParent().getDisplay().getBounds();
			shellBounds.y -= shellBounds.height / 6;
		}
		else if (getParent().isVisible())
		{
			// Move the dialog to the center of the parent shell.
			shellBounds = getParent().getBounds();
		}
		else
		{
			// Move the dialog to the center of the display
			shellBounds = getParent().getDisplay().getBounds();
		}
		
        Point dialogSize = shell.getSize();

        shell.setLocation(shellBounds.x + (shellBounds.width - dialogSize.x) / 2,
        					shellBounds.y + (shellBounds.height - dialogSize.y) / 2);	
   	}
}
