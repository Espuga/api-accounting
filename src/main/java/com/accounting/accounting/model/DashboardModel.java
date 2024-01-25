package com.accounting.accounting.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class DashboardModel {
	
	public static Map<String, Object> getHome(JdbcTemplate jdbcAccounting, Integer groupId) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", groupId), (rs) -> {
				return Table.read().db(rs).get(0, 0);
			});
			Table data = jdbcAccounting.query(String.format("SELECT SUM(amount) FROM %s", groupName), (rs) -> {
				return Table.read().db(rs);
			});
			result.put("amount",data.get(0, 0));
			result.put("ok", true);
		} catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		
		return result;
	}
	
	/**
	 * RETURN TABLE DATA
	 * @param jdbcAccounting
	 * @param groupId
	 * @return
	 */
	public static List<Map<String, Object>> getDataTable(JdbcTemplate jdbcAccounting, Integer groupId) {
		List<Map<String, Object>> dataReturn = new ArrayList<>();
		// Get group name
		String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", groupId), (rs) -> {
			return Table.read().db(rs).get(0, 0);
		});
		// Get data
		Table data = jdbcAccounting.query(String.format(
				"SELECT t.id, t.title, t.description, t.amount, t.data, u.name FROM %s t JOIN users u ON (u.id = t.userId) ORDER BY data ", groupName ), (rs) -> {
			return Table.read().db(rs);
		});
		
		// Change data format to return
		if(!data.isEmpty()) {
			for(Row row : data) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("id", row.getInt("id"));
				aux.put("title", row.getString("title"));
				aux.put("description", row.getString("description"));
				aux.put("amount", row.getDouble("amount"));
				aux.put("data", row.getDate("data").toString());
				aux.put("name", row.getString("name"));
				dataReturn.add(aux);
			}
		}
		return dataReturn;
	}
	
	/**
	 * RETURN CHART DATA
	 * @param jdbcAccounting
	 * @param groupId
	 * @return
	 */
	public static Map<String, Object> getDataChart(JdbcTemplate jdbcAccounting, Integer groupId) {
		Map<String, Object> result = new HashMap<>();
		// Get group name
		String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", groupId), (rs) -> {
			return Table.read().db(rs).get(0, 0);
		});
		
		Table sprints = jdbcAccounting.query("SELECT name, data FROM sprints", (rs) -> {
			return Table.read().db(rs);
		});
		
		// Get data
		String query = "SELECT id, title, description, amount, data FROM %s ORDER BY data";
		Table data = jdbcAccounting.query(String.format(query, groupName), (rs) -> {
			return Table.read().db(rs);
		});
		
		List<String> dates = new ArrayList<>();				// xAxis data
		List<Double> addedData = new ArrayList<>();			// Added Founds
		List<Double> withdrawedData = new ArrayList<>();	// Withdrawed Founds
		List<Double> totalData = new ArrayList<>();	// Global Founds
		
		if(!data.isEmpty()) {
			
			LocalDate currentDate = LocalDate.now();	// Current date
			
			List<LocalDate> sprintsDate = new ArrayList<>();
			boolean aux = false;
			for(Row row : sprints) {
				if(row.getDate("data").isBefore(currentDate) || row.getDate("data").isEqual(currentDate) || aux == true) {
					sprintsDate.add(row.getDate("data"));
					if(aux == true) {
						break;
					}
				}else {
					aux = true;
				}
			}
			
			for(int i = 1; i < sprintsDate.size(); i++) {
				Table current = data.where(
					data.dateColumn("data").isBetweenIncluding(sprintsDate.get(i-1), sprintsDate.get(i).minusDays(1))
					.and(data.intColumn("id").isNotEqualTo(0))
					);
				Table current2 = data.where(
					data.dateColumn("data").isBetweenIncluding(sprintsDate.get(0), sprintsDate.get(i).minusDays(1)));
				// System.out.println(current);
				dates.add(sprints.where(sprints.dateColumn("data").isEqualTo(sprintsDate.get(i-1))).getString(0, "name"));
				if(current.isEmpty()) {
					addedData.add(0.0);		// Add Added Founds
					withdrawedData.add(0.0);
				}else {
					Table added = current.where(current.doubleColumn("amount").isGreaterThan(0));
					Table withdrawed = current.where(current.doubleColumn("amount").isLessThan(0));
					addedData.add(added.doubleColumn("amount").sum());		// Add Added Founds
					withdrawedData.add(Math.abs(withdrawed.doubleColumn("amount").sum()));	// Add Withdrawed Founds	
				}
				if(current2.isEmpty()){
					totalData.add(0.0);
				}else {
					totalData.add(current2.doubleColumn("amount").sum());
				}
			}
		}
		
		result.put("dates", dates);
		// result.put("totalData", getTotal2(data));
		result.put("totalData", totalData);
		result.put("addedData", addedData);
		result.put("withdrawedData", withdrawedData);
		
		return result;
	}
	
	/**
	 * RETURN TABLE & CHART DATA
	 * @param jdbcAccounting
	 * @param groupId
	 * @return
	 */
	public static Map<String, Object> getInit(JdbcTemplate jdbcAccounting, Integer groupId) {
		Map<String, Object> result = new HashMap<>();
		result.put("ok", true);
		
		result.put("dataTable", getDataTable(jdbcAccounting, groupId));	// Get table data
		result.put("dataChart", getDataChart(jdbcAccounting, groupId));	// Get chart data
		
		return result;
	}
	
	/**
	 * NEW TRANSACTION
	 * @param jdbcAccounting
	 * @param transactionData
	 * @return
	 */
	public static Map<String, Object> setTransaction(JdbcTemplate jdbcAccounting, TransactionData transactionData) {
		Map<String, Object> result = new HashMap<>();
		try {
			// Get group name
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", transactionData.getGroupId()), (rs) -> {
				return Table.read().db(rs).get(0, 0);
			});
			
			
			String insertQuery = String.format("INSERT INTO %s (title, description, amount, data, userId) "
					+ "values (?, ?, ?, ?, (SELECT id FROM users where token = '%s'))", groupName, transactionData.getToken());
			jdbcAccounting.update(
			            insertQuery,
			            transactionData.getTitle(),
			            transactionData.getDescription(),
			            transactionData.getAmount(),
			            transactionData.getDate()
			        );
			result.put("ok", true);
			result.put("dataTable", getDataTable(jdbcAccounting, 1));
		}catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		return result;
	}
	
	/**
	 * RETURN GROUPS LIST
	 * @param jdbcAccounting
	 * @param token
	 * @return
	 */
	public static Map<String, Object> getGroups(JdbcTemplate jdbcAccounting, String token) {
		Map<String, Object> result = new HashMap<>();
		try {
			// Get groups list
			String query = "SELECT g.id, g.name, g.admin_id FROM users u "
					+ "JOIN users_groups ug ON (ug.user_id = u.id) "
					+ "JOIN `groups` g ON (ug.group_id = g.id) "
					+ "WHERE u.token='%s' ";
			Table data = jdbcAccounting.query(String.format(query, token), (rs) -> {
				return Table.read().db(rs);
			});
			
			// Change groups format
			List<Map<String, Object>> groups = new ArrayList<>();
			for(Row row : data) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("name", row.getString("name"));
				aux.put("id", row.getInt("id"));
				aux.put("admin_id", row.getInt("admin_id"));
				groups.add(aux);
			}
			
			result.put("ok", true);
			result.put("groups", groups);
		}catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		return result;
	}
}
