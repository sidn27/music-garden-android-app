package com.musicgarden.android.ws;

public class CreateRoomMessage extends UserMessage {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
