package com.accounting.accounting.model;

public class UpdateTransactionData {
  private Integer id;
  private String title;
  private String description;
  private Double amount;
  private String data;
  private Integer groupId;

  public UpdateTransactionData(Integer id, String title, String description, Double amount, String data, Integer groupId) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.amount = amount;
    this.data = data;
    this.groupId = groupId;
  }

  public Integer getId() {
    return this.id;
  }
  public String getTitle() {
    return this.title;
  }
  public String getDescription() {
    return this.description;
  }
  public Double getAmount() {
    return this.amount;
  }
  public String getData() {
    return this.data;
  }
  public Integer getGroupId() {
    return this.groupId;
  }
}
