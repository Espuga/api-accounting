package com.accounting.accounting.model;

import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Table;

public class AuthModel {

	public static String login(JdbcTemplate jdbcAccounting, String username, String password) {
		
		Table data = jdbcAccounting.query(String.format("SELECT token FROM users WHERE username = '%s' AND password = '%s' ", username, password), (rs) -> {
			return Table.read().db(rs);
		});
		if(data.isEmpty()) {
			return "";
		}
		return data.get(0, 0).toString();
	}
	
	public static String signin(JdbcTemplate jdbcAccounting, String username, String password) {
		try {
			String token = "";
			String characters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789@#$%&";
			
			Random random = new Random();
	        for(int i = 0; i < 32; i++) {
	        	token += characters.charAt(random.nextInt(characters.length()));
	        }
			//jdbcAccounting.update(String.format("UPDATE users SET token='%s'", token));
			jdbcAccounting.update(String.format("INSERT INTO users (username, password, token) VALUES ('%s', '%s', '%s')", username, password, token));
			return token;
		} catch( Exception e) {
			System.out.println(e);
			return "";
		}
	}
	
	public static String getUsername(JdbcTemplate jdbcAccounting, String token) {
		Table data = jdbcAccounting.query(String.format("SELECT username FROM users WHERE token = '%s'", token), (rs) -> {
			return Table.read().db(rs);
		});
		if(data.isEmpty()) {
			return "";
		}
		return data.getString(0, 0);
	}
}