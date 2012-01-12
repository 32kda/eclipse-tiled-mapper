package com.onpositive.mapper.actions;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.onpositive.mapper.editors.MapEditor;

public class CopyAction extends AbstractMapEditorAction implements IPropertyChangeListener {
	
	public CopyAction(MapEditor mapEditor) {
		super(mapEditor);
	}
	
	@Override
	public void run() {
		mapEditor.copySelection();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(MapEditor.MARQEE_SELECTION_PROP))
			setEnabled(mapEditor.hasSelection());
	}

}
