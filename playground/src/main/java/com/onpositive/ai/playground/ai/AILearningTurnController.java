package com.onpositive.ai.playground.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mulesoft.nn.api.ITrainableModel;
import com.onpositive.ai.playground.model.Game;
import com.onpositive.ai.playground.model.ITurnController;
import com.onpositive.ai.playground.model.Position;
import com.onpositive.ai.playground.model.Unit;
import com.onpositive.ai.playground.model.UnitAction;
import com.onpositive.ai.playground.model.UnitSide;

public class AILearningTurnController implements ITurnController {
	
	private ITrainableModel<GameStateDTO> trainableModel;
	
	private Random random = new Random();
	
	private double explorationRatio = 0.3;
	private double explorationMultiplier = 0.95;
	private double minExplorationRatio = 0.01;
	
	private Game game;
	private DTOFactory factory = new DTOFactory(game);

	private int currentTurn = 0;
	private Multimap<Unit, GameStateDTO> rememberedActions = ArrayListMultimap.create();

	public AILearningTurnController(Game game) {
		this.game = game;
	}

	@Override
	public void requestAction(Unit unit) {
		List<UnitAction> possibleActions = new ArrayList<>();
		List<Position> reachableCells = game.getReachableCells(unit);
		for (Position position : reachableCells) {
			possibleActions.add(new UnitAction(unit,position,null));
			List<Unit> attackableUnits = game.getAttackableUnits(unit,position);
			for (Unit target : attackableUnits) {
				possibleActions.add(new UnitAction(unit,position,target));
			}
		}

		UnitAction action = chooseAndRememberAction(unit,possibleActions);
		game.finishTurn(action);
	}

	private UnitAction chooseAndRememberAction(Unit unit, List<UnitAction> possibleActions) {
		List<GameStateDTO> stateDTOs = possibleActions.stream().map(unitAction -> factory.createGameStateDTO(unitAction)).collect(Collectors.toList());
		double dice = random.nextDouble();
		UnitAction action = null;
		GameStateDTO gameStateDTO = null;
		if (dice < explorationRatio) {
			int randomChoice = random.nextInt(stateDTOs.size());
			action = possibleActions.get(randomChoice);
			gameStateDTO = stateDTOs.get(randomChoice);
		} else { 
			trainableModel.predict(stateDTOs);
			gameStateDTO = stateDTOs.stream().max((a1,a2) -> (int)Math.signum(a1.getQValue() - a2.getQValue())).get();
			action = possibleActions.get(stateDTOs.indexOf(gameStateDTO));
		}
		rememberedActions.put(unit,gameStateDTO);
		return action;
	}

	@Override
	public void actionPerformed(UnitAction action) {
		int currentTurn = game.getCurrentTurn();
		if (currentTurn > this.currentTurn && explorationRatio > minExplorationRatio) { 
			explorationMultiplier =  Math.max(minExplorationRatio, explorationRatio * explorationMultiplier);
		}
		this.currentTurn = currentTurn;
	}

	@Override
	public void gameFinished(UnitSide side) {
		rememberedActions.keySet().stream().forEach(unit -> {
			Collection<GameStateDTO> actions = rememberedActions.get(unit);
			double[] actualQs = game.getQProcessor().getActualQs(unit, 0);
			if (actualQs.length != actions.size()) {
				throw new IllegalStateException("Actual Q values array size (" + actualQs.length + ") does not match remembered actions count (" + actions.size() + ")");
			}
		});
//		trainableModel.fit() //TODO

	}

}
