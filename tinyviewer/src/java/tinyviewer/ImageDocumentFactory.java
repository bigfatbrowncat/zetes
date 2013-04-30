package tinyviewer;

import java.io.IOException;

import crossbase.ui.abstracts.DocumentFactory;

public class ImageDocumentFactory implements DocumentFactory<ImageDocument>
{
	@Override
	public ImageDocument createFromFile(String fileName)
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

}
