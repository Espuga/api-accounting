package com.accounting.accounting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class AuthModel {

	public static Map<String, Object> login(JdbcTemplate jdbcAccounting, String username, String password) {
		Map<String, Object> result = new HashMap<>();
		try {
			Table data = jdbcAccounting.query(String.format("SELECT token FROM users WHERE username = '%s' AND password = '%s' ", username, password), (rs) -> {
				return Table.read().db(rs);
			});
			if(data.isEmpty()) {
				result.put("token", signin(jdbcAccounting, username, password));
			}else {
				result.put("token", data.get(0, 0).toString());
			}
			
			// Rights
			Table rights = jdbcAccounting.query(
					String.format("SELECT ur.group_id, ur.right_id FROM users_rights ur WHERE ur.user_id = (SELECT id FROM users WHERE username = '%s')", 
							username), (rs) -> {
				return Table.read().db(rs);
			});
			List<Map<String, Object>> rightsReturn = new ArrayList<>();
			for(Row row : rights) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("group_id", row.getInt("group_id"));
				aux.put("right_id", row.getInt("right_id"));
				rightsReturn.add(aux);
			}
			result.put("rights", rightsReturn);
			result.put("ok", true);
		} catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		return result;
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
			jdbcAccounting.update(
					"INSERT INTO users (username, password, token) VALUES ( ?, ?, ?)",
					username,
					password,
					token
					);
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