package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.TelegramMessageSender;

@Service
public class MessageService {
  @Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;

  private final TelegramMessageSender telegramMessageSender;

  public MessageService(TelegramMessageSender telegramMessageSender) {
    this.telegramMessageSender = telegramMessageSender;
  }
	
  /**
   * SEND TELEGRAM MESSAGE
   * @param message
   * @param token
   * @return
   */
	public boolean sendMessage(String message, String token) {
		return telegramMessageSender.sendMessage(myAccounting, message, token);
	}

  /**
   * GET THE TELEGRAM HISTORY OF MESSAGES
   * @return
   */
  public Map<String, Object> getMessages() {
    return telegramMessageSender.getMessages(myAccounting);
  }
}
