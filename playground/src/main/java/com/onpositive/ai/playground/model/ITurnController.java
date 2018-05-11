package com.onpositive.ai.playground.model;

import java.util.function.Consumer;

public interface ITurnController {
	
	public void requestAction(Unit unit, Consumer<UnitAction> callback);
	
	public void actionPerformed(UnitAction action);
	
	public void gameFinished(UnitSide side);

}
