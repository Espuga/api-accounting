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
  @Scheduled(cron = "0 0 20 * * FRI")
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
        if(ara.isEqual(last.minusDays(2))) {
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

}
