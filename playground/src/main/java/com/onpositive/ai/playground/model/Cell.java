package com.onpositive.ai.playground.model;

public class Cell {
	
	private boolean obstacle = false;
	private Unit unit = null;
	
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

}
