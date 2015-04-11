package zetes.wings;

import zetes.wings.abstracts.Document;
import zetes.wings.abstracts.DocumentListener;

public class NullDocument implements Document {

	public NullDocument() {
		throw new RuntimeException("NullDocument class isn't intended to be instantiated. It should be used only for applications that don't support documents");
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
	
	@Override
	public void addListener(DocumentListener listener) {
		// Do nothing - it's a NULL document
	}

	@Override
	public void removeListener(DocumentListener listener) {
		// Do nothing - it's a NULL document
	}
}
