package com.accounting.accounting.model;

public class CreateGroupData {
	private String token;
	private String name;
	private String users;
	private long amount;
	private String vlan;
	
	public CreateGroupData(String token, String name, String users, long amount, String vlan) {
		this.token = token;
		this.name = name;
		this.users = users;
		this.amount = amount;
		this.vlan = vlan;
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
	public String getVlan() {
		return this.vlan;
	}
}
