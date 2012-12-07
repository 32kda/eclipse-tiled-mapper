package com.onpositive.mapper.dragging;

import java.util.Iterator;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.core.ObjectSelectionLayer;

public class ObjectSelectionDragger implements IDragger {
	
	private static int SELECT_MODE = 0;
	private static int MOVE_MODE = 1;
	
	protected Point initialDragLocation = new Point(0, 0);
	private final MapEditor editor;
	private ObjectSelectionLayer selectionLayer;
	private int currentMode = SELECT_MODE;
	
	public ObjectSelectionDragger(MapEditor editor) {
		this.editor = editor;
	}

	@Override
	public void handleMove(MouseEvent e) {
		// Do nothing
	}

	@Override
	public boolean canStartDrag(MouseEvent e) {
		if (editor.getCurrentPointerState() != MapEditor.PS_MARQUEE)
			return false;
		return editor.getCurrentLayer() instanceof ObjectGroup;
	}

	@Override
	public void handleDragStart(MouseEvent e) {
		initialDragLocation.x = e.x;
		initialDragLocation.y = e.y;
		Iterator<MapLayer> layersSpecial = editor.getMap().getLayersSpecial();
		currentMode = SELECT_MODE;
		for (Iterator<MapLayer> iterator = layersSpecial; iterator.hasNext();) {
			MapLayer layer = iterator.next();
			if (layer == selectionLayer && layer.getBounds().contains(initialDragLocation.x,initialDragLocation.y)) {
				currentMode = MOVE_MODE;
			}
		}
		if (currentMode == SELECT_MODE) {
			if (selectionLayer != null) {
				editor.getMap().removeLayerSpecial(selectionLayer);
			}
			selectionLayer = new ObjectSelectionLayer();
			editor.getMap().addLayerSpecial(selectionLayer);
			selectionLayer.setBounds(new Rectangle(e.x,e.y,1,1));
		}
	}

	@Override
	public void handleDrag(MouseEvent e) {
		setSelectionBounds(e);
	}

	protected void setSelectionBounds(MouseEvent e) {
		int x = initialDragLocation.x;
		int y = initialDragLocation.y;
		int width = e.x - initialDragLocation.x;
		int height = e.y - initialDragLocation.y;
		if (width < 0) {
			x = Math.max(0,x+width);
			width = -width;
		}
		if (height < 0) {
			y = Math.max(0,y+height);
			height = -height;
		}
		selectionLayer.setBounds(new Rectangle(x,y,width,height));
	}

	@Override
	public boolean canFinishDrag(MouseEvent e) {
		return true;
	}

	@Override
	public void handleDragFinish(MouseEvent e) {
		if (currentMode == SELECT_MODE) {
			setSelectionBounds(e);
			selectionLayer.maskedCopyFrom(editor.getCurrentLayer(), selectionLayer.getBounds());
			Iterator<MapObject> objects = selectionLayer.getObjects();
			if (!objects.hasNext()) {
				editor.getMap().removeLayerSpecial(selectionLayer);
				return;
			}
			Rectangle origBounds = selectionLayer.getBounds();
			int left = origBounds.x + origBounds.width;
			int right = origBounds.x;
			int top = origBounds.y + origBounds.height;
			int bottom = origBounds.y;
			for (; objects.hasNext();) {
				MapObject object = objects.next();
				Rectangle rect = object.getBounds();
				if (rect.x < left) 
					left = rect.x;
				if (rect.y < top)
					top = rect.y;
				if (rect.x + rect.width > right)
					right = rect.x + rect.width;
				if (rect.y + rect.height > bottom)
					bottom = rect.y + rect.height;
			}
			selectionLayer.setBounds(new Rectangle(left,top,right - left, bottom - top));
		} else if (currentMode == MOVE_MODE) {
			//TODO impl move mode
		}
	}

}
