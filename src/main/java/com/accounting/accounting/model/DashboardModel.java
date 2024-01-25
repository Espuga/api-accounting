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
	
	/**
	 * CREATE NEW GROUP
	 * @param jdbcAccounting
	 * @param groupData
	 * @return
	 */
	public static Map<String, Object> createGroup(JdbcTemplate jdbcAccounting, CreateGroupData groupData) {
		Map<String, Object> result = new HashMap<>();
		
		// Create new Table
		String newQuery = "CREATE TABLE %s (id int not null AUTO_INCREMENT, title VARCHAR(50), description VARCHAR(200), amount DOUBLE, "
				+ "data DATE, userId INTEGER, PRIMARY KEY(id), foreign key(userId) references users(id))";
		jdbcAccounting.update(String.format(newQuery, groupData.getName()));
		
		// Add group in groups table
		jdbcAccounting.update(
				"INSERT INTO `groups` (name, admin_id) VALUES ( ?, (SELECT u.id FROM users u WHERE u.token = ? ))", 
				groupData.getName(),
				groupData.getToken()
				);
		
		// Add user to table
		jdbcAccounting.update(
				"INSERT INTO `users_groups` (user_id, group_id) VALUES "
				+ "( (SELECT u.id FROM users u WHERE u.token = ?), "
				+ "(SELECT g.id FROM `groups` g WHERE g.name = ?))", 
				groupData.getToken(),
				groupData.getName()
				);
		
		// Add starting amount
		String insertQuery = String.format("INSERT INTO %s (title, description, amount, data, userId) "
				+ "values (?, ?, ?, ?, (SELECT id FROM users where token = '%s'))", groupData.getName(), groupData.getToken());
		jdbcAccounting.update(
		            insertQuery,
		            "Starting Amount",
		            "",
		            groupData.getAmount(),
		            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
		        );
		jdbcAccounting.update(String.format("UPDATE %s SET id = 0 WHERE id = 1", groupData.getName()));
		
		// Add rights to admin user
		jdbcAccounting.update(
				String.format(
						"INSERT INTO users_rights (user_id, group_id, right_id) VALUES "
						+ "((SELECT id FROM users where token = '%s'), (SELECT g.id FROM `groups` g WHERE g.name = ?), 1)",
							groupData.getToken()),
					groupData.getName()
				);
		
		// Add rest of users
		String[] users = groupData.getUsers().strip().split(",");
		for(String user : users) {
			jdbcAccounting.update(
					"INSERT INTO users_groups (user_id, group_id) VALUES "
					+ "((SELECT id FROM users WHERE username = ?), "
					+ "(SELECT g.id FROM `groups` g WHERE g.name = ?)) ",
					user,
					groupData.getName()
					);
		}
		
		// Rights
		Table rights = jdbcAccounting.query(
				String.format("SELECT ur.group_id, ur.right_id FROM users_rights ur WHERE ur.user_id = (SELECT id FROM users WHERE token = '%s')", 
						groupData.getToken()), (rs) -> {
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
		return result;
	}
	
	/**
	 * ADD MEMBERS TO GROUP
	 * @param jdbcAccounting
	 * @param token
	 * @param groupId
	 * @param users2
	 * @return
	 */
	public static boolean changeMembers(JdbcTemplate jdbcAccounting, String token, Integer groupId, String users2) {
		String[] users = users2.strip().split(",");
		for(String user : users) {
			jdbcAccounting.update(
					"INSERT INTO users_groups (user_id, group_id) VALUES "
					+ "((SELECT id FROM users WHERE username = ?), "
					+ "?) ",
					user,
					groupId
					);
		}
		
		return true;
	}
	
	/**
	 * GET GROUP USERS
	 * @param jdbcAccounting
	 * @param groupId
	 * @return
	 */
	public static Map<String, Object> getUsers(JdbcTemplate jdbcAccounting, Integer groupId) {
		Map<String, Object> result = new HashMap<>();
		result.put("ok", true);
		
		String query = "SELECT u.id, u.username, u.name FROM users_groups ug JOIN users u ON (u.id = ug.user_id) WHERE ug.group_id = %s";
		
		Table data = jdbcAccounting.query(String.format(query, groupId), (rs) -> {
			return Table.read().db(rs);
		});
		Table rights = jdbcAccounting.query(String.format("SELECT user_id, right_id FROM users_rights WHERE group_id = %d", groupId), (rs) -> {
			return Table.read().db(rs);
		});
		
		List<Map<String, Object>> users = new ArrayList<>();
		for(Row row : data) {
			Map<String, Object> aux = new HashMap<>();
			aux.put("id", row.getInt("id"));
			aux.put("username", row.getString("username"));
			aux.put("name", row.getString("name"));
			Table permisos = rights.where(rights.intColumn("user_id").isEqualTo(row.getInt("id")));
			List<Integer> p = new ArrayList<>();
			for(Row row2 : permisos) {
				p.add(row2.getInt("right_id"));
			}
			aux.put("rights", p);
			users.add(aux);
		}
		result.put("users", users);
		
		return result;
	}
	
	/**
	 * QUIT USER FROM GROUP
	 * @param jdbcAccounting
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public static boolean quitUsers(JdbcTemplate jdbcAccounting, String groupId, String userId) {
		try {
			jdbcAccounting.update(
				"DELETE FROM users_groups WHERE user_id = ? AND group_id = ? ",
				userId,
				groupId
				);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
	}
	
	/**
	 * DELETE GROUP
	 * @param jdbcAccounting
	 * @param groupId
	 * @return
	 */
	public static boolean deleteGroup(JdbcTemplate jdbcAccounting, String groupId) {
		try {
			jdbcAccounting.update(
					"DELETE FROM users_groups WHERE group_id = ?",
					groupId
					);
			jdbcAccounting.update("DELETE FROM users_rights WHERE group_id = ?", groupId);
			String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %s", groupId), (rs) -> {
				return Table.read().db(rs).get(0, 0);
			});
			jdbcAccounting.update(
					String.format("DROP TABLE %s", groupName)
					);
			jdbcAccounting.update(
					"DELETE FROM `groups` WHERE id = ?",
					groupId
					);
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * GET SPRINTS + DATES
	 * @param jdbcAccounting
	 * @return
	 */
	public static Map<String, Object> getSprints(JdbcTemplate jdbcAccounting) {
		Map<String, Object> result = new HashMap<>();
		
		Table data = jdbcAccounting.query("SELECT name, data FROM sprints", (rs) -> {
			return Table.read().db(rs);
		});
		
		List<Map<String, Object>> sprintsList = new ArrayList<>();
		for(Row row : data) {
			Map<String, Object> aux = new HashMap<>();
			aux.put("name", row.getString("name"));
			aux.put("date", row.getDate("data"));
			sprintsList.add(aux);
		}
		result.put("sprints", sprintsList);
		result.put("ok", true);
		return result;
	}
	
	/**
	 * UPDATE SPRINTS PERIODS
	 * @param jdbcAccounting
	 * @param sprintsData
	 * @return
	 */
	public static boolean updateSprints(JdbcTemplate jdbcAccounting, SprintsData[] sprintsData) {
		try {
			for(SprintsData data : sprintsData) {
				LocalDate dia = LocalDate.parse(data.getDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				jdbcAccounting.update(
						"UPDATE sprints SET data = ? WHERE name = ?",
						dia,
						data.getName()
						);
			}
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * GET ALL RIGHTS TO LIST
	 * @param jdbcAccounting
	 * @return
	 */
	public static Map<String, Object> getRights(JdbcTemplate jdbcAccounting) {
		Map<String, Object> result = new HashMap<>();

		try {
			Table data = jdbcAccounting.query("SELECT id, name, description FROM rights WHERE id <> 2", (rs) -> {
				return Table.read().db(rs);
			});
			List<Map<String, Object>> rights = new ArrayList<>();

			for(Row row : data) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("id", Integer.toString(row.getInt("id")));
				aux.put("name", row.getString("name"));
				aux.put("description", row.getString("description"));
				rights.add(aux);
			}
			result.put("rights", rights);
			result.put("ok", true);
		} catch(Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}

		return result;
	}

	/**
	 * SAVE USER RIGHTS
	 * @param jdbcAccounting
	 * @param rightsData
	 * @return
	 */
	public static boolean saveRights(JdbcTemplate jdbcAccounting, RightsData rightsData) {
		try {
			Table rights = jdbcAccounting.query(String.format("SELECT right_id FROM users_rights WHERE user_id = %d AND group_id = %d", rightsData.getUserId(), rightsData.getGroupId()), (rs) -> {
				return Table.read().db(rs);
			});
			for(Integer rightId : rightsData.getRightsId()) {
				if(rights.where(rights.intColumn("right_id").isEqualTo(rightId)).isEmpty()) {
					jdbcAccounting.update(
						"INSERT INTO users_rights (user_id, group_id, right_id) VALUES (?, ?, ?)", 
						rightsData.getUserId(),
						rightsData.getGroupId(),
						rightId);
				}
			}
			List<Integer> rightsList = new ArrayList<>(Arrays.asList(rightsData.getRightsId()));
			for(Row row : rights) {
				if (!rightsList.contains(row.getInt("right_id"))) {
					jdbcAccounting.update(
						"DELETE FROM users_rights WHERE user_id = ? AND group_id = ? AND right_id = ?",
						rightsData.getUserId(),
						rightsData.getGroupId(),
						row.getInt("right_id")
						);
				}
			}
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
