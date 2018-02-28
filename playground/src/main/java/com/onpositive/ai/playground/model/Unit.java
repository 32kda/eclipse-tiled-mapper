package com.onpositive.ai.playground.model;

public class Unit {
	
	private UnitType type;
	private int health;
	private Position position;
	private Cell[][] field;
	private UnitSide side;
	private int reward;
	
	public Unit(UnitType type, UnitSide side, Position position, Cell[][] field) {
		this (type, side, position);
		this.field = field;
		setPosition(position);
	}
	
	public Unit(UnitType type, UnitSide side, Position position) {
		this.type = type;
		this.side = side;
		this.health = type.health;
		this.position = position;
	}

	public UnitType getType() {
		return type;
	}

	public void setType(UnitType type) {
		this.type = type;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void takeDamage(int damage) {
		this.health -= damage;
		if (health <= 0) {
			field[this.position.y][this.position.x].setUnit(null);
		}
	}
	
	public boolean isAlive() {
		return health > 0;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		if (!this.position.equals(position)) {
			if (this.position != null) {
				field[this.position.y][this.position.x].setUnit(null);
			}
			this.position = position;
			field[this.position.y][this.position.x].setUnit(this);
		}
	}

	public Cell[][] getField() {
		return field;
	}

	public void setField(Cell[][] field) {
		this.field = field;
		field[this.position.y][this.position.x].setUnit(this);
	}

	public UnitSide getSide() {
		return side;
	}

	public void setSide(UnitSide side) {
		this.side = side;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}
	
	public void addReward(int amount) {
		this.reward += amount;
	}

	@Override
	public String toString() {
		return type + "[health=" + health + ", position=" + position + ", side=" + side + ", reward="
				+ reward + "]";
	}

}
