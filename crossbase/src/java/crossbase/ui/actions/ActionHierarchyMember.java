package crossbase.ui.actions;

import crossbase.abstracts.ViewWindow;

public abstract class ActionHierarchyMember<TVW extends ViewWindow<?>> {
	public static final int NO_ID = -1;
	
	private int id;
	
	public ActionHierarchyMember(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

}
