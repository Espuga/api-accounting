package com.accounting.accounting.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class DashboardModel {
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
		Table data = jdbcAccounting.query(String.format("SELECT * FROM %s", groupName), (rs) -> {
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
		
		// Get data
		String query = "SELECT id, title, description, amount, data FROM %s ORDER BY data";
		Table data = jdbcAccounting.query(String.format(query, groupName), (rs) -> {
			return Table.read().db(rs);
		});
		
		List<String> dates = new ArrayList<>();				// xAxis data
		List<Double> addedData = new ArrayList<>();			// Added Founds
		List<Double> withdrawedData = new ArrayList<>();	// Withdrawed Founds
		List<Double> totalData = new ArrayList<>();			// Total Founds
		
		if(!data.isEmpty()) {
			// Get first monday data
			LocalDate firstMonday = (LocalDate) data.get(0, 4);	// Get first day
			while(firstMonday.getDayOfWeek().getValue() != 1) {	// Get first monday after first day
				firstMonday = firstMonday.minusDays(1);
			}
			LocalDate lastDayWeek = firstMonday.plusDays(6);	// Get last day of the week
			
			LocalDate currentDate = LocalDate.now();	// Current date
			
			while(firstMonday.isBefore(currentDate)) {	// For every week
				Table current = data.where(data.dateColumn("data").isBetweenIncluding(firstMonday, lastDayWeek));

				dates.add(firstMonday.toString());	// Add data
				totalData.add(current.doubleColumn("amount").sum());	// Add Total Founds
				Table added = current.where(current.doubleColumn("amount").isGreaterThan(0));
				Table withdrawed = current.where(current.doubleColumn("amount").isLessThan(0));
				addedData.add(added.doubleColumn("amount").sum());		// Add Added Founds
				withdrawedData.add(Math.abs(withdrawed.doubleColumn("amount").sum()));	// Add Withdrawed Founds
				
				
				firstMonday = firstMonday.plusDays(7);	// Next monday
				lastDayWeek = lastDayWeek.plusDays(7);	// Next day of Week
			}
		}
		
		
		result.put("dates", dates);
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
			
			String insertQuery = String.format("INSERT INTO %s (title, description, amount, data) values (?, ?, ?, ?)", groupName);
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
			String query = "SELECT g.id, g.name FROM users u "
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
	
	public static boolean createGroup(JdbcTemplate jdbcAccounting, CreateGroupData groupData) {
		
		// Create new Table
		String newQuery = "CREATE TABLE %s (id int not null AUTO_INCREMENT, title VARCHAR(50), description VARCHAR(200), amount DOUBLE, "
				+ "data DATE, PRIMARY KEY(id))";
		jdbcAccounting.update(String.format(newQuery, groupData.getName()));
		
		// Add group in groups table
		jdbcAccounting.update(
				"INSERT INTO `groups` (name, admin_id) VALUES ( ?, (SELECT u.id FROM users u WHERE u.token = ? ))", 
				groupData.getName(),
				groupData.getToken()
				);
		
		// Add users to table
		jdbcAccounting.update(
				"INSERT INTO `users_groups` (user_id, group_id) VALUES "
				+ "( (SELECT u.id FROM users u WHERE u.token = ?), "
				+ "(SELECT g.id FROM `groups` g WHERE g.name = ?))", 
				groupData.getToken(),
				groupData.getName()
				);
		
		return true;
	}
	
	public static boolean changeMembers(JdbcTemplate jdbcAccounting, String token, Integer groupId, String users2) {
		String[] users = users2.split(",");
		for(String user : users) {
			user = user.strip();
			String id = jdbcAccounting.query(String.format("SELECT id FROM users WHERE username = '%s'", user), (rs) -> {
				return Table.read().db(rs).get(0, 0).toString();
			});
			if(!id.isEmpty()) {
				jdbcAccounting.update("INSERT INTO users_groups (user_id, group_id) VALUES (?, ?)", id, groupId);
			}
		}
		return true;
	}
}
