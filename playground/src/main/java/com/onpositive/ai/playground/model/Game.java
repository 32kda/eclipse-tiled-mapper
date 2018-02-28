package com.onpositive.ai.playground.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;

public class Game {
	
	private Cell[][] cells;
	private Map gameMap;
	private int xSize;
	private int ySize;
	private int[][] rangeTable; //"Rank" table for pathfinding
	
	private List<Unit> units = new ArrayList<>();
	private int curUnit = 0;
	private java.util.Map<UnitSide, ITurnController> sideTurnControllers = new HashMap<>();
	
	
	public Game(Map gameMap) {
		this.gameMap = gameMap;
		xSize = gameMap.getWidth();
		ySize = gameMap.getHeight();
		cells = new Cell[ySize][];
		rangeTable = new int[ySize][];
		TileLayer obstacleLayer = getObstacleLayer();
		for (int i = 0; i < cells.length; i++) {
			cells[i] = new Cell[xSize];
			rangeTable[i] = new int[xSize];
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = new Cell();
				cells[i][j].setObstacle(obstacleLayer != null && obstacleLayer.getTileAt(j,i) != null);
			}
		}
	}
	
	private TileLayer getObstacleLayer() {
		for (MapLayer layer : gameMap.getLayers()) {
			if ("obstacles".equals(layer.getName()) && layer instanceof TileLayer) {
				return (TileLayer) layer;
			}
		}
		return null;
	}
	
	public List<Unit> getAttackableUnits(Unit unit, Position attackPosition) {
		List<Unit> res = new ArrayList<>();
		int attackRange = unit.getType().attackRange;
		UnitSide side = unit.getSide();
		if (attackRange == 1) { //melee
			checkMeleeTarget(attackPosition.x - 1, attackPosition.y, side, res);
			checkMeleeTarget(attackPosition.x, attackPosition.y - 1, side, res);
			checkMeleeTarget(attackPosition.x + 1, attackPosition.y, side, res);
			checkMeleeTarget(attackPosition.x, attackPosition.y + 1, side, res);
		} else { //ranged attack
			findRangedTargets(attackPosition.x, attackPosition.y, side, attackRange, res);
		}
		return res;
	}
	
	private void findRangedTargets(int x, int y, UnitSide side, int attackRange, List<Unit> res) {
		for (Unit unit : units) {
			if (unit.getSide() != side && inDirectRange(x,y,unit.getPosition().x, unit.getPosition().y, attackRange)) {
				res.add(unit);
			}
		}
	}
	
	public List<UnitAction> getPossibleActions(Unit unit) {
		List<UnitAction> result = new ArrayList<>();
		List<Position> reachableCells = getReachableCells(unit);
		for (Position pos : reachableCells) {
			result.add(new UnitAction(unit, pos, null));
			List<Unit> attackableUnits = getAttackableUnits(unit, pos);
			for (Unit target : attackableUnits) {
				result.add(new UnitAction(unit, pos, target));	
			}
		}
		return result;
	}
	
	public void performAction(UnitAction action) {
		Unit unit = action.unit;
		unit.setPosition(action.moveTo);
		Unit targetUnit = action.attackTarget;
		if (targetUnit != null) {
			int damage = unit.getType().attack;
			targetUnit.takeDamage(damage);
			if (!targetUnit.isAlive()) {
				units.remove(unit);
			}
		}
	}
	
	public void addUnit(Unit unit) {
		units.add(unit);
		unit.setField(cells);
	}	
	
	public void setSideTurnController(UnitSide side, ITurnController controller) {
		sideTurnControllers.put(side, controller);
	}
	
	public void nextTurn() {
		Unit unit = units.get(curUnit);
		ITurnController controller = sideTurnControllers.get(unit.getSide());
		controller.requestAction(unit);
	}
	
	public void finishTurn(UnitAction action) {
		performAction(action);
		ITurnController controller = sideTurnControllers.get(action.unit.getSide());
		controller.actionPerformed(action);
		if (isGameFinished()) {
			sideTurnControllers.values().stream().forEach(contr -> contr.gameFinished(getWonSide()));
		} else {
			curUnit++;
			if (curUnit >= units.size()) {
				curUnit = 0;
			}
			nextTurn();
		}
	}

	public List<Position> getReachableCells(Unit unit) {
		int maxRange = unit.getType().range;
		int x = unit.getPosition().x;
		int y = unit.getPosition().y;
		return getReachableCells(x,y,maxRange);
	}

	public List<Position> getReachableCells(int x, int y, int maxRange) {
		Queue<Position> cellQueue = new LinkedList<>();
		for (int i = 0; i < rangeTable.length; i++) {
			Arrays.fill(rangeTable[i], Integer.MAX_VALUE);
		}
		rangeTable[y][x] = 0;
		Position ownPosition = new Position(x,y);
		cellQueue.add(ownPosition);
		List<Position> result = new ArrayList<>();
		result.add(ownPosition); //Since no move - is also a possible move
		
		while (!cellQueue.isEmpty()) {
			Position cur = cellQueue.poll();
			checkPossibleMoves(cellQueue, cur, maxRange);
			result.add(cur);
		}
		return result;
	}
	
	private void checkMeleeTarget(int x, int y, UnitSide unitSide, List<Unit> res) {
		if (x >= 0 && 
			x < xSize &&
			y >= 0 &&
			y < ySize) {
			Unit target = getTarget(x,y,unitSide);
			if (target != null) {
				res.add(target);
			}
			
		}
		
	}

	private Unit getTarget(int x, int y, UnitSide unitSide) {
		Unit unit = cells[y][x].getUnit();
		if (unit != null && unit.getSide() != unitSide) {
			return unit;
		}
		return null;
	}

	protected void checkPossibleMoves(Queue<Position> cellQueue, Position cur, int maxRange) {
		int range = rangeTable[cur.y][cur.x];
		if (range < maxRange) {
			int estimatedRange = range + 1;
			checkCandidate(cellQueue, cur.x - 1, cur.y, estimatedRange);
			checkCandidate(cellQueue, cur.x, cur.y - 1, estimatedRange);
			checkCandidate(cellQueue, cur.x + 1, cur.y, estimatedRange);
			checkCandidate(cellQueue, cur.x, cur.y + 1, estimatedRange);
		}
	}

	private void checkCandidate(Queue<Position> cellQueue, int x, int y, int esimatedRange) {
		if (x >= 0 && 
			x < xSize &&
			y >= 0 &&
			y < ySize &&
			rangeTable[y][x] > esimatedRange &&
			isFree(x,y)) {
			rangeTable[y][x] = esimatedRange;
			cellQueue.add(new Position(x,y));
		}
	}

	private boolean isFree(int x, int y) {
		return !cells[y][x].isObstacle() && cells[y][x].getUnit() == null;
	}
	
	private boolean inDirectRange(int x1, int y1, int x2, int y2, int range) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) <= range * range;
	}

	public Map getGameMap() {
		return gameMap;
	}

	public List<Unit> getUnits() {
		return units;
	}
	
	public boolean isGameFinished() {
		if (units.size() == 0) {
			return true;
		}
		return getWonSide() != null;
	}

	public UnitSide getWonSide() {
		UnitSide vonSide = units.get(0).getSide();
		for (int i = 1; i < units.size(); i++) {
			if (units.get(i).getSide() != vonSide) {
				return null;
			}
		}
		return vonSide;
	}
	
}
