package com.onpositive.ai.playground.ui;

import com.onpositive.ai.playground.model.ITurnController;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;

public interface IUITurnController extends ITurnController{
	
	public void cellSelected(Position position);
	public void targetSelected(Unit unit);
	public void cellHovered(Position position);
	public void targetHovered(Unit unit);
	
}
