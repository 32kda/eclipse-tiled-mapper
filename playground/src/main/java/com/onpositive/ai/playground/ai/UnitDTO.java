package com.onpositive.ai.playground.ai;

import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;
import com.onpositive.ai.playground.model.UnitType;

public class UnitDTO {
	
	private boolean current;
	private boolean target;
	private UnitSide unitSide;
	private UnitType unitType;
	private double health;

	public UnitDTO(Unit unit, boolean current, boolean target) {
		this.current = current;
		this.target = target;
		this.unitSide = unit.getSide();
		this.unitType = unit.getType();
		this.health = unit.getHealth() * 1.0 / unitType.health;
	}
	
	public UnitDTO() {
		unitSide = null;
		unitType = null;
		health = 0;
	}

}
