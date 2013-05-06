package tinyviewer;

import java.io.IOException;

import crossbase.abstracts.DocumentLoader;

public class ImageDocumentLoader implements DocumentLoader<ImageDocument>
{
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

}
