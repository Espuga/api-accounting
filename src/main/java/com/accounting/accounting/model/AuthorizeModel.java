package com.accounting.accounting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class AuthorizeModel {

  /**
   * TEACHERS GET TRANSACTIONS TO AUTHORIZE
   * @param jdbcAccounting
   * @return
   */
	public static Map<String, Object> getToAuthorize(JdbcTemplate jdbcAccounting) {
		Map<String, Object> result = new HashMap<>();
		try {
			List<Map<String, Object>> listToAuthorize = new ArrayList<>();
			Table groups = jdbcAccounting.query("SELECT id, name FROM `groups` WHERE id <> 0", (rs) -> {
				return Table.read().db(rs);
			});
			
			for(Row group : groups) {
				Table data = jdbcAccounting.query(
						String.format(
								"SELECT t.id, t.title, t.description, t.amount, u.name, t.data "
								+ "FROM %s t "
								+ "JOIN users u ON (t.userId = u.id) "
								+ "WHERE t.authorized = 0", 
								group.getString("name")
								), (rs) -> {
					return Table.read().db(rs);
				});

				for(Row groupData : data) {
					Map<String, Object> aux = new HashMap<>();
					aux.put("id", groupData.getInt("id"));
					aux.put("title", groupData.getString("title"));
					aux.put("description", groupData.getString("description"));
					aux.put("amount", groupData.getDouble("amount"));
					aux.put("group", group.getString("name"));
					aux.put("groupId", group.getInt("id"));
					aux.put("user", groupData.getString("name"));
					aux.put("data", groupData.getDate("data"));
					listToAuthorize.add(aux);
				}
				
			}
			result.put("data", listToAuthorize);
			result.put("ok", true);
		} catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		return result;
	}
	
  /**
   * TEACHERS AUTHORIZE THE TRANSACTION
   * @param jdbcAccounting
   * @param transactionStateData
   * @return
   */
	public static boolean accpetTransaction(JdbcTemplate jdbcAccounting, TransactionStateData transactionStateData) {
		try {
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %s", transactionStateData.getGroupId()), (rs) -> {
	          return Table.read().db(rs).get(0, 0);
	        });
			
			jdbcAccounting.update(String.format("UPDATE %s SET authorized = 1 WHERE id = %s", groupName, transactionStateData.getId()));
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
  /**
   * TEACHERS UNAUTHORIZE THE TRANSACTION
   * @param jdbcAccounting
   * @param groupId
   * @param id
   * @return
   */
	public static boolean dropTransaction(JdbcTemplate jdbcAccounting, String groupId, String id) {
		try {
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %s", groupId), (rs) -> {
	          return Table.read().db(rs).get(0, 0);
	        });
			
			jdbcAccounting.update(String.format("DELETE FROM %s WHERE id = %s", groupName, id));
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
   * STUDENS GET THE TRANSACTIONS TO ACCEPT
   * @param jdbcAccounting
   * @param groupId
   * @return
   */
	public static Map<String, Object> getToAccept(JdbcTemplate jdbcAccounting, String groupId) {
		Map<String, Object> result = new HashMap<>();
		try {
			List<Map<String, Object>> listToAuthorize = new ArrayList<>();
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %s", groupId), (rs) -> {
		          return Table.read().db(rs).get(0, 0);
		        });
			Table data = jdbcAccounting.query(
					String.format(
							"SELECT t.id, t.title, t.description, t.amount, u.name, t.data "
							+ "FROM %s t "
							+ "JOIN users u ON (t.userId = u.id) "
							+ "WHERE t.authorized = 1 AND t.accepted = 0", 
							groupName
							), (rs) -> {
				return Table.read().db(rs);
			});

			for(Row groupData : data) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("id", groupData.getInt("id"));
				aux.put("title", groupData.getString("title"));
				aux.put("description", groupData.getString("description"));
				aux.put("amount", groupData.getDouble("amount"));
				aux.put("user", groupData.getString("name"));
				aux.put("data", groupData.getDate("data"));
				listToAuthorize.add(aux);
			}
			
			result.put("data", listToAuthorize);
			result.put("ok", true);
		} catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		return result;
	}
	
  /**
   * STUDENS ACCEPT THE TRANSACTION
   * @param jdbcAccounting
   * @param transactionStateData
   * @return
   */
	public static boolean accpetTheTransaction(JdbcTemplate jdbcAccounting, TransactionStateData transactionStateData) {
		try {
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %s", transactionStateData.getGroupId()), (rs) -> {
	          return Table.read().db(rs).get(0, 0);
	        });
			
			jdbcAccounting.update(String.format("UPDATE %s SET accepted = 1 WHERE id = %s", groupName, transactionStateData.getId()));
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
