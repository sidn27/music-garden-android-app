package com.musicgarden.android.ws;

import com.musicgarden.android.models.User;

import java.util.List;

public class UserListMessage extends Message {

	private List<User> list;

	public List<User> getList() {
		return list;
	}

	public void setList(List<User> list) {
		this.list = list;
	}
}
