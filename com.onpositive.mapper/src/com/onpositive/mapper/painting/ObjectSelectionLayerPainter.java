package com.onpositive.mapper.painting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import tiled.core.MapLayer;
import tiled.core.ObjectGroup;

public class ObjectSelectionLayerPainter implements ISpecialLayerPainter {

	@Override
	public void paintSpecialLayer(GC gc, MapLayer layer) {
		if (layer instanceof ObjectGroup) {
			Rectangle bounds = ((ObjectGroup)layer).getPixelBounds();
			gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_CYAN));
			gc.setLineWidth(3);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawRectangle(bounds);
			gc.setLineWidth(1);
			gc.setLineStyle(SWT.LINE_SOLID);
		}
	}

	@Override
	public boolean needRegularPaint(MapLayer layer) {
		return !(layer instanceof ObjectGroup); 
	}

}
