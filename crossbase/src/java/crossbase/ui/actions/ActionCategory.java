package crossbase.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;
import crossbase.ui.actions.Action.Handler;

public class ActionCategory<TD extends Document, TVW extends ViewWindow<TD>> implements ActionHierarchyMember<TD, TVW> {

	private String title;
	private LinkedList<ActionHierarchyMember<TD, TVW>> items = new LinkedList<>();
	
	public ActionCategory(String title) {
		if (title == null) throw new IllegalArgumentException("Action title shouldn't be null");
		this.title = title;
	}
	
	public void addItem(ActionHierarchyMember<TD, TVW> item) {
		items.addLast(item);
	}
	
	public ActionHierarchyMember<TD, TVW> getItem(int i) {
		return items.get(i);
	}
	
	public int getItemsCount() {
		return items.size();
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (title == null) throw new IllegalArgumentException("Action title shouldn't be null");
		this.title = title;
	}

	public Action<TD, TVW> findActionByIdRecursively(int id) {
		List<ActionCategory<TD, TVW>> inners = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) instanceof ActionCategory) {
				inners.add((ActionCategory<TD, TVW>)items.get(i));
			} else if (items.get(i) instanceof Action) {
				Action<TD, TVW> cur = (Action<TD, TVW>) items.get(i); 
				if (cur.getId() == id) {
					return cur;
				}
			}
		}
		
		for (int i = 0; i < inners.size(); i++) {
			 Action<TD, TVW> res = inners.get(i).findActionByIdRecursively(id);
			 if (res != null) return res;
		}
		
		return null;
	}
	
}
