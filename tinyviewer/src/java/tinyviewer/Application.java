package tinyviewer;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
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
				ArrayList<ImageDocument> documents = new ArrayList<ImageDocument>();
				
				// Creating documents for files
				for (int i = 0; i < names.length; i++)
				{
					String fileName = fileDialog.getFilterPath() + "/" + names[i];
					try
					{
						documents.add(new ImageDocument(fileName));
					}
					catch (IOException e)
					{
						// TODO Show a message box here
						e.printStackTrace();
					}
				}
				
				getDocumentWindowsManager().openViewForDocuments(documents.toArray(new ImageDocument[] {}));
			}
			dummyShell.dispose();
		}
	};
		
	private DropTargetAdapter viewWindowDropTargetAdapter = new DropTargetAdapter()
	{
		public void drop(DropTargetEvent event) {
			String fileList[] = null;
			FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				fileList = (String[]) event.data;
				for (int i = 0; i < fileList.length; i++)
				{
					ImageDocument document;
					try
					{
						document = new ImageDocument(fileList[i]);
						getDocumentWindowsManager().openViewForDocument(document);
					}
					catch (IOException e)
					{
						// TODO Show a message box here
						e.printStackTrace();
					}
				}
			}
		}
	};
		
		
	public Application()
	{
		// About box factory
		setAboutBoxFactory(new TinyViewerAboutBoxFactory());

		// Menu constructor
		TinyViewerMenuConstructor menuConstructor = new TinyViewerMenuConstructor();
		menuConstructor.setFileOpenSelectionAdapter(fileOpenSelectionAdapter);
		setMenuConstructor(menuConstructor);

		// View window factory construction
		ImageViewWindowFactory imageViewWindowFactory = new ImageViewWindowFactory();
		imageViewWindowFactory.setViewWindowDropTargetAdapter(viewWindowDropTargetAdapter);
		
		// Image view windows manager
		ViewWindowsManager<ImageViewWindow> imageViewWindowsManager;
		imageViewWindowsManager = new ViewWindowsManager<ImageViewWindow>();
		imageViewWindowsManager.setViewWindowFactory(imageViewWindowFactory);
		imageViewWindowsManager.setMenuConstructor(menuConstructor);
		setDocumentWindowsManager(imageViewWindowsManager);
	}
	
	public static void main(String... args)
	{
		new Application().run(args);
	}
}
