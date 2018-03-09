package com.onpositive.ai.playground.model;

public class BasicRewardCalculator implements IRewardCalculator {
	
	private static final int DAMAGE_DONE_REWARD = 5;
	private static final int DAMAGE_TAKEN_REWARD = -3;
	private static final int KILL_REWARD = 10;
	private static final int DEATH_REWARD = -7;

	@Override
	public int getAttackerReward(UnitAction action) {
		if (action.attackTarget != null) {
			return action.attackTarget.isAlive() ? DAMAGE_DONE_REWARD : DAMAGE_DONE_REWARD + KILL_REWARD;
		}
		return 0;
	}

	@Override
	public int getTargetReward(UnitAction action) {
		if (action.attackTarget != null) {
			return action.attackTarget.isAlive() ? DAMAGE_TAKEN_REWARD : DAMAGE_TAKEN_REWARD + DEATH_REWARD;
		}
		return 0;
	}

}
