package com.accounting.accounting.model;


public class NewAccountData {
	private String name;
	private String username;
	private String password;
    
	public NewAccountData(String name, String username, String password) {
		this.name = name;
		this.username = username;
		this.password = password;
	}
    
    
    public String getName() {
    	return this.name;
    }
    public String getUsername() {
    	return this.username;
    }
    public String getPassword() {
    	return this.password;
    }
}
