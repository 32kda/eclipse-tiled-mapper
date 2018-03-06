package com.onpositive.ai.playground.ai;

public class GameStateDTO {
	
	private boolean[][] obstacles;
	private UnitActionDTO unitAction;
	private UnitDTO[] units;
	
	public GameStateDTO(boolean[][] obstacles, UnitDTO[] units, UnitActionDTO unitAction) {
		this.obstacles = obstacles;
		this.units = units;
		this.unitAction = unitAction;
	}
	
	
}
