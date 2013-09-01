package crossbase.ui.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import crossbase.abstracts.ViewWindow;

public class ActionList<TVW extends ViewWindow<?>> extends Action<TVW> {

	private LinkedList<ActionHierarchyMember<TVW>> items = new LinkedList<>();
	private boolean isSubMenu = true;
	private boolean isRadioItems;
	
	public ActionList() {
		super();
	}
	
	public ActionList(String title) {
		super(title);
	}

	public ActionList(String title, boolean isSubMenu) {
		super(title);
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
	
	public boolean isRadioItems() {
		return isRadioItems;
	}
	
	public void setRadioItems(boolean radioItems) {
		this.isRadioItems = radioItems;
	}
}
