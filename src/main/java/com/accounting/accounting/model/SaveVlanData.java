package com.accounting.accounting.model;

public class SaveVlanData {
  private String groupId;
  private String vlan;

  public SaveVlanData(String groupId, String vlan) {
    this.groupId = groupId;
    this.vlan = vlan;
  }

  public String getGroupId() {
    return this.groupId;
  }
  public String getVlan() {
    return this.vlan;
  }
}
