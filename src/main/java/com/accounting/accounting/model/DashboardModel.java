package com.accounting.accounting.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.icu.util.LocaleMatcher.Result;

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
		Table data = jdbcAccounting.query(String.format(
				"SELECT t.id, t.title, t.description, t.amount, t.data, u.id as userId, u.name "
				+ "FROM %s t "
				+ "JOIN users u ON (u.id = t.userId) "
				+ "WHERE t.authorized = 1 AND t.accepted = 1 "
				+ "ORDER BY data ", groupName ), (rs) -> {
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
				aux.put("id", row.getInt("id"));
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
		String query = "SELECT id, title, description, amount, data "
				+ "FROM %s "
				+ "WHERE authorized = 1 AND accepted = 1 "
				+ "ORDER BY data";
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
					totalData.add(new BigDecimal(current2.doubleColumn("amount").sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
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
		if(!groupId.equals(0)){
      try {
        result.put("dataTable", getDataTable(jdbcAccounting, groupId));	// Get table data
        result.put("dataChart", getDataChart(jdbcAccounting, groupId));	// Get chart data
        result.put("ok", true);
      } catch (Exception e) {
        System.out.println(e);
        result.put("ok", false);
      }
    }
		
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
			
			
			String insertQuery = String.format("INSERT INTO %s (title, description, amount, data, userId, authorized, accepted) "
					+ "values (?, ?, ?, ?, (SELECT id FROM users where token = '%s'), ?, ?)", groupName, transactionData.getToken());
			jdbcAccounting.update(
			            insertQuery,
			            transactionData.getTitle(),
			            transactionData.getDescription(),
			            transactionData.getAmount(),
			            transactionData.getDate(),
                  (transactionData.getAmount()>0 ? "0" : "1" ),
                  (transactionData.getAmount()>0 ? "0" : "1" )
			        );
			// result.put("dataTable", getDataTable(jdbcAccounting, transactionData.getGroupId()));
			result.put("ok", true);
		}catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
      result.put("msg", e);
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

      Table right = jdbcAccounting.query(
        String.format("SELECT group_id, right_id FROM users_rights WHERE user_id = (SELECT id FROM users WHERE token = '%s') AND group_id = 0 AND right_id = 2", token), (rs) -> {
        return Table.read().db(rs);
      });

      String query = "";

      if(right.isEmpty()){
        query = "SELECT g.id, g.name FROM users u "
					+ "JOIN users_groups ug ON (ug.user_id = u.id) "
					+ "JOIN `groups` g ON (ug.group_id = g.id) "
					+ "WHERE u.token='%s' ";
      } else {
        query = "SELECT g.id, g.name FROM `groups` g "
          + "WHERE g.id <> 0";
      }
			// Get groups list
			
			Table data = jdbcAccounting.query(String.format(query, token), (rs) -> {
				return Table.read().db(rs);
			});

			
			// Change groups format
			List<Map<String, Object>> groups = new ArrayList<>();
			for(Row row : data) {
				Map<String, Object> aux = new HashMap<>();
				aux.put("name", row.getString("name"));
				aux.put("id", row.getInt("id"));
				// aux.put("admin_id", row.getInt("admin_id"));
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


  private static String findLastDay(Table sprintsTable, String sprint) {
    String[] nom = sprintsTable.where(sprintsTable.stringColumn("name").isEqualTo(sprint)).getString(0, "name").split(" ");
    String num = nom[nom.length - 1];

    try {
        String seguent = sprintsTable.where(sprintsTable.stringColumn("name").isEqualTo("Sprint "+(Integer.parseInt(num)+1))).get(0, 1).toString();
        LocalDate data = LocalDate.parse(seguent).minusDays(1);

        return data.toString();
    } catch (Exception e) {
        return "";
    }
  }

  /**
   * GET SPRINTS + SPRINTS PERIODS
   * @param jdbcAccounting
   * @return
   */
  public static Map<String, Object> getSprints(JdbcTemplate jdbcAccounting) {
    Map<String, Object> result = new HashMap<>();

    List<Map<String, Object>> sprints = new ArrayList<>();
    
    Table sprintsTable = jdbcAccounting.query("SELECT name, data FROM sprints" , (rs) -> {
        return Table.read().db(rs);
    });


    for(Row row : sprintsTable) {
        Map<String, Object> aux = new HashMap<>();
        aux.put("name", row.getString("name"));
        aux.put("start", row.getDate("data"));
        aux.put("end", findLastDay(sprintsTable, row.getString("name")));
        sprints.add(aux);
    }

    result.put("sprints", sprints);
    result.put("ok", true);

    return result;
  }

  /**
   * UPDATE TRANSACTION INFO
   * @param jdbcAccounting
   * @param updateTransactionData
   * @return
   */
  public static boolean updateTransaction(JdbcTemplate jdbcAccounting, UpdateTransactionData updateTransactionData) {
    try {
      String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", updateTransactionData.getGroupId()), (rs) -> {
				return Table.read().db(rs).get(0, 0);
			});


      jdbcAccounting.update(
        String.format("UPDATE %s SET title = ?, description = ?, amount = ?, data = ? WHERE id = ? ", groupName), 
        updateTransactionData.getTitle(),
        updateTransactionData.getDescription(),
        updateTransactionData.getAmount(),
        updateTransactionData.getData(),
        updateTransactionData.getId()
      ); 
      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }


  /**
   * DELETE TRANSACTION
   * @param jdbcAccounting
   * @param groupId
   * @param id
   * @return
   */
  public static boolean deleteTransaction(JdbcTemplate jdbcAccounting, String groupId, String id) {
    try  {
      String query = String.format("SELECT name FROM `groups` WHERE id = %d", Integer.parseInt(groupId));
      String groupName = (String) jdbcAccounting.query(query, (rs) -> {
				return Table.read().db(rs).get(0, 0);
			});

      jdbcAccounting.update(String.format("DELETE FROM %s WHERE id = ?", groupName), id);

      return true;
    } catch(Exception e) {
      System.out.println(e);
      return false;
    }
  }
}
