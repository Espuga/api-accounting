package com.accounting.accounting.model;

public class CreateGroupData {
	private String token;
	private String name;
	private String users;
	private long amount;
	
	public CreateGroupData(String token, String name, String users, long amount) {
		this.token = token;
		this.name = name;
		this.users = users;
		this.amount = amount;
	}
	
	// Getters
	public String getToken() {
		return this.token;
	}
	public String getName() {
		return this.name;
	}
	public String getUsers() {
		return this.users;
	}
	public long getAmount() {
		return this.amount;
	}
}
