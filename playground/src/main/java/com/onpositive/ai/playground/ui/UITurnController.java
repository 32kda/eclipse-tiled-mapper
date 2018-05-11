package com.onpositive.ai.playground.ui;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JTextPane;

import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

public class UITurnController implements IUITurnController {
	
	private GameView gameView;
	private Position movePosition;
	private Unit curUnit;
	private JTextPane infoPane;
	private UnitSide wonSide;
	private Consumer<UnitAction> callback;

	public UITurnController(GameView gameView, JTextPane infoPane) {
		this.gameView = gameView;
		this.infoPane = infoPane;
	}

	@Override
	public void requestAction(Unit unit, Consumer<UnitAction> callback) {
		this.curUnit = unit;
		this.callback = callback;
		List<Position> reachableCells = gameView.getGame().getReachableCells(unit);
		gameView.setCurUnit(unit);
		gameView.setReachableCells(reachableCells);
		if (curUnit.getType().attackRange == 1) {
			List<Unit> attackableUnits = reachableCells.stream().flatMap(position -> gameView.getGame().getAttackableUnits(unit,position).stream()).distinct().collect(Collectors.toList());
			gameView.setAttackableUnits(attackableUnits);
		} else {
			gameView.setAttackableUnits(gameView.getGame().getAttackableUnits(unit,unit.getPosition()));
		}
		gameView.refresh();
	}

	@Override
	public void actionPerformed(UnitAction action) {
		gameView.finishTurn(action);
		printUnitStats();
	}

	private void printUnitStats() {
		List<Unit> units = gameView.getGame().getUnits();
		String str = units.stream().sorted((u1,u2) -> u1.getSide().ordinal() - u2.getSide().ordinal()).map(unit -> getString(unit)).collect(Collectors.joining("\n"));
		String txt = "Turn: " + gameView.getGame().getCurrentTurn() + "\n" + str;
		if (wonSide != null) {
			txt += "\n " + wonSide + " won";
		}
		infoPane.setText(txt);
	}

	private String getString(Unit unit) {
		StringBuilder builder = new StringBuilder(); 
		builder.append(unit.getSide() == UnitSide.LEFT ? "L " : "R ");
		builder.append(unit.getType());
		builder.append(" reward: ");
		builder.append(unit.getReward());
		builder.append(" HP: ");
		builder.append(unit.getHealth());
		builder.append("/");
		builder.append(unit.getType().health);
		return builder.toString();
	}

	@Override
	public void gameFinished(UnitSide side) {
		this.wonSide = side;
	}

	@Override
	public void cellSelected(Position position) {
		if (curUnit != null) {
			this.movePosition = position;
		}
		gameView.setSelectedMovePosition(movePosition);
		if (curUnit.getType().attackRange > 1) {
			gameView.setAttackableUnits(gameView.getGame().getAttackableUnits(curUnit,movePosition));
		}
	}

	@Override
	public void targetSelected(Unit unit) {
		if (curUnit != null) {
			if (movePosition != null && unit == null) { //Move without attack
				checkAcceptTarget(unit);
			} else {
				if (movePosition == null) {
					movePosition = curUnit.getPosition(); //Attack without move
				}
				checkAcceptTarget(unit);
			}
			movePosition = null;
		}
	}
	
	public void checkAcceptTarget(Unit unit) {
		List<Unit> attackableUnits = gameView.getGame().getAttackableUnits(curUnit,movePosition);
		if (unit == null || attackableUnits.contains(unit)) {
			UnitAction action = new UnitAction(curUnit, movePosition, unit);
			callback.accept(action);
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
