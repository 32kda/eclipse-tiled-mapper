package com.onpositive.ai.playground.ai;

import com.mulesoft.nn.annotions.In;
import com.mulesoft.nn.annotions.Out;

public class GameStateDTO {
	
	@Out
	private double qValue;
	
	@In
	private boolean[][] obstacles;
	@In
	private UnitActionDTO unitAction;
	@In
	private UnitDTO[][] units;
	
	public GameStateDTO(boolean[][] obstacles, UnitDTO[][] unitDTOs, UnitActionDTO unitAction) {
		this.obstacles = obstacles;
		this.units = unitDTOs;
		this.unitAction = unitAction;
	}

	public double getQValue() {
		return qValue;
	}
	
	
}
