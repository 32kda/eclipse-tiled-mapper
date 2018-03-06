package com.onpositive.ai.playground.ai;

import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;
import com.onpositive.ai.playground.model.UnitType;

public class UnitDTO {
	
	private boolean current;
	private UnitSide unitSide;
	private UnitType unitType;
	private double health;

	public UnitDTO(Unit unit, boolean current) {
		this.current = current;
		this.unitSide = unit.getSide();
		this.unitType = unit.getType();
		this.health = unit.getHealth() * 1.0 / unitType.health;
	}
	
	public UnitDTO(Unit unit) {
		this(unit,false);
	}

}
