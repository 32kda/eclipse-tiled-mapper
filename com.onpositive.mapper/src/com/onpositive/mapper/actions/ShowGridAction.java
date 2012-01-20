package com.onpositive.mapper.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

public class ShowGridAction extends AbstractActiveMapEditorAction {
	
	@Override
	public void run(IAction action) {
		editor.setShowGrid(action.isChecked());
	}

}
