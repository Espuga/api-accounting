package com.accounting.accounting.model;

public class SalaryData {
  private Double salary1;
  private Double salary2;

  public SalaryData(Double salary1, Double salary2) {
    this.salary1 = salary1;
    this.salary2 = salary2;
  }

  public Double getSalary1() {
    return this.salary1;
  }
  public Double getSalary2() {
    return this.salary2;
  }
}
