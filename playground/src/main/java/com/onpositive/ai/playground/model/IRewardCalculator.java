package com.onpositive.ai.playground.model;

public interface IRewardCalculator {
	
	public int getAttackerReward(UnitAction action);

	public int getTargetReward(UnitAction action);
	
	public UnitSide getWonSide(Game game);
	
}
