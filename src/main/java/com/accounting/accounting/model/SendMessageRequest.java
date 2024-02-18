package com.accounting.accounting.model;

public class SendMessageRequest {
  private String message;
  private String token;

  public SendMessageRequest(String token, String message) {
    this.message = message;
    this.token = token;
  }

  public String getToken() {
    return this.token;
  }
  public String getMessage() {
    return this.message;
  }
}