package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

public class HighlightCurrentLayerAction extends AbstractActiveMapEditorAction {

	@Override
	public void run(IAction action) {
		editor.setHighlightCurrentLayer(action.isChecked());
	}

}
