package crossbase.ui.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;

public class ActionCategory<TVW extends ViewWindow<?>> extends ActionHierarchyMember<TVW> {

	private LinkedList<ActionHierarchyMember<TVW>> items = new LinkedList<>();
	
	public ActionCategory(int id) {
		super(id);
	}

	public ActionCategory(int id, String title) {
		super(id, title);
	}
	
	public void addLastItem(ActionHierarchyMember<TVW> item) {
		items.addLast(item);
	}
	
	public void addFirstItem(ActionHierarchyMember<TVW> item) {
		items.addFirst(item);
	}
	
	public void addItem(int i, ActionHierarchyMember<TVW> item) {
		items.add(i, item);
	}
	
	public ActionHierarchyMember<TVW> getItem(int i) {
		return items.get(i);
	}
	
	public int getItemsCount() {
		return items.size();
	}
	
	public ActionHierarchyMember<TVW> findActionByIdRecursively(int id) {
		List<ActionCategory<TVW>> inners = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getId() == id) {
				return items.get(i);
			}
			
			if (items.get(i) instanceof ActionCategory) {
				inners.add((ActionCategory<TVW>)items.get(i));
			}
		}
		
		for (int i = 0; i < inners.size(); i++) {
			ActionHierarchyMember<TVW> res = inners.get(i).findActionByIdRecursively(id);
			if (res != null) return res;
		}
		
		return null;
	}
	
}
