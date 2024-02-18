package com.accounting.accounting.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

@Component
public class TelegramMessageSender {
  private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
  // private static final String BOT_TOKEN = "6878232403:AAE-sJrQfVQ-rxmTH1_58uHWfqqWPvk1ATw";
  private static final String BOT_TOKEN = "";

  // public boolean sendMessage(String chatId, String message) {
  public boolean sendMessage(JdbcTemplate jdbcaccounting, String message, String token) {
    String chatId = jdbcaccounting.query("SELECT value FROM settings WHERE `key` = 'chatId'", (rs) -> {
      return Table.read().db(rs).getString(0, 0);
    });

      RestTemplate restTemplate = new RestTemplate();

      String url = TELEGRAM_API_URL + BOT_TOKEN + "/sendMessage";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      String requestBody = "{\"chat_id\": \"" + chatId + "\", \"text\": \"" + message + "\"}";

      HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        LocalDate ara = LocalDate.now(); 
        jdbcaccounting.update(
          "INSERT INTO messages (message, userId, date) VALUES (?, (SELECT id FROM users WHERE token = ?), ? )",
          message,
          token,
          ara
          );

        return true;
      } else {
        return false;
      }
  }

  public static Map<String, Object> getMessages(JdbcTemplate jdbcAccounting) {
    Map<String, Object> result = new HashMap<>();

    try {
      Table messages = jdbcAccounting.query("SELECT m.id, m.message, u.name, m.date FROM messages m JOIN users u ON (u.id = m.userId)", (rs) -> {
        return Table.read().db(rs);
      });

      List<Map<String, Object>> messagesSent = new ArrayList<>();
      for(Row row : messages) {
        Map<String, Object> aux = new HashMap<>();
        aux.put("id", row.getInt("id"));
        aux.put("message", row.getString("message"));
        aux.put("name", row.getString("name"));
        aux.put("date", row.getDate("date"));
        messagesSent.add(aux);
      }
      
      result.put("messages", messagesSent);
      result.put("ok", true);
    }catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }
    return result;
  }
}
