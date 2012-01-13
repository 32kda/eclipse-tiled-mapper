package com.onpositive.mapper.actions;

import java.util.Vector;

import org.eclipse.jface.action.Action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.undo.MapLayerStateEdit;

import com.onpositive.mapper.editors.MapEditor;

public abstract class AbstractLayerAction extends Action implements
		IEditorActionDelegate {
	
	IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(MapEditor.CURRENT_LAYER_PROP))
				setEnabled(calcEnabled(new StructuredSelection()));
		}
	};
	
	protected MapEditor editor;

	protected IAction action;

	@Override
	public void run(IAction action) {
		run();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		setEnabled(calcEnabled(selection));
	}
	
    public boolean calcEnabled(ISelection selection) {
    	return editor != null && editor.getCurrentLayerIndex() >= 0;
    }

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof MapEditor) {
			if (action != this)
				this.action = action;
			if (editor != null)
				editor.removePartPropertyListener(propertyChangeListener);
			editor = (MapEditor) targetEditor;
			editor.addPartPropertyListener(propertyChangeListener);
		} else {
			editor = null;
		}
	}
	
    @Override
    public void run() {
    	 // Capture the layers before the operation is executed.
        Map map = editor.getMap();
        Vector<MapLayer> layersBefore = new Vector<MapLayer>(map.getLayerVector());

        doPerformAction();

        // Capture the layers after the operation is executed and create the
        // layer state edit instance.
        Vector<MapLayer> layersAfter = new Vector<MapLayer>(map.getLayerVector());
        MapLayerStateEdit mapLayerStateEdit =  
                new MapLayerStateEdit(map, layersBefore, layersAfter,
                                      getText());
        editor.addEdit(mapLayerStateEdit);
    }

    /**
     * Actually performs the action that modifies the layer configuration.
     */
    protected abstract void doPerformAction();

}
