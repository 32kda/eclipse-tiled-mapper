package com.onpositive.ai.playground.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.ai.playground.model.ITurnController;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

public class UITurnController implements IUITurnController {
	
	private GameView gameView;
	private Position movePosition;
	private Unit curUnit;

	public UITurnController(GameView gameView) {
		this.gameView = gameView;
	}

	@Override
	public void requestAction(Unit unit) {
		this.curUnit = unit;
		List<Position> reachableCells = gameView.getGame().getReachableCells(unit);
		List<Unit> attackableUnits = reachableCells.stream().flatMap(position -> gameView.getGame().getAttackableUnits(unit,position).stream()).distinct().collect(Collectors.toList());
		gameView.setCurUnit(unit);
		gameView.setReachableCells(reachableCells);
		gameView.setAttackableUnits(attackableUnits);
		gameView.refresh();
	}

	@Override
	public void actionPerformed(UnitAction action) {
		
	}

	@Override
	public void gameFinished(UnitSide side) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cellSelected(Position position) {
		if (curUnit != null) {
			this.movePosition = position;
		}
	}

	@Override
	public void targetSelected(Unit unit) {
		if (curUnit != null) {
			if (movePosition != null && unit == null) { //Move without attack
				gameView.finishTurn(new UnitAction(curUnit, movePosition, unit));
			} else {
				if (movePosition == null) {
					movePosition = curUnit.getPosition(); //Attack without move
				}
				gameView.finishTurn(new UnitAction(curUnit, movePosition, unit));
			}
			movePosition = null;
		}
	}

	@Override
	public void cellHovered(Position position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void targetHovered(Unit unit) {
		// TODO Auto-generated method stub
		
	}

}
