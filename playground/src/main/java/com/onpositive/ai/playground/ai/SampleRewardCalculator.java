package com.onpositive.ai.playground.ai;

import java.util.List;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.IRewardCalculator;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

/**
 * Sample Reward Calculator, just giving using positive reward for moving right and negative - for moving left. No reward  given for attacking
 * @author 32kda
 *
 */
public class SampleRewardCalculator implements IRewardCalculator {

	@Override
	public int getOwnReward(UnitAction action) {
		return action.moveTo.x - action.oldPosition.x;
	}

	@Override
	public int getTargetReward(UnitAction action) {
		return 0;
	}

	@Override
	/**
	 * Victory condition is moving any unit to the right side of a map
	 */
	public UnitSide getWonSide(Game game) {
		int width = game.getGameMap().getWidth();
		List<Unit> units = game.getUnits();
		for (Unit unit : units) {
			if (unit.getPosition().x == width - 1) {
				return unit.getSide();
			}
		}
		return null;
	}

}
