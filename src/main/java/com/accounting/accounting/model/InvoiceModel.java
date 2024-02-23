package com.accounting.accounting.model;

public class InvoiceModel {
  private String[] vlans;
  private Double[] prCpu;
  private Double[] prDisk;
  private String token;
  private String data;

  public InvoiceModel(String[] vlans, Double[] prCpu, Double[] prDisk, String token, String data) {
    this.vlans = vlans;
    this.prCpu = prCpu;
    this.prDisk = prDisk;
    this.token = token;
    this.data = data;
  }

  public String[] getVlans() {
    return this.vlans;
  }
  public Double[] getPrCpu() {
    return this.prCpu;
  }
  public Double[] getPrDisk() {
    return this.prDisk;
  }
  public String getToken() {
    return this.token;
  }
  public String getData() {
    return this.data;
  }
}
