package zetes.wings.abstracts;

public interface AboutBox {
	/**
	 * @return {@code true} if the dialog window was previously opened and closed (disposed) after that 
	 */
	boolean isDisposed();
	
	/**
	 * Opens the dialog.
	 * @return {@code true} if it has been opened, {@code false} if it had been already opened at the moment when <code>open</code> was called
	 */
	public boolean open();
}
