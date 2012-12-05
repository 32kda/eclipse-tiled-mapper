package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;

public class ShowGridAction extends AbstractActiveMapEditorAction {
	
	@Override
	public void run(IAction action) {
		editor.setShowGrid(action.isChecked());
	}

}
