package com.accounting.accounting.model;

public class SprintsData {
	private String name;
	private String date;
	
	// CONSTRUCTOR
	public SprintsData(String name, String date) {
		this.name = name;
		this.date = date;
	}
	
	// GETTERS
	public String getName() {
		return this.name;
	}
	public String getDate() {
		return this.date;
	}
}
