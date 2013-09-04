package tinyviewer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import zetes.abstracts.Document;


public class ImageDocument implements Document
{
	private Image image;
	private String fileName;

	private static Image loadImage(InputStream stream) throws IOException
	{
		try
		{
			Display display = Display.getDefault();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		}
		finally
		{
			stream.close();
		}
	}
	
	private static Image loadImage(String fileName) throws IOException
	{
		return loadImage(new FileInputStream(fileName));
	}

	public ImageDocument(String fileName) throws IOException
	{
		this.fileName = fileName;
		this.image = loadImage(fileName);
	}
	
	public String getTitle()
	{
		return fileName;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public void dispose()
	{
		if (image != null && !image.isDisposed()) image.dispose();
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		dispose();
		super.finalize();
	}
}
