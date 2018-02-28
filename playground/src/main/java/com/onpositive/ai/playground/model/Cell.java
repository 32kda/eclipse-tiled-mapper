package com.onpositive.ai.playground.model;

public class Cell {
	
	private boolean obstacle = false;
	private Unit unit = null;
	private UnitSide unitSide = null;
	
	public boolean isObstacle() {
		return obstacle;
	}
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public UnitSide getUnitSide() {
		return unitSide;
	}
	public void setUnitSide(UnitSide unitSide) {
		this.unitSide = unitSide;
	}

}
