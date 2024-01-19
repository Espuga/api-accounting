package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.SigninData;
import com.accounting.accounting.service.AuthService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthService authService;
	
	@GetMapping("/login")
	public Map<String, Object> login(@RequestParam("username") String username, @RequestParam("password") String password) {
		return authService.login(username, password);
	}
	
	@GetMapping("/getUsername")
	public String getUsername(@RequestParam("token") String token) {
		return authService.getUsername(token);
	}
}
