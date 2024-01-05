package com.accounting.accounting.model;

public class CreateGroupData {
	private String token;
	private String name;
	
	public CreateGroupData(String token, String name) {
		this.token = token;
		this.name = name;
	}
	
	// Getters
	public String getToken() {
		return this.token;
	}
	public String getName() {
		return this.name;
	}
}
