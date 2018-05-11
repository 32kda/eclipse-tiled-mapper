package com.onpositive.ai.playground.ai;

import com.mulesoft.nn.annotions.In;
import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.util.ArrayUtil;

public class UnitActionDTO {
	
	@In
	private boolean[][] moveTo;
	
	@In
	private boolean[][] attackTarget;
	
	public UnitActionDTO(UnitAction unitAction, Game game) {
		moveTo = DTOFactory.initArray(game);
		attackTarget = DTOFactory.initArray(game);
		moveTo[unitAction.moveTo.y][unitAction.moveTo.x] = true;
		if (unitAction.attackTarget != null) {
			attackTarget[unitAction.attackTarget.getPosition().y][unitAction.attackTarget.getPosition().x] = true;	
		}
	}

}
