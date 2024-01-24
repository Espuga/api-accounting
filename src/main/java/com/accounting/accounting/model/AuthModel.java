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
			Table data = jdbcAccounting.query(String.format("SELECT token, name FROM users WHERE username = '%s' AND password = '%s' ", username, password), (rs) -> {
				return Table.read().db(rs);
			});
			if(data.isEmpty()) {
				result.put("token", "");
			}else {
				result.put("token", data.get(0, 0).toString());
			}
			result.put("name", data.get(0, 1).toString());
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
	
	
	public static Map<String, Object> createACcount(JdbcTemplate jdbcAccounting, NewAccountData newAccountData) {
		Map<String, Object> result = new HashMap<>();
		
		String token = "";
		String characters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789@#$%&";
		
		Random random = new Random();
        for(int i = 0; i < 32; i++) {
        	token += characters.charAt(random.nextInt(characters.length()));
        }
        
        Table data = jdbcAccounting.query(String.format("SELECT token FROM users WHERE token = '%s'", token), (rs) -> {
        	return Table.read().db(rs);
        });
        
        while(!data.isEmpty()) {
        	// Do not repet token
        	token = "";
        	for(int i = 0; i < 32; i++) {
            	token += characters.charAt(random.nextInt(characters.length()));
            }
        	data = jdbcAccounting.query(String.format("SELECT token FROM users WHERE token = '%s'", token), (rs) -> {
            	return Table.read().db(rs);
            });
        }
		
        if(data.isEmpty()) {
        	data = jdbcAccounting.query(String.format("SELECT username FROM users WHERE username = '%s'", newAccountData.getUsername()), (rs) -> {
        		return Table.read().db(rs);
        	});
        	if(data.isEmpty()) {
        		// if the username is unique
        		jdbcAccounting.update(
        				"INSERT INTO users (username, name, password, token) VALUES (?, ?, ?, ?)",
        				newAccountData.getUsername(),
        				newAccountData.getName(),
        				newAccountData.getPassword(),
        				token
        				);
        		
        		result.put("token", token);
        		result.put("user", newAccountData.getName());
        	}else {
        		// username not unique
        		result.put("token", "");
        		result.put("msg", "This username is alredy used!");
        	}
        }
        
		
		return result;
	}
	
	public static String getUsername(JdbcTemplate jdbcAccounting, String token) {
		Table data = jdbcAccounting.query(String.format("SELECT name FROM users WHERE token = '%s'", token), (rs) -> {
			return Table.read().db(rs);
		});
		if(data.isEmpty()) {
			return "";
		}
		return data.getString(0, 0);
	}
}