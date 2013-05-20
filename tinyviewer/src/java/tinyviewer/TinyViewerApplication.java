package tinyviewer;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import crossbase.ApplicationBase;
import crossbase.ui.DefaultAboutBox;

public class TinyViewerApplication extends ApplicationBase<DefaultAboutBox, ImageDocument, ImageViewWindow, TinyViewerMenuConstructor>
{
	@Override
	public String getTitle()
	{
		return "Tiny Viewer";
	}

	@Override
	public DefaultAboutBox createAboutBox(Shell parent)
	{
		DefaultAboutBox res = new DefaultAboutBox(parent);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/crossbase/icon.png");
		res.setDescriptionText("A simple image file viewer.\nThis application shows the power of Avian + SWT");
		res.setCopyrightText("Copyright \u00a9 2013, Ilya Mizus");
		res.setWindowSize(new Point(370, 180));
		return res;
	}
	
	@Override
	public ImageDocument loadFromFile(String fileName)
	{
		try
		{
			return new ImageDocument(fileName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
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
				
				getViewWindowsManager().openViewForDocuments(documents.toArray(new ImageDocument[] {}));
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
						getViewWindowsManager().openViewForDocument(document);
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
		
		
	public TinyViewerApplication()
	{

	}
	
	public static void main(String... args)
	{
		new TinyViewerApplication().run(args, null);
	}

	@Override
	public ImageViewWindowsManager createViewWindowsManager()
	{
		return new ImageViewWindowsManager(getTitle(), viewWindowDropTargetAdapter);
	}

	@Override
	public TinyViewerMenuConstructor createMenuConstructor()
	{
		TinyViewerMenuConstructor menuConstructor = new TinyViewerMenuConstructor();
		menuConstructor.setFileOpenSelectionAdapter(fileOpenSelectionAdapter);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}
}
