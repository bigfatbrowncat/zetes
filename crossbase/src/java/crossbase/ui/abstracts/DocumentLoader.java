package crossbase.ui.abstracts;

public interface DocumentLoader<T extends Document>
{
	/**
	 * This function creates a document object from a file.
	 * @param fileName The name of the file to create a document object from
	 * @return The new object. 
	 * It can be null if the object can't be created from that particular file or 
	 * if it can't be created from a file at all. 
	 */
	T loadFromFile(String fileName);
}
