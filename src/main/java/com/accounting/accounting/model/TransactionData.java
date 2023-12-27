package com.accounting.accounting.model;

import java.sql.Date;

public class TransactionData {
	private String title;
    private String description;
    private double amount;
    private Date date;
    
    public TransactionData(String title, String description, double amount, Date date) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.date = date;
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
}
