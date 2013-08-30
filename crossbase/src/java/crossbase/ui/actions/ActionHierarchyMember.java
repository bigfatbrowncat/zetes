package crossbase.ui.actions;

import crossbase.abstracts.ViewWindow;

public abstract class ActionHierarchyMember<TVW extends ViewWindow<?>> {
	private int id;
	private String title;
	
	public ActionHierarchyMember(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public ActionHierarchyMember(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

}
