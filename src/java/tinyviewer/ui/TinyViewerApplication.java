package tinyviewer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import crossbase.Application;
import crossbase.ui.AboutBox;
import crossbase.ui.DocumentWindow;
import crossbase.ui.DocumentWindowsManager;
import crossbase.ui.MenuConstructor;

public class TinyViewerApplication extends Application
{
	private AboutBox aboutBox = null;
	private SelectionAdapter fileOpenSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Shell dummyShell = new Shell(Display.getDefault());
			FileDialog fileDialog = new FileDialog(dummyShell, SWT.OPEN);
			fileDialog.setText("Open image");
			fileDialog.setFilterNames(new String[] { "Image (*.png; *.bmp; *.jpg; *.jpeg)", "All files" });
			fileDialog.setFilterExtensions(new String[] { "*.png; *.bmp; *.jpg; *.jpeg", "*.*" });
			String fileName = fileDialog.open();
			if (fileName != null)
			{
				getDocumentWindowsManager().openFile(fileName);
			}
			dummyShell.dispose();
		}
	};
	
	@Override
	protected void showAbout()
	{
		if (aboutBox == null || aboutBox.isDisposed())
		{
			Shell dummyShell = new Shell(Display.getDefault());
			aboutBox = new AboutBox(dummyShell);
			aboutBox.open();
			dummyShell.dispose();
		}
	}
	
	@Override
	public void run(String[] arguments)
	{
		TinyViewerMenuConstructor menuConstructor = new TinyViewerMenuConstructor();
		menuConstructor.setOpenSelectionAdapter(fileOpenSelectionAdapter);
		setMenuConstructor(menuConstructor);

		setDocumentWindowsManager(new DocumentWindowsManager<ViewWindow>(new ViewWindowFactory(menuConstructor)));
		
		super.run(arguments);
	}
}
