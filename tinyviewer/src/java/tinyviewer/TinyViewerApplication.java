package tinyviewer;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import zetes.ApplicationBase;
import zetes.ui.DefaultAboutBox;
import zetes.ui.actions.Handler;


public class TinyViewerApplication extends ApplicationBase<DefaultAboutBox, ImageDocument, ImageViewWindow, TinyViewerMenuConstructor, ImageViewWindowsManager>
{
	//static {
	//	System.loadLibrary("tinyview.debug");
	//}
	
	@Override
	public String getTitle()
	{
		return "Tiny Viewer";
	}

	@Override
	public DefaultAboutBox createAboutBox(ImageViewWindow window)
	{
		DefaultAboutBox res = new DefaultAboutBox(window);
		res.setApplicationName(getTitle());
		res.setIconResourceName("/tinyviewer/wingphotos64.png");
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
	
	private Handler<ImageViewWindow> fileOpenHandler = new Handler<ImageViewWindow>() {
		
		@Override
		public void execute(ImageViewWindow window) {
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
				
				getViewWindowsManager().openWindowsForDocuments(documents.toArray(new ImageDocument[] {}));
			}
			dummyShell.dispose();		
		}
	};
	
	public TinyViewerApplication()
	{
	}
	
	@Override
	public ImageViewWindowsManager createViewWindowsManager()
	{
		return new ImageViewWindowsManager();
	}

	@Override
	public TinyViewerMenuConstructor createMenuConstructor(ImageViewWindowsManager viewWindowsManager)
	{
		TinyViewerMenuConstructor menuConstructor = new TinyViewerMenuConstructor(viewWindowsManager);
		menuConstructor.setFileOpenHandler(fileOpenHandler);
		return menuConstructor;
	}

	@Override
	public boolean needsAtLeastOneView()
	{
		return false;
	}

	public static void main(String... args)
	{
		new TinyViewerApplication().run(args);
	}
}
