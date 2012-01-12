/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package com.onpositive.mapper.actions;

import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.mapeditor.undo.MapLayerStateEdit;

import com.onpositive.mapper.editors.MapEditor;

/**
 * Provides a common abstract class for actions that modify the layer
 * configuration. It makes sure the undo/redo information is properly
 * maintained.
 *
 * todo: These actions will need to listen to changing of the current selected
 * todo: layer index as well as changes to the opened map. Action should always
 * todo: be disabled when no map is opened. More specific checks should be
 * todo: included in subclasses.
 *
 * @version $Id$
 */
public abstract class AbstractLayerAction extends SelectionProviderAction
{
    protected final MapEditor editor;

    protected AbstractLayerAction(ISelectionProvider provider,
                                  MapEditor editor, String name, String description)
    {
        super(provider, name);
        setDescription(description);
//        putValue(SHORT_DESCRIPTION, description);
//        putValue(ACTION_COMMAND_KEY, name);
        this.editor = editor;
        setEnabled(calcEnabled());
    }

    protected AbstractLayerAction(ISelectionProvider provider,
                                  MapEditor editor, String name, String description, ImageDescriptor icon)
    {
        this(provider, editor, name, description);
        setImageDescriptor(icon);
    }
    
    @Override
    public void selectionChanged(IStructuredSelection selection) {
    	setEnabled(calcEnabled());
    }
    
    public boolean calcEnabled() {
    	return !getSelection().isEmpty();
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
        MapLayerStateEdit mapLayerStateEdit = //TODO add undo support 
                new MapLayerStateEdit(map, layersBefore, layersAfter,
                                      getText());
        editor.addEdit(mapLayerStateEdit);
    }

    /**
     * Actually performs the action that modifies the layer configuration.
     */
    protected abstract void doPerformAction();
}
