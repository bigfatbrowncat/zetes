package flyer;

import java.io.IOException;

import zetes.wings.abstracts.Document;

public class FlyerDocument implements Document
{
	public FlyerDocument(String fileName) throws IOException
	{
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public String getTitle() {
		return null;
	}
}
