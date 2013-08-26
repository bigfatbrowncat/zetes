package crossbase.ui.actions;

import crossbase.abstracts.Document;
import crossbase.abstracts.ViewWindow;

public interface ActionHierarchyMember<TD extends Document, TVW extends ViewWindow<TD>> {
	String getTitle();

}
