package crossbase.ui.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import crossbase.abstracts.ViewWindow;

public class ActionList<TVW extends ViewWindow<?>> extends Action<TVW> {

	private LinkedList<ActionHierarchyMember<TVW>> items = new LinkedList<>();
	private boolean isSubMenu = true;
	
	public ActionList(int id) {
		super(id);
	}

	public ActionList(int id, String title) {
		super(id, title);
	}

	public ActionList(int id, String title, boolean isSubMenu) {
		super(id, title);
		this.setSubMenu(isSubMenu);
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
	
	public void removeItem(ActionHierarchyMember<TVW> item) {
		items.remove(item);
	}
	
	public ActionHierarchyMember<TVW> getItem(int i) {
		return items.get(i);
	}
	
	public int indexOf(ActionHierarchyMember<TVW> item) {
		return items.indexOf(item);
	}
	
	public int getItemsCount() {
		return items.size();
	}
	
	/**
	 * If this option is {@code true}, all items from the list
	 * will be placed into a submenu. Otherwise, they will take
	 * place right in the place where the {@link ActionList} item supposed
	 * to be. In that case, of course, the item itself will not be shown  
	 * @return {@code isSubMenu} value
	 */
	public boolean isSubMenu() {
		return isSubMenu;
	}

	/**
	 * If this option is {@code true}, all items from the list
	 * will be placed into a submenu. Otherwise, they will take
	 * place right in the place where the {@link ActionList} item supposed
	 * to be. In that case, of course, the item itself will not be shown  
	 * @param isSubMenu the new value 
	 */
	public void setSubMenu(boolean isSubMenu) {
		this.isSubMenu = isSubMenu;
	}
		
	public ActionHierarchyMember<TVW> findActionByIdRecursively(int id) {
		List<ActionList<TVW>> inners = new ArrayList<>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getId() == id) {
				return items.get(i);
			}
			
			if (items.get(i) instanceof ActionList) {
				inners.add((ActionList<TVW>)items.get(i));
			}
		}
		
		for (int i = 0; i < inners.size(); i++) {
			ActionHierarchyMember<TVW> res = inners.get(i).findActionByIdRecursively(id);
			if (res != null) return res;
		}
		
		return null;
	}
}
