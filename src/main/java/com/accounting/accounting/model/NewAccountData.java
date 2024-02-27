package com.accounting.accounting.model;


public class NewAccountData {
	private String name;
	private String username;
	private String password;
	private Integer course;
    
	public NewAccountData(String name, String username, String password, Integer course) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.course = course;
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
		public Integer getCourse() {
			return this.course;
		}
}
