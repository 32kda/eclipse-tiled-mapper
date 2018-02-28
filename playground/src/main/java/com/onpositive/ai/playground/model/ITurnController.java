package com.onpositive.ai.playground.model;

public interface ITurnController {
	
	public void requestAction(Unit unit);
	
	public void actionPerformed(UnitAction action);
	
	public void gameFinished(UnitSide side);
}
