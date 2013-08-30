package crossbase.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;
import crossbase.ui.HotKey;

public class Action<TD extends Document, TVW extends ViewWindow<TD>> extends ActionHierarchyMember<TD, TVW> {
	
	public static class Handler {
		private SelectionAdapter listener;
		private boolean enabled = true;
		private boolean visible = true;
		
		public Handler() {
			enabled = true;
			visible = true;
		}
		
		public SelectionAdapter getListener() {
			return listener;
		}
		public void setListener(SelectionAdapter listener) {
			System.out.println("setting listener to " + listener.toString());
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
	
	private HotKey hotKey;

	private HashMap<TVW, Handler> handlers = new HashMap<>();
	
	public Action(int id, String title) {
		super(id, title);
	}
	
	public HotKey getHotKey() {
		return hotKey;
	}
	public void setHotKey(HotKey hotKey) {
		this.hotKey = hotKey;
	}
	
	
	/**
	 * <p>You can set an action handler for every window. If you want to set
	 * the default handler (which isn't connected to any window), use the key {@code null}.</p>
	 * 
	 * <p>If default handler is specified for the {@link Action}, this action is shown for every
	 * window (and in the case when no windows are present on OS X).</p>
	 * <p>If a {@link Handler} is specified for the active {@link ViewWindow}, it overrides the
	 * default one.</p>
	 * <p>If no default {@link Handler} specified, the action should be shown only for
	 * windows that provide a specific handler for the {@link Action}</p>
	 * 
	 * @return A modifiable {@link Map} that connects view windows and handlers
	 */
	public Map<TVW, Handler> getHandlers() {
		return handlers;
	}

}
