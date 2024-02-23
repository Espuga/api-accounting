package com.accounting.accounting.model;

import java.util.Map;

public class ProjectData {
  private String title;
  private String description;
  private Integer group_id;
  private String data;
  private Map<Integer, Map<String, Double>> users_hours;

  public ProjectData(String title, String description, Integer group_id, String data, Map<Integer, Map<String, Double>> users_hours) {
    this.title = title;
    this.description = description;
    this.group_id = group_id;
    this.data = data;
    this.users_hours = users_hours;
  }

  public String getTitle() {
    return this.title;
  }
  public String getDescription() {
    return this.description;
  }
  public Integer getGroupId() {
    return this.group_id;
  }
  public String getData() {
    return this.data;
  }
  public Map<Integer, Map<String, Double>> getUsersHours() {
    return this.users_hours;
  }
}
