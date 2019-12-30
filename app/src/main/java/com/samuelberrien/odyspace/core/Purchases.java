package com.samuelberrien.odyspace.core;

/**
 * Created by samuel on 05/08/17.
 */

public enum Purchases {
	SHIP("Ship"),
	FIRE("Fire"),
	BONUS("Bonus"),
	ENHANCEMENT("Enhancement");

	private String name;

	Purchases(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
