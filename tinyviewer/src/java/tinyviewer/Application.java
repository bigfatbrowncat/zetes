package tinyviewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import crossbase.ApplicationBase;
import crossbase.ui.ViewWindowsManager;

public class Application extends ApplicationBase
{
	public static final String APP_NAME = "Tiny Viewer";

	private SelectionAdapter fileOpenSelectionAdapter = new SelectionAdapter()
	{
		@Override
		public void widgetSelected(SelectionEvent arg0)
		{
			Shell dummyShell = new Shell(Display.getDefault());
			FileDialog fileDialog = new FileDialog(dummyShell, SWT.OPEN | SWT.MULTI);
			fileDialog.setText("Open image");
			fileDialog.setFilterNames(new String[] { "Image (*.png; *.bmp; *.jpg; *.jpeg)", "All files" });
			fileDialog.setFilterExtensions(new String[] { "*.png; *.bmp; *.jpg; *.jpeg", "*.*" });
			String firstFile = fileDialog.open();
			if (firstFile != null)
			{
				String[] names = fileDialog.getFileNames();
				String[] fullNames = new String[names.length];
				
				for (int i = 0; i < names.length; i++)
				{
					fullNames[i] = fileDialog.getFilterPath() + "/" + names[i];
				}
				
				getDocumentWindowsManager().openFiles(fullNames);
			}
			dummyShell.dispose();
		}
	};
	
	@Override
	public void run(String[] arguments)
	{
		TinyViewerAboutBoxFactory tinyViewAboutBoxFactory = new TinyViewerAboutBoxFactory();
		setAboutBoxFactory(tinyViewAboutBoxFactory);
		
		TinyViewerMenuConstructor menuConstructor = new TinyViewerMenuConstructor();
		menuConstructor.setOpenSelectionAdapter(fileOpenSelectionAdapter);

		setMenuConstructor(menuConstructor);

		ViewWindowsManager<ImageViewWindow> imageViewWindowsManager = new ViewWindowsManager<ImageViewWindow>();
		ImageViewWindowFactory imageViewWindowFactory = new ImageViewWindowFactory();
		
		imageViewWindowsManager.setViewWindowFactory(imageViewWindowFactory);
		imageViewWindowFactory.setImageViewWindowsManager(imageViewWindowsManager);
		imageViewWindowFactory.setMenuConstructor(menuConstructor);
		
		setDocumentWindowsManager(imageViewWindowsManager);
		
		menuConstructor.updateMenus();
		
		super.run(arguments);
	}
	
	public static void main(String... args)
	{
		new Application().run(args);
	}
}
