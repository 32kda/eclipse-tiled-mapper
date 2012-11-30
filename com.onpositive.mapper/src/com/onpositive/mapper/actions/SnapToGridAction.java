package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

public class SnapToGridAction extends AbstractActiveMapEditorAction {

	@Override
	public void run(IAction action) {
		editor.setSnapToGrid(action.isChecked());
	}

}
