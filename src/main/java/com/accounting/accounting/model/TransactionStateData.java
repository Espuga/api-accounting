package com.accounting.accounting.model;

public class TransactionStateData {
	private String groupId;
	private String id;
	
	public TransactionStateData(String groupId, String id) {
		this.groupId = groupId;
		this.id = id;
	}
	
	public String getGroupId() {
		return this.groupId;
	}
	public String getId() {
		return this.id;
	}
}
