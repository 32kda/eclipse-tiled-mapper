package com.onpositive.ai.playground.ai;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.ITurnController;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

public class AILearningTurnController implements ITurnController {
	
	private Game game;

	public AILearningTurnController(Game game) {
		this.game = game;
	}

	@Override
	public void requestAction(Unit unit) {
		List<UnitAction> possibleActions = new ArrayList<>();
		List<Position> reachableCells = game.getReachableCells(unit);
		for (Position position : reachableCells) {
			possibleActions.add(new UnitAction(unit,position,null));
			List<Unit> attackableUnits = game.getAttackableUnits(unit,position);
			for (Unit target : attackableUnits) {
				possibleActions.add(new UnitAction(unit,position,target));
			}
		}
	}

	@Override
	public void actionPerformed(UnitAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gameFinished(UnitSide side) {
		// TODO Auto-generated method stub

	}

}
