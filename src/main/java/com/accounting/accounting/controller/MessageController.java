package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.SendMessageRequest;
import com.accounting.accounting.model.TelegramMessageSender;
import com.accounting.accounting.service.MessageService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/telegram")
public class MessageController {
  @Autowired
  MessageService messageService;

  @PostMapping("/send-message")
  public boolean sendMessage(@RequestBody SendMessageRequest request) {
    // return telegramMessageSender.sendMessage("719358840", request.getMessage()  );
    return messageService.sendMessage(request.getMessage(), request.getToken());
  }

  @GetMapping("/getMessages")
  public Map<String, Object> getMessages() {
    return messageService.getMessages();
  }
}