package zetes.wings.base;

import java.util.LinkedList;

import zetes.wings.abstracts.Document;
import zetes.wings.abstracts.DocumentListener;

public abstract class DocumentBase implements Document {

	private LinkedList<DocumentListener> listeners = new LinkedList<>(); 
	
	@Override
	public void addListener(DocumentListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(DocumentListener listener) {
		listeners.remove(listener);
	}

	protected void issueTitleChanged() {
		for (DocumentListener listener : listeners) {
			listener.titleChanged(this);
		}
	}
}
