package com.onpositive.ai.playground.model;

public class UnitAction {
	
	public final Unit unit;
	public final Position oldPosition;
	public final Position moveTo;
	public final Unit attackTarget;
	
	public UnitAction(Unit unit,Position moveTo, Unit attackTarget) {
		this.unit = unit;
		this.oldPosition = unit.getPosition();
		this.moveTo = moveTo;
		this.attackTarget = attackTarget;
	}

}
