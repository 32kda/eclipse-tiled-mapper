package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;
import com.onpositive.mapper.ui.UIUtil;

public class HighlightLayerActionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = UIUtil.getActiveEditor();
		if (activeEditor != null && activeEditor instanceof MapEditor) {
			Command command = event.getCommand();
			State state = command.getState("STYLE");
			state.setValue(!(Boolean) state.getValue());
			((MapEditor) activeEditor).setHighlightCurrentLayer((Boolean) state.getValue());
		}
		return null;
	}

}
