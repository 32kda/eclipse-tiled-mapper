package com.onpositive.ai.playground.model;

public interface IRewardCalculator {
	
	public int getAttackerReward(Unit attacker, Unit target, int damage);
	
	public int getTargetReward(Unit attacker, Unit target, int damage);
	
}
