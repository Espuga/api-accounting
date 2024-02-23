package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.AuthModel;
import com.accounting.accounting.model.NewAccountData;
import com.accounting.accounting.model.SigninData;

@Service
public class AuthService {

	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
  /**
   * LOGIN
   * @param username
   * @param password
   * @return
   */
	public Map<String, Object> login(String username, String password) {
		return AuthModel.login(myAccounting, username, password);
	}
	
  /**
   * CREATE NEW USER's ACCOUNT
   * @param newAccountData
   * @return
   */
	public Map<String, Object> createACcount(NewAccountData newAccountData){
		return AuthModel.createACcount(myAccounting, newAccountData);
	}
	
  /**
   * GET THE USER NAME
   * @param token
   * @return
   */
	public String getUsername(String token) {
		return AuthModel.getUsername(myAccounting, token);
	}
}
