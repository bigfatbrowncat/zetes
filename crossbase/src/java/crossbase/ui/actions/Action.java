package crossbase.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;
import crossbase.ui.HotKey;

public class Action<TD extends Document, TVW extends ViewWindow<TD>> implements ActionHierarchyMember<TD, TVW> {
	
	public static class Handler {
		private SelectionAdapter listener;
		private boolean enabled = true;
		private boolean visible = true;
		
		public SelectionAdapter getListener() {
			return listener;
		}
		public void setListener(SelectionAdapter listener) {
			this.listener = listener;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public boolean isVisible() {
			return visible;
		}
		public void setVisible(boolean visible) {
			this.visible = visible;
		}
	}
	
	private int id;
	private String title;
	private HotKey hotKey;
	private boolean globallySupported;

	private HashMap<TVW, Handler> handlers = new HashMap<>();
	
	public Action(int id, String title) {
		if (title == null) throw new IllegalArgumentException("Action title shouldn't be null");
		this.id = id;
		this.title = title;
	}
	
	/**
	 * <p>If this property is set to true, this {@link Action} will
	 * be shown for every window. In OS X it will be placed
	 * to the global menu too.</p>
	 * <p>For windows that don't provide a special handler for it,
	 * the default one will be used</p>
	 * 
	 * <p>If this property is false, this item will be shown only for windows that provide
	 * a specific handler for it</p>
	 * 
	 * @return globallySupported value
	 */
	public boolean isGloballySupported() {
		return globallySupported;
	}

	/**
	 * <p>If this property is set to true, this {@link Action} will
	 * be shown for every window. In OS X it will be placed
	 * to the global menu too.</p>
	 * <p>For windows that don't provide a special handler for it,
	 * the default one will be used.</p>
	 * 
	 * <p>If this property is false, this item will be shown only for windows that provide
	 * a specific handler for it</p>
	 * 
	 * @return globallySupported value
	 */
	public void setGloballySupported(boolean value) {
		globallySupported = value;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if (title == null) throw new IllegalArgumentException("Action title shouldn't be null");
		this.title = title;
	}
	
	public HotKey getHotKey() {
		return hotKey;
	}
	public void setHotKey(HotKey hotKey) {
		this.hotKey = hotKey;
	}
	
	/**
	 * You can set an action handler for every window. If you want to set
	 * the default handler (which isn't connected to any window), use the key {@code null}.
	 * @return Map that connects view windows and handlers
	 */
	public Map<TVW, Handler> getHandlers() {
		return handlers;
	}

}
