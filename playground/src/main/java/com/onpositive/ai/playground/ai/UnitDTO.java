package com.onpositive.ai.playground.ai;

import com.mulesoft.nn.annotions.In;
import com.mulesoft.nn.annotions.OneHot;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;
import com.onpositive.ai.playground.model.UnitType;

public class UnitDTO {
	
	@In
	private boolean current;
	@In
	private boolean target;
	@In
	@OneHot
	private UnitSide unitSide;
	@In
	@OneHot
	private UnitType unitType;
	@In
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
