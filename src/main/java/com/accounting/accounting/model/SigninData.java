package com.accounting.accounting.model;

public class SigninData {
	private String username;
	private String password;
	
	public SigninData(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	// Getters
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
}
