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

import org.eclipse.jface.viewers.ISelectionProvider;

import tiled.core.Map;
import tiled.mapeditor.resources.Resources;

import com.onpositive.mapper.editors.MapEditor;

/**
 * Adds a layer to the current map and selects it.
 *
 * @version $Id$
 */
public class AddLayerAction extends AbstractLayerAction
{
//    public AddLayerAction(ISelectionProvider provider, MapEditor editor) {
//        super(provider,
//              editor,
//              Resources.getString("action.layer.add.name"),
//              Resources.getString("action.layer.add.tooltip"), Resources.getImageDescriptor("gnome-new.png"));
//    }

    protected void doPerformAction() {
        Map currentMap = editor.getMap();
        currentMap.addLayer();
        editor.setCurrentLayer(currentMap.getTotalLayers() - 1);
    }
    
    @Override
	public boolean calcEnabled() {
		return true;
	}
    
    @Override
    public boolean isEnabled() {
    	return true;
    }

}
