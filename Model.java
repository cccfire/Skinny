package com.mineprom.skinny;

public enum Model {

	DEFAULT("steve"),
	SLIM("slim"),
	STEVE("steve"),
	ALEX("slim");

	private final String name;

	Model(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
