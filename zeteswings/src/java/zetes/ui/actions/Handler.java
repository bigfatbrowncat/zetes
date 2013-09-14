package zetes.ui.actions;

import zetes.ui.actions.Action;
import zetes.abstracts.ViewWindow;
import zetes.ui.HotKey;

public abstract class Handler<TVW extends ViewWindow<?>> {

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}
	
	public boolean isChecked() {
		return false;
	}
	
	/**
	 * This function should return title for dynamically titled items.
	 * If it returns <code>null</code>, the default {@link Action} title is used.
	 * @return Dynamically generated title
	 */
	public String getTitle() {
		return null;
	}
	
	/**
	 * This function should return a {@link HotKey} object for dynamic
	 * hot key. If it's not defined, the default hotkey from {@link Action}
	 * would be used.
	 * @return Hot key
	 */
	public HotKey getHotKey() {
		return null;
	}
	
	public abstract void execute(TVW window);
}