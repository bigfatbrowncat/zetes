package zetes.wings;

import zetes.wings.abstracts.Document;

public class NullDocument implements Document {

	public NullDocument() {
		throw new RuntimeException("Null document isn't intended to be instantiated. It should be used only for applications that don't support documents");
	}
	
	@Override
	public String getTitle()
	{
		return null;
	}

	@Override
	public void dispose()
	{
	}
	
}
