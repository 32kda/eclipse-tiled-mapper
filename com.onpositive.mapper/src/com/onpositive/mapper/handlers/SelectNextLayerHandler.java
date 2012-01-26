package com.onpositive.mapper.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;

import com.onpositive.mapper.editors.MapEditor;
import com.onpositive.mapper.ui.UIUtil;

public class SelectNextLayerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = UIUtil.getActiveEditor();
		if (activeEditor instanceof MapEditor)
			((MapEditor) activeEditor).selectNextLayer();
		return null;
	}

}
