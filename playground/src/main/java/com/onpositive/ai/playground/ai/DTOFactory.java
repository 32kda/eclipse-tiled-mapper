package com.onpositive.ai.playground.ai;

import java.util.List;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;

public class DTOFactory {
	
	private boolean[][] obstacles;
	private Game game;
	
	public DTOFactory(Game game) {
		this.game = game;
		obstacles = new boolean[game.getGameMap().getHeight()][];
		for (int i = 0; i < obstacles.length; i++) {
			obstacles[i] = new boolean[game.getGameMap().getWidth()];
			for (int j = 0; j < obstacles[i].length; j++) {
				obstacles[i][j] = game.isObstacleCell(j,i);
			}
		}
	}
	
	public GameStateDTO createGameStateDTO(UnitAction action) {
		UnitDTO[][] unitDTOs = new UnitDTO[game.getGameMap().getHeight()][];
		for (int i = 0; i < unitDTOs.length; i++) {
			unitDTOs[i] = new UnitDTO[game.getGameMap().getWidth()];
			for (int j = 0; j < unitDTOs[i].length; j++) {
				Unit unit = game.getUnit(j,i);
				if (unit != null) {
					unitDTOs[i][j] = new UnitDTO(unit, unit == action.unit, unit == action.attackTarget);
				} else {
					unitDTOs[i][j] = new UnitDTO();
				}
			}
		}
		UnitActionDTO actionDTO = new UnitActionDTO(action, game);
		return new GameStateDTO(obstacles,unitDTOs,actionDTO);
	}
	
	public static boolean[][] initArray(Game game) {
		boolean[][] res = new boolean[game.getGameMap().getHeight()][];
		for (int i = 0; i < res.length; i++) {
			res[i] = new boolean[game.getGameMap().getWidth()];
		}
		return res;
	}

}
