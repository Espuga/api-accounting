package com.accounting.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.AuthModel;

@Service
public class AuthService {

	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
	public String login(String username, String password) {
		return AuthModel.login(myAccounting, username, password);
	}
	
	public String signin(String username, String password) {
		return AuthModel.signin(myAccounting, username, password);
	}
	
	public String getUsername(String token) {
		return AuthModel.getUsername(myAccounting, token);
	}
}
