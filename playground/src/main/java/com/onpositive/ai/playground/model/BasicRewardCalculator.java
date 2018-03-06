package com.onpositive.ai.playground.model;

public class BasicRewardCalculator implements IRewardCalculator {
	
	private static final int DAMAGE_DONE_REWARD = 5;
	private static final int DAMAGE_TAKEN_REWARD = -3;
	private static final int KILL_REWARD = 10;
	private static final int DEATH_REWARD = -7;

	@Override
	public int getAttackerReward(Unit attacker, Unit target, int damage) {
		return target.isAlive() ? DAMAGE_DONE_REWARD : DAMAGE_DONE_REWARD + KILL_REWARD;
	}

	@Override
	public int getTargetReward(Unit attacker, Unit target, int damage) {
		return target.isAlive() ? DAMAGE_TAKEN_REWARD : DAMAGE_TAKEN_REWARD + DEATH_REWARD;
	}

}
