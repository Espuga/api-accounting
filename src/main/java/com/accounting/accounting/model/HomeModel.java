package com.accounting.accounting.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class HomeModel { 
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
   * GET THE AMOUNT OF A GROUP
   * @param jdbcAccounting
   * @param groupId
   * @return
   */
	public static Map<String, Object> getHome(JdbcTemplate jdbcAccounting, Integer groupId) {
		Map<String, Object> result = new HashMap<>();
		
		try {
      if(!groupId.equals(0)){
        String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", groupId), (rs) -> {
          return Table.read().db(rs).get(0, 0);
        });
        Table data = jdbcAccounting.query(String.format("SELECT SUM(amount) FROM %s WHERE accepted = 1 AND authorized = 1", groupName), (rs) -> {
          return Table.read().db(rs);
        });
        result.put("amount",data.get(0, 0));
        result.put("ok", true);
      }
		} catch (Exception e) {
			System.out.println(e);
			result.put("ok", false);
		}
		
		return result;
	}

  /**
   * GET HOME n OF MACHINES
   * @param jdbcAccounting
   * @param jdbcProxmox
   * @param groupId
   * @return
   */
  public static Map<String, Object> getNMachines(JdbcTemplate jdbcAccounting, JdbcTemplate jdbcProxmox, String groupId) {
    Map<String, Object> result = new HashMap<>();
    
    try {
      String vlan = jdbcAccounting.query(String.format("SELECT vlan FROM `groups` WHERE id = '%s'", groupId), (rs) -> {
        return Table.read().db(rs).get(0, 0).toString();
      });
      Table sprintsTable = jdbcAccounting.query("SELECT name, data FROM sprints" , (rs) -> {
        return Table.read().db(rs);
      });

      for(Row row : sprintsTable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
          // Convertimos la cadena de fecha a un objeto LocalDate
          LocalDate end = LocalDate.parse(findLastDay(sprintsTable, row.getString("name")), formatter);
        if(LocalDate.now().isAfter(row.getDate("data")) && 
            LocalDate.now().isBefore(end) || 
            LocalDate.now().equals(row.getDate("data")) || 
            LocalDate.now().equals(end)) {
          String query = String.format("SELECT COUNT(vmid) FROM cpu WHERE vmid LIKE '%S%%' AND data >= '%s' AND data <= '%s' ", vlan, row.getDate("data").toString(), end.toString());
          Table a = jdbcProxmox.query(query, (rs) -> {
            return Table.read().db(rs);
          });
          String num = a.get(0, 0).toString();
          result.put("num", num);
          result.put("ok", true);
          return result;
        }
      }
    } catch(Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }
    
    return result;
  }


  public static void provaCron(JdbcTemplate myAccounting, JdbcTemplate myProxmox) {
    Table sprints = myAccounting.query("SELECT name, data FROM sprints", (rs) -> {
      return Table.read().db(rs);
    });
    LocalDate ara = LocalDate.now();
    
    for(int i = 0; i < sprints.rowCount()-1; i++) {
      LocalDate first = LocalDate.parse(sprints.get(i, 1).toString());
      LocalDate last = LocalDate.parse(sprints.get(i+1, 1).toString()).minusDays(1);
      // Si es aquest sprint
      if(ara.isAfter(first) && ara.isBefore(last)){
        // Si es l'ultim divendres
        if(ara.isEqual(first.plusDays(1))) {

          Table users = myAccounting.query("SELECT u.id, u.course, ug.group_id FROM users u JOIN users_groups ug ON (u.id = ug.user_id)", (rs) -> {
            return Table.read().db(rs);
          });

          Double priceFirstCourse = myAccounting.query("SELECT value FROM price WHERE name = 'firstCourse'", (rs) -> {
            return Double.parseDouble(Table.read().db(rs).get(0, 0).toString());
          });
          Double priceSecondCourse = myAccounting.query("SELECT value FROM price WHERE name = 'secondCourse'", (rs) -> {
            return Double.parseDouble(Table.read().db(rs).get(0, 0).toString());
          });

          Table data = myAccounting.query(
            String.format("SELECT ur.group_id, ur.user_id, SUM(ur.hours) as hours  FROM users_projects ur  JOIN users u ON (ur.user_id = u.id) JOIN projects p ON (p.id = ur.project_id) "
              + "WHERE p.data BETWEEN '%s' AND '%s' GROUP BY ur.user_id", first.toString(), last.toString()), (rs) -> {
            return Table.read().db(rs);
          });

          // System.out.println(data);

          List<Integer> groups = users.intColumn("group_id").unique().asList();

          for(Integer group : groups) {
            // System.out.println("Group: "+group.toString());
            Double money = 0.0;
            Table usersGroup = users.where(users.intColumn("group_id").isEqualTo(group));
            // System.out.println(usersGroup);
            for(Row user : usersGroup) {
              // System.out.println("User: "+user.getInt("id"));
              Table aux = data.where(data.intColumn("user_id").isEqualTo(user.getInt("id")).and(data.intColumn("group_id").isEqualTo(group)));
              // System.out.println(aux);
              if(!aux.isEmpty()){
                Double hours = Double.parseDouble(aux.get(0, 2).toString());
                // System.out.println(hours);
                money += hours*((user.getInt("course") == 1) ? priceFirstCourse : priceSecondCourse);
              }

            }

            // System.out.println("Group Money: "+money.toString());
            String groupName = (String) myAccounting.query(String.format("SELECT name FROM `groups` WHERE id = %d", group), (rs) -> {
              return Table.read().db(rs).get(0, 0);
            });
            myAccounting.update(
              String.format("INSERT INTO %s (title, description, amount, data, userId, authorized, accepted) VALUES (?, ?, ?, ?, ?, ?, ?)", groupName),
              "Salary",
              "",
              money*-1,
              ara.toString(),
              0,
              1, 
              1
            );
          }

        }
      }
    }
    
  }
}
