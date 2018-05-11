package com.onpositive.ai.playground.model;

public interface IRewardCalculator {
	
	public int getOwnReward(UnitAction action);

	public int getTargetReward(UnitAction action);
	
	public UnitSide getWonSide(Game game);
	
}
