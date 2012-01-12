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

package tiled.mapeditor.undo;

import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import tiled.core.Map;
import tiled.core.MapLayer;

/**
 * A change in the layer state. Used for adding, removing and rearranging
 * the layer stack of a map.
 */
public class MapLayerStateEdit extends AbstractOperation
{
    private final Map map;
    private final Vector<MapLayer> layersBefore;
    private final Vector<MapLayer> layersAfter;
    private final String name;

    public MapLayerStateEdit(Map m,
                             Vector<MapLayer> before,
                             Vector<MapLayer> after,
                             String name) {
    	super(name);
        map = m;
        layersBefore = before;
        layersAfter = after;
        this.name = name;
    }

    public String getPresentationName() {
        return name;
    }
    
    @Override
    public boolean canExecute() {
    	return false;
    }

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		map.setLayerVector(layersAfter);
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		map.setLayerVector(layersBefore);
		return Status.OK_STATUS;
	}
}
