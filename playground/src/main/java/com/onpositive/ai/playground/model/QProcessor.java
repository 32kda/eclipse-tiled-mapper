package com.onpositive.ai.playground.model;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;

public class QProcessor {
	
	ListMultimap<Unit,Integer> unitRewards = ArrayListMultimap.create();
	IRewardCalculator rewardCalculator = new BasicRewardCalculator();
	
	private double discountFactor = 0.5; //Discount factor for future reward (see 'Bellman Equation')
	private double rewardEpsilon = 0.02; //A lowest value of future reward we need to still take into account

	public QProcessor() {
	}
	
	
	
	public QProcessor(double discountFactor, double rewardEpsilon) {
		this.discountFactor = discountFactor;
		this.rewardEpsilon = rewardEpsilon;
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
			} else {
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
