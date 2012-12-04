package com.onpositive.mapper.dragging;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.onpositive.mapper.editors.MapEditor;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.util.AnchoringUtil;
import tiled.view.MapView;

public class ObjectResizeDragger implements IDragger {
	
	protected final MapView mapView;
	protected Map map;
	protected final MapEditor mapEditor;

	public ObjectResizeDragger(MapEditor mapEditor, MapView mapView) {
		this.mapEditor = mapEditor;
		this.mapView = mapView;
		map = mapView.getMap();
	}

	@Override
	public void handleMove(MouseEvent e) {
		if (mapEditor.getCurrentPointerState() != MapEditor.PS_MOVEOBJ)
			return;
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_ARROW));
		if (layer instanceof ObjectGroup) {
			MapObject targetObject = ((ObjectGroup) layer).getObjectAt(e.x, e.y);
			if (targetObject != null) {
				Rectangle bounds = ((ObjectGroup) layer).getActualObjectRectangle(targetObject);
				Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
				for (int i = 0; i < anchorRects.length; i++) {
					if (anchorRects[i].contains(e.x,e.y)) {
						if (i == AnchoringUtil.ANCHOR_TOP || i == AnchoringUtil.ANCHOR_BOTTOM) {
							mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZEN));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LEFT || i == AnchoringUtil.ANCHOR_RIGHT) {
							mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZEWE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LT || i == AnchoringUtil.ANCHOR_RB) {
							mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZENWSE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LB || i == AnchoringUtil.ANCHOR_RT) {
							mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZENESW));
							return;
						}

					}
				}
				if (e.x >= bounds.x && e.x <= bounds.x + 5 && e.y >= bounds.y && e.y <= bounds.y + 5) {
					mapView.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_SIZENWSE));
				}
			}
		}
	}

	@Override
	public void canStartDrag(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragStart(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrag(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void canFinishDrag(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDragFinish(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
