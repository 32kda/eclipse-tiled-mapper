package com.onpositive.ai.playground.ai;

import java.util.Collection;

import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitSide;
import com.onpositive.ai.playground.model.UnitType;

import tiled.core.Map;
import tiled.io.TMXMapReader;

public class GameSamplesProvider implements ISamplesProvider<GameStateDTO> {
	
	private Game game;
	private AILearningTurnController turnController;

	public GameSamplesProvider() {
		try {
			Map currentMap = new TMXMapReader().readMap("playground1.tmx");

			System.out.println(currentMap.toString() + " loaded");

			game = new Game(currentMap);
			addUnits(game);
			turnController = new AILearningTurnController(game);
			game.setSideTurnController(UnitSide.LEFT, turnController);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Collection<GameStateDTO> provideNextBatch() {
		while (!game.isGameFinished()) {
			game.nextTurn();
		}
		return turnController.getAllActions();
	}
	
	private static void addUnits(Game game) {
		int leftCol = 3;
//		int rightCol = 16;
		int row = 6;

		game.addUnit(new Unit(UnitType.WARRIOR,UnitSide.LEFT, new Position(leftCol,row)));
		
	}

}
