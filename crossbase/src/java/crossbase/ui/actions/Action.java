package crossbase.ui.actions;

import java.util.HashMap;

import org.eclipse.swt.events.SelectionAdapter;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;
import crossbase.ui.HotKey;

public class Action<TD extends Document, TVW extends ViewWindow<TD>> implements ActionHierarchyMember<TD, TVW> {
	
	public static class Handler {
		private SelectionAdapter listener;
		private boolean enabled;
		
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
		private boolean visible;
	}
	
	private int id;
	private String title;
	private HotKey hotKey;

	private HashMap<TVW, Handler> handlers = new HashMap<>();
	
	public Action(int id, String title) {
		if (title == null) throw new IllegalArgumentException("Action title shouldn't be null");
		this.id = id;
		this.title = title;
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
	public HashMap<TVW, Handler> getHandlers() {
		return handlers;
	}

}
