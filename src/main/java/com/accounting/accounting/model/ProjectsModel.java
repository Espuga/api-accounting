package com.accounting.accounting.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class ProjectsModel {
  
  /**
   * GET GROUPS PROJECTS
   * @param jdbcAccounting
   * @param id
   * @return
   */
  public static Map<String, Object> getProjects(JdbcTemplate jdbcAccounting, String id, String start, String end) {
    Map<String, Object> result = new HashMap<>();

    try {
      Table data = jdbcAccounting.query(
        String.format("SELECT id, title, description, data FROM projects "
        + "WHERE group_id = %s AND data BETWEEN '%s' AND '%s'", id, start, end), (rs) -> {
        return Table.read().db(rs);
      });

      List<Map<String,Object>> projectsList = new ArrayList<>();
      List<Map<String,Object>> users = new ArrayList<>();
      for(Row project : data) {
        Table hours = jdbcAccounting.query(
          String.format("SELECT u.username, u.name, up.hours FROM users_projects up JOIN users u ON (up.user_id = u.id) WHERE group_id = %s AND project_id = %d", id, project.getInt("id")), (rs) -> {
          return Table.read().db(rs);
        });

        Map<String, Object> aux = new HashMap<>();
        aux.put("id", project.getInt("id"));
        aux.put("title", project.getString("title"));
        aux.put("description", project.getString("description"));
        aux.put("data", project.getDate("data").toString());
        for(Row hour : hours) {
          aux.put(hour.getString("username"), new BigDecimal(hour.getDouble("hours")).setScale(2, RoundingMode.HALF_UP).doubleValue());
          Map<String, Object> userAux = new HashMap<>();
          userAux.put("name", hour.getString("name"));
          userAux.put("username", hour.getString("username"));
          if(!users.contains(userAux)){
            users.add(userAux);
          }
        }
        projectsList.add(aux);
      }
      result.put("projects", projectsList);
      result.put("users", users);
      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }

    return result;
  }

  /**
   * GET USERS CHART
   * @param jdbcAccounting
   * @param id
   * @param start
   * @param end
   * @return
   */
  public static Map<String, Object> getChart(JdbcTemplate jdbcAccounting, String id, String start, String end) {
    Map<String, Object> result = new HashMap<>();

    try {
      Table data = jdbcAccounting.query(
        String.format("SELECT ur.user_id, u.username, u.name, SUM(ur.hours) as hours  FROM users_projects ur  JOIN users u ON (ur.user_id = u.id) JOIN projects p ON (p.id = ur.project_id) WHERE ur.group_id = %s "
          + "AND p.data BETWEEN '%s' AND '%s' GROUP BY ur.user_id, u.username, u.name", id, start, end), (rs) -> {
        return Table.read().db(rs);
      });

      List<Double> hours = new ArrayList<>();
      List<String> users = new ArrayList<>();
      List<Integer> usersId = new ArrayList<>();
      for(Row user : data) {
        hours.add(user.getDouble("hours"));
        usersId.add(user.getInt("user_id"));
        users.add(user.getString("name"));
      }
      result.put("hours", hours);
      result.put("users", users);
      result.put("usersId", usersId);
      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }

    return result;
  }

  /**
   * CREATE NEW PROJECT + ADD USERS HOURS
   * @param jdbcAccounting
   * @param projectData
   * @return
   */
  public static boolean newProject(JdbcTemplate jdbcAccounting, ProjectData projectData) {
    try {
      // Add project
      jdbcAccounting.update(
        "INSERT INTO projects (title, description, group_id, data) VALUES (?, ?, ?, ?)",
        projectData.getTitle(),
        projectData.getDescription(),
        projectData.getGroupId(),
        projectData.getData()
      );

      Integer projectId = jdbcAccounting.query(
        String.format("SELECT id FROM projects WHERE title = '%s' AND description = '%s' AND group_id = %d", 
          projectData.getTitle(),
          projectData.getDescription(),
          projectData.getGroupId()
          ), (rs) -> {
            return Integer.parseInt(Table.read().db(rs).get(0, 0).toString());
          }
        );

      // Add hours
      List<Integer> keys = projectData.getUsersHours().keySet().stream()
        .filter(key -> key instanceof Integer)
        .map(Integer.class::cast)
        .collect(Collectors.toList());
      
      for(Integer i : keys) {
        // System.out.println(projectData.getUsersHours().get(i).get("hours"));
        jdbcAccounting.update(
          "INSERT INTO users_projects (group_id, user_id, project_id, hours) VALUES (?, ? ,?, ?)",
          projectData.getGroupId(),
          i,
          projectId,
          (projectData.getUsersHours().get(i).get("hours")+(projectData.getUsersHours().get(i).get("minutes")/60))
        );
      }
      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  /**
   * GET THE USER HOURS OF THE SPRINT BY PROJECTS
   * @param jdbcAccounting
   * @param groupId
   * @param userId
   * @param start
   * @param end
   * @return
   */
  public static Map<String, Object> getUserInfo(JdbcTemplate jdbcAccounting, Integer groupId, Integer userId, String start, String end) {
    Map<String, Object> result = new HashMap<>();

    try {
      Table data = jdbcAccounting.query(String.format(
        "SELECT p.title, up.hours "
        + "FROM projects p "
        + "JOIN users_projects up ON (up.project_id = p.id) "
        + "WHERE p.data BETWEEN '%s' AND '%s' AND up.group_id = %d AND up.user_id = %d"
        , start, end, groupId, userId), (rs) -> {
          return Table.read().db(rs);
        });
      List<Map<String, Object>> userProjects = new ArrayList<>();
      for(Row row : data) {
        Map<String, Object> aux = new HashMap<>();
        aux.put("title", row.getString("title"));
        aux.put("hours", row.getDouble("hours"));
        userProjects.add(aux);
      }
      result.put("userProjects", userProjects);

      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }

    return result;
  }

  /**
   * GET THE PROJECT INFO
   * @param jdbcAccounting
   * @param projectId
   * @return
   */
  public static Map<String, Object> getProject(JdbcTemplate jdbcAccounting, Integer projectId) {
    Map<String, Object> result = new HashMap<>();

    try {
      // Project info
      Table projectData = jdbcAccounting.query(
        String.format("SELECT title, description, data FROM projects WHERE id = %d", projectId), (rs) -> {
        return Table.read().db(rs);
      });
      for(Row row : projectData) {
        result.put("title", row.getString("title"));
        result.put("description", row.getString("description"));
        result.put("data", row.getDate("data").toString());
      }
      // Users info
      Table data = jdbcAccounting.query(
        String.format("SELECT u.id, u.username, u.name, up.hours FROM users_projects up JOIN users u ON (u.id = up.user_id) WHERE up.project_id = %d", projectId), (rs) -> {
        return Table.read().db(rs);
      });
      List<Map<String, Object>> usersList = new ArrayList<>();
      for(Row user : data) {
        // System.out.println(user);
        Integer hours = Integer.parseInt(new BigDecimal(user.getDouble("hours")).setScale(0, RoundingMode.HALF_DOWN).toString());
        // System.out.println(hours);
        BigDecimal decimals = new BigDecimal(user.getDouble("hours")).setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(hours));
        Integer minutes = (decimals.toString().length() == 1) ? Integer.parseInt(decimals.toString()+"0") : Integer.parseInt(decimals.toString().substring(decimals.toString().length() - 2));
        minutes = minutes*60/100;
        // System.out.println(minutes);
        Map<String, Object> aux = new HashMap<>();
        aux.put("hours", hours);
        aux.put("minutes", minutes);
        aux.put("id", user.getInt("id"));
        aux.put("username", user.getString("username"));
        aux.put("name", user.getString("name"));
        usersList.add(aux);
      }
      result.put("userList", usersList);
      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }

    return result;
  }

  /**
   * SAVE THE PROJECTS UPDATES
   * @param jdbcAccounting
   * @param projectData
   * @return
   */
  public static boolean saveProject(JdbcTemplate jdbcAccounting, ProjectData projectData) {
    try {
      // project info
      jdbcAccounting.update(
        String.format("UPDATE projects SET title = ?, description = ?, data = ? WHERE id = %d", projectData.getGroupId()), 
        projectData.getTitle(), projectData.getDescription(), projectData.getData());

      // user info
      List<Integer> keys = projectData.getUsersHours().keySet().stream()
        .filter(key -> key instanceof Integer)
        .map(Integer.class::cast)
        .collect(Collectors.toList());
      
      for(Integer i : keys) {
        // System.out.println(projectData.getUsersHours().get(i).get("hours"));
        jdbcAccounting.update(
          // "INSERT INTO users_projects (group_id, user_id, project_id, hours) VALUES (?, ? ,?, ?)",
          String.format("UPDATE users_projects SET hours = ? WHERE project_id = %s AND user_id = %d", projectData.getGroupId(), i),
          (projectData.getUsersHours().get(i).get("hours")+(projectData.getUsersHours().get(i).get("minutes")/60))
        );
      }

      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  /**
   * DELETE THE PROJECT
   * @param jdbcAccounting
   * @param id
   * @return
   */
  public static boolean deleteProject(JdbcTemplate jdbcAccounting, String id) {
    try {
      jdbcAccounting.update("DELETE FROM users_projects WHERE project_id = ?", id);
      jdbcAccounting.update("DELETE FROM projects WHERE id = ?", id);
      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }


}
