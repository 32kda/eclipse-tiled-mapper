package tiled.core;

import java.util.Iterator;

import org.eclipse.swt.graphics.Rectangle;

public class ObjectSelectionLayer extends ObjectGroup {

	@Override
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
	}
	
	@Override
	public void maskedCopyFrom(MapLayer other, Rectangle mask) {
    	if (!(other instanceof ObjectGroup)) 
    		return;
    	for (Iterator<MapObject> iterator = ((ObjectGroup) other).getObjects(); iterator.hasNext();) {
    		MapObject object = (MapObject) iterator.next();
			Rectangle actualRect = ((ObjectGroup) other).getActualObjectRectangle(object);
			if (mask.contains(actualRect.x,actualRect.y) && mask.contains(actualRect.x + actualRect.width, actualRect.y + actualRect.height)) {
				addObject(object);
			}
		}
	}
}
