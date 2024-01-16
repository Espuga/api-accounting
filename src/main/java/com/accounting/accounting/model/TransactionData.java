package com.accounting.accounting.model;

import java.sql.Date;

public class TransactionData {
	private String title;
    private String description;
    private double amount;
    private Date date;
    private Integer groupId;
    private String token;
    
    public TransactionData(String title, String description, double amount, Date date, Integer groupId, String token) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.groupId = groupId;
        this.token = token;
    }
    
    public String getTitle() {
    	return this.title;
    }
    public String getDescription() {
    	return this.description;
    }
    public double getAmount() {
    	return this.amount;
    }
    public Date getDate() {
    	return this.date;
    }
    public Integer getGroupId() {
    	return this.groupId;
    }
    public String getToken() {
    	return this.token;
    }
}
