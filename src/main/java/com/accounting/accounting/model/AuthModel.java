package com.accounting.accounting.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class AuthModel {

	public static String encodePassword(String password) {
    try {
      // Crear una instancia de MessageDigest con el algoritmo SHA-256
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // Obtener el hash de la contraseña
      byte[] encodedHash = digest.digest(password.getBytes());

      // Convertir el hash en una cadena hexadecimal
      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedHash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

	public static Map<String, Object> login(JdbcTemplate jdbcAccounting, String username, String password) {
		Map<String, Object> result = new HashMap<>();
		try {
			Table data = jdbcAccounting.query(String.format("SELECT token, name, password FROM users WHERE username = '%s' ", username), (rs) -> {
				return Table.read().db(rs);
			});

			if(data.get(0, 2).toString().equals(encodePassword(password))) {
				result.put("token", data.get(0, 0).toString());
			}else {
				result.put("token", "");
			}

			/* if(data.isEmpty()) {
				result.put("token", "");
			}else {
				result.put("token", data.get(0, 0).toString());
			} */
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
      result.put("msg", e);
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
        				encodePassword(newAccountData.getPassword()),
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