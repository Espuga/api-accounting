package com.accounting.accounting.model;

public class PricesModel {
  // {priceCpu : priceCpu.value, priceDisk: priceDisk.value}
  private String priceCpu;
  private String priceDisk;

  public PricesModel(String priceCpu, String priceDisk) {
    this.priceCpu = priceCpu;
    this.priceDisk = priceDisk;
  }

  public String getPriceCpu() {
    return this.priceCpu;
  }
  public String getPriceDisk() {
    return this.priceDisk;
  }
}
