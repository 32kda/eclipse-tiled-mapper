package com.onpositive.ai.playground.model;

import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class QProcessor {
	
	ListMultimap<Unit,Integer> unitRewards = ArrayListMultimap.create();
	IRewardCalculator rewardCalculator = new BasicRewardCalculator();
	
	private double discountFactor = 0.5; //Discount factor for future reward (see 'Bellman Equation')

	public QProcessor() {
	}
	
	public QProcessor(double discountFactor) {
		this.discountFactor = discountFactor;
	}
	
	public double[] getActualQs(Unit unit, int maxStepsBack) {
		List<Integer> rewards = unitRewards.get(unit);
		int minI = maxStepsBack == 0 ? 0 : (int) Math.max(0, rewards.size() - maxStepsBack);
		double curReward = 0;
		double[] result = new double[rewards.size() - minI];
		for (int i = rewards.size() - 1; i >= minI; i--) {
			curReward = rewards.get(i) + discountFactor * curReward;
			result[i-minI] = curReward;
		}
		return result;
	}

	public void registerRewards(UnitAction action) {
		int attackerReward = rewardCalculator.getAttackerReward(action);
		int targetReward = rewardCalculator.getTargetReward(action);
		
		action.unit.addReward(attackerReward);
		unitRewards.put(action.unit,attackerReward);
		
		if (action.attackTarget != null) {
			action.attackTarget.addReward(targetReward);
			List<Integer> values = unitRewards.get(action.attackTarget);
			if (values == null || values.isEmpty()) {
				unitRewards.put(action.attackTarget, targetReward);
			} else { //For target - this reward should be added to previous step reward, as far as most likely it was a result of previous move
				values.set(values.size() - 1, values.get(values.size() - 1).intValue() + targetReward);
			}
		}
	}

	public IRewardCalculator getRewardCalculator() {
		return rewardCalculator;
	}

	public void setRewardCalculator(IRewardCalculator rewardCalculator) {
		this.rewardCalculator = rewardCalculator;
	}

}
