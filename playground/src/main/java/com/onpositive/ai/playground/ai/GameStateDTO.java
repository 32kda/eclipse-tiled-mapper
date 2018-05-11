package com.onpositive.ai.playground.ai;

import com.mulesoft.nn.annotions.DataSetName;
import com.mulesoft.nn.annotions.In;
import com.mulesoft.nn.annotions.MaxLen;
import com.mulesoft.nn.annotions.Out;

@DataSetName("Playground")
public class GameStateDTO {
	
	@Out
	private double qValue;
	
	@In
	@MaxLen(20)
	private boolean[][] obstacles;
	@In
	private UnitActionDTO unitAction;
	@In
	@MaxLen(20)
	private UnitDTO[][] units;
	
	public GameStateDTO(boolean[][] obstacles, UnitDTO[][] unitDTOs, UnitActionDTO unitAction) {
		this.obstacles = obstacles;
		this.units = unitDTOs;
		this.unitAction = unitAction;
	}

	public double getQValue() {
		return qValue;
	}

	public void setQValue(double qValue) {
		this.qValue = qValue;
	}
	
}
