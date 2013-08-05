package crossbase.abstracts;

public interface AboutBox {
	boolean isDisposed();
	
	/**
	 * Open the dialog.
	 * @return true if it has been opened, false if it had been opened when <code>open</code> was called
	 */
	public boolean open();
}
