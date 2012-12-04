package com.onpositive.mapper.dragging;

import static tiled.util.AnchoringUtil.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
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
	protected Point initialDragLocation = new Point(0, 0);
	protected int dragType = 0;
	protected Rectangle initialBounds;
	protected MapObject targetObject;

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
		mapView.setCursor(Display.getDefault()
				.getSystemCursor(SWT.CURSOR_ARROW));
		if (layer instanceof ObjectGroup) {
			MapObject targetObject = ((ObjectGroup) layer)
					.getObjectAt(e.x, e.y);
			if (targetObject != null) {
				Rectangle bounds = ((ObjectGroup) layer)
						.getActualObjectRectangle(targetObject);
				Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
				for (int i = 0; i < anchorRects.length; i++) {
					if (anchorRects[i].contains(e.x, e.y)) {
						if (i == AnchoringUtil.ANCHOR_TOP
								|| i == AnchoringUtil.ANCHOR_BOTTOM) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZEN));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LEFT
								|| i == AnchoringUtil.ANCHOR_RIGHT) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZEWE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LT
								|| i == AnchoringUtil.ANCHOR_RB) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZENWSE));
							return;
						}
						if (i == AnchoringUtil.ANCHOR_LB
								|| i == AnchoringUtil.ANCHOR_RT) {
							mapView.setCursor(Display.getDefault()
									.getSystemCursor(SWT.CURSOR_SIZENESW));
							return;
						}

					}
				}
				if (e.x >= bounds.x && e.x <= bounds.x + 5 && e.y >= bounds.y
						&& e.y <= bounds.y + 5) {
					mapView.setCursor(Display.getDefault().getSystemCursor(
							SWT.CURSOR_SIZENWSE));
				}
			}
		}
	}

	@Override
	public boolean canStartDrag(MouseEvent e) {
		if (mapEditor.getCurrentPointerState() != MapEditor.PS_MOVEOBJ)
			return false;
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		if (layer instanceof ObjectGroup) {
			MapObject targetObject = ((ObjectGroup) layer)
					.getObjectAt(e.x, e.y);
			if (targetObject != null) {
				Rectangle bounds = ((ObjectGroup) layer)
						.getActualObjectRectangle(targetObject);
				Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
				for (Rectangle rectangle : anchorRects) {
					if (rectangle.contains(e.x, e.y))
						return true;
				}
			}
		}
		return false;

	}

	@Override
	public void handleDragStart(MouseEvent e) {
		MapLayer layer = map.getLayer(mapView.getCurrentLayer());
		targetObject = ((ObjectGroup) layer).getObjectAt(e.x, e.y);
		if (targetObject != null) {
			Rectangle bounds = ((ObjectGroup) layer)
					.getActualObjectRectangle(targetObject);
			Rectangle targetBounds = targetObject.getBounds();
			initialBounds = new Rectangle(targetBounds.x,targetBounds.y,targetBounds.width,targetBounds.height);
			Rectangle[] anchorRects = AnchoringUtil.getAnchorRects(bounds);
			for (int i = 0; i < anchorRects.length; i++) {
				if (anchorRects[i].contains(e.x, e.y)) {
					initialDragLocation.x = e.x;
					initialDragLocation.y = e.y;
					dragType = i;
				}
			}
		}
	}

	@Override
	public void handleDrag(MouseEvent e) {
		Point delta = new Point(e.x - initialDragLocation.x, e.y - initialDragLocation.y);
		int x = initialBounds.x;
		int y = initialBounds.y;
		int width = initialBounds.width;
		int height = initialBounds.height;
		if (dragType == ANCHOR_LEFT || dragType == ANCHOR_LT || dragType == ANCHOR_LB) {
			delta.x = Math.min(delta.x,width);
			x += delta.x;
			width -= delta.x;
		}
		if (dragType == ANCHOR_TOP || dragType == ANCHOR_LT || dragType == ANCHOR_RT) {
			delta.y = Math.min(delta.y,width);
			y += delta.y;
			height -= delta.y;
		}
		
		targetObject.setBounds(new Rectangle(x,y,width,height));
	}

	@Override
	public boolean canFinishDrag(MouseEvent e) {
		return true;
	}

	@Override
	public void handleDragFinish(MouseEvent e) {
		// TODO Always return to initial for now
		targetObject.setBounds(initialBounds);
	}

}
