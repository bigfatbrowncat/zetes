package crossbase.abstracts;

public interface Document
{
	/**
	 * @return The document title.
	 */
	String getTitle();
	
	/**
	 * This function is called when the document has been
	 * closed. It should free all the system resources linked
	 * to the document
	 */
	void dispose();
}
