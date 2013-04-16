package crossbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Application
{

	public static void main(String... args)
	{
		Display display = new Display ();
		Shell shell = new Shell(display);
	 
		Text helloWorldTest = new Text(shell, SWT.NONE);
		helloWorldTest.setText("Hello World SWT");
		helloWorldTest.pack();
	 
		shell.pack();
		shell.open ();
		
		while (!shell.isDisposed ())
		{
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		
		
	}
}
