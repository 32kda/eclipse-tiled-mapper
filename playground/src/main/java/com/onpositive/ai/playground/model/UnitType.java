package com.onpositive.ai.playground.model;

public enum UnitType {
	
	WARRIOR(100,10,3),
	ARCHER(80,7,3,5),
	KNIGHT(150,30,2),
	ORC(80,15,4);
	
	public final int health;
	public final int attack;
	public final int range;
	public final int attackRange;
	
	private UnitType(int health, int attack, int range, int attackRange) {
		this.health = health;
		this.attack = attack;
		this.range = range;
		this.attackRange = attackRange;
	}
	
	private UnitType(int health, int attack, int range) {
		this(health, attack, range, 1);
	}
	
}
