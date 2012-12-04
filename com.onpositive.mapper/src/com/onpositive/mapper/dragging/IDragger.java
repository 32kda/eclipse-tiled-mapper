package com.onpositive.mapper.dragging;

import org.eclipse.swt.events.MouseEvent;


public interface IDragger {
	public void handleMove(MouseEvent e);
	public void canStartDrag(MouseEvent e);
	public void handleDragStart(MouseEvent e);
	public void handleDrag(MouseEvent e);
	public void canFinishDrag(MouseEvent e);
	public void handleDragFinish(MouseEvent e);
}
