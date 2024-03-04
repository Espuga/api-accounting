package com.accounting.accounting.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

@Component
@EnableScheduling
public class SchreduledModel {
  @Autowired
  @Qualifier("jdbcaccounting")
  JdbcTemplate myAccounting;
  @Autowired
  @Qualifier("jdbcproxmox")
  JdbcTemplate myProxmox;

  @Scheduled(cron = "0 0 8 * * ?")
  public void executeDailyTask() {
    List<Map<String, Object>> listToAuthorize = new ArrayList<>();
    Table groups = myAccounting.query("SELECT id, name FROM `groups` WHERE id <> 0", (rs) -> {
      return Table.read().db(rs);
    });
    
    for(Row group : groups) {
      Table data = myAccounting.query(
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
    if(!listToAuthorize.isEmpty()) {
      TelegramMessageSender.sendAlert(myAccounting, "There are transactions to authorize!");
    }

  }

  // Cada divendres, mirar la data si es a final d'sprint, cobrar
  // Cada Divendres a les 20:00
  @Scheduled(cron = "0 0 8 * * WEN")
  public void autoProxmoxInvoice() {
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
        if(ara.isEqual(last.minusDays(4))) {
          String groupsIds = myAccounting.queryForList("SELECT id FROM `groups` WHERE id <> 0").stream()
            .map(value -> value.get("id").toString())
            .collect(Collectors.joining(","));
          Map<String, Object> result = VmachinesModel.getGroupsDataTable(myAccounting, myProxmox, groupsIds, first.toString(), last.toString());

          ArrayList<Map<String, Object>> dades = (ArrayList<Map<String, Object>>) result.get("dades"); 

          ArrayList<String> vlans = new ArrayList<>();
          ArrayList<Double> prCpu = new ArrayList<>();
          ArrayList<Double> prDisk = new ArrayList<>();

          for(Map<String, Object> obj : dades) {
            vlans.add(obj.get("vlan").toString());
            prCpu.add(Double.parseDouble(obj.get("sumCpu").toString()));
            prDisk.add(Double.parseDouble(obj.get("sumDisk").toString()));
          }

          InvoiceModel invoiceModel2 = new InvoiceModel(vlans.toArray(new String[vlans.size()]), prCpu.toArray(new Double[prCpu.size()]), prDisk.toArray(new Double[prDisk.size()]), "Twt@IrmUB4zTfHUC3IYGlSJwZpSjY2Bi", ara.toString());

          VmachinesModel.doInvoice(myAccounting, invoiceModel2);

        }
      }
    }
  }

  @Scheduled(cron = "0 0 20 * * WEN")
  public void provaCron() {
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
        if(ara.isEqual(last.minusDays(4))) {

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
              0, 
              0
            );
          }

        }
      }
    }
    
  }

}
