package com.accounting.accounting.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class VmachinesModel {

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
   * GET VMACHINES SELECTORS
   * @param jdbcAccounting
   * @return
   */
  public static Map<String, Object> getSelectors(JdbcTemplate jdbcAccounting) {
    Map<String, Object> result = new HashMap<>();

    try {
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

      Table priceTable = jdbcAccounting.query("SELECT name, value FROM price", (rs) -> {
        return Table.read().db(rs);
      });

      String priceCpu = priceTable.where(priceTable.stringColumn("name").isEqualTo("cpu")).get(0, 1).toString();
      String priceDisk = priceTable.where(priceTable.stringColumn("name").isEqualTo("disk")).get(0, 1).toString();

      result.put("priceCpu", priceCpu);
      result.put("priceDisk", priceDisk);


      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }
    return result;
  }

  private static Map<String, Object> getTableVlan(Table global, String vlan2, double priceCpu, double priceDisk) {
    Map<String, Object> result2 = new HashMap<>();
    
    Table vlan = global.where(global.stringColumn("vmid").startsWith(vlan2).and(global.stringColumn("vmid").lengthEquals(6)));
    
    List<Map<String, Object>> listVlan = new ArrayList<>();
                    
    Table aux = vlan.summarize("cpu", AggregateFunctions.median).by("hostname", "vmid");
    Table aux2 = vlan.summarize("cpus", AggregateFunctions.median).by("hostname", "vmid");
    Table aux3 = vlan.summarize("maxdisk", AggregateFunctions.median).by("hostname", "vmid");
    Table result = aux.joinOn("hostname", "vmid").inner(aux2, aux3);
    //System.out.println(result);
    
    Double prCpu = 0.0;
    Double prDisk = 0.0;

    for(Row row: result) {
      Map<String, Object> afegir = new HashMap<>();
      afegir.put("hostname", row.getObject("hostname"));
      afegir.put("vmid", row.getObject("vmid"));
      double valorCpu = Math.round(((Number) row.getObject("Median [cpu]")).doubleValue() * Math.pow(10, 2)) / Math.pow(10, 2);
      double valorCpus = Math.round(((Number) row.getObject("Median [cpus]")).doubleValue() * Math.pow(10, 2)) / Math.pow(10, 2);
      double valorMaxdisk = Math.round(((Number) row.getObject("Median [maxdisk]")).doubleValue() * Math.pow(10, 2)) / Math.pow(10, 2);
      BigDecimal cpuPrice = new BigDecimal(valorCpu*priceCpu).setScale(2, RoundingMode.HALF_UP);
      BigDecimal diskPrice = new BigDecimal(valorMaxdisk*priceDisk).setScale(2, RoundingMode.HALF_UP);
      
      afegir.put("cpu", valorCpu);
      afegir.put("cpus", valorCpus);
      afegir.put("maxdisk", Double.toString(valorMaxdisk)+" gb");
      afegir.put("cpuPrice", cpuPrice.toString()+"€");
      prCpu += Double.parseDouble(cpuPrice.toString());
      afegir.put("diskPrice", diskPrice.toString()+"€");
      prDisk += Double.parseDouble(diskPrice.toString());
      listVlan.add(afegir);
    }
    result2.put("listVlan", listVlan);
    
    // Double sumaritzatCpu = (Double) result.summarize("Median [cpu]", AggregateFunctions.sum).apply().get(0, 0);
    // sumaritzatCpu = Math.round(sumaritzatCpu * Math.pow(10, 2)) / Math.pow(10, 2);
    
    result2.put("sum", new BigDecimal(prCpu).setScale(2, RoundingMode.HALF_UP));
    
    // Double sumaritzatMaxdisk = (Double) result.summarize("Median [maxdisk]", AggregateFunctions.sum).apply().get(0, 0);
    // sumaritzatMaxdisk = Math.round(sumaritzatMaxdisk * Math.pow(10, 2)) / Math.pow(10, 2);
    
    result2.put("sumDisk", new BigDecimal(prDisk).setScale(2, RoundingMode.HALF_UP));
    
    
    return result2;
  }

  /**
   * GET GROUP's PROXMOX DATA
   * @param jdbcAccounting
   * @param jdbcProxmox
   * @param groupId
   * @param start
   * @param end
   * @return
   */
  public static Map<String, Object> getDataTable(JdbcTemplate jdbcAccounting, JdbcTemplate jdbcProxmox, String groupId, String start, String end) {
    Map<String, Object> result = new HashMap<>();

    try {
      // Get vlan (22, 23, 24)
      String vlan = jdbcAccounting.query(String.format("SELECT vlan FROM `groups` WHERE id = %s", groupId), (rs) -> {
        return Table.read().db(rs);
      }).getString(0, "vlan");


      Table cpus = jdbcProxmox.query(String.format("SELECT data, vmid, hostname, cpus FROM cpus WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
        return Table.read().db(rs);
      });
      Table cpu = jdbcProxmox.query(String.format("SELECT data, vmid, hostname, cpu FROM cpu WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
        return Table.read().db(rs);
      });
      Table maxdisk = jdbcProxmox.query(String.format("SELECT data, vmid, hostname, maxdisk FROM maxdisk WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
        return Table.read().db(rs);
      });
      
      Table global = cpus.joinOn("hostname", "data", "vmid").inner(cpu, maxdisk);


      // Get prices
      Table priceTable = jdbcAccounting.query("SELECT name, value FROM price", (rs) -> {
        return Table.read().db(rs);
      });
      String priceCpu = priceTable.where(priceTable.stringColumn("name").isEqualTo("cpu")).get(0, 1).toString();
      String priceDisk = priceTable.where(priceTable.stringColumn("name").isEqualTo("disk")).get(0, 1).toString();
      
      // Get group table
      Map<String, Object> aux = getTableVlan(global, vlan, Double.parseDouble(priceCpu), Double.parseDouble(priceDisk));
      result.put("dataTable", aux.get("listVlan"));
      result.put("sumCpu", aux.get("sum"));
      result.put("sumDisk", aux.get("sumDisk"));
      result.put("vlan", vlan);

      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }

    return result;
  }

  /**
   * TEACHER GET GROUPS DATA
   * @param jdbcAccounting
   * @param jdbcProxmox
   * @param groupsId
   * @param start
   * @param end
   * @return
   */
  public static Map<String, Object> getGroupsDataTable(JdbcTemplate jdbcAccounting, JdbcTemplate jdbcProxmox, String groupsId, String start, String end) {
    Map<String, Object> result = new HashMap<>();

    List<Map<String, Object>> aux = new ArrayList<>();
    try {
      for(String groupId : groupsId.split(",")) {
        aux.add(getDataTable(jdbcAccounting, jdbcProxmox, groupId, start, end));
      }
      result.put("dades", aux);
      result.put("ok", true);
    } catch (Exception e) {
      System.out.println(e);
      result.put("ok", false);
    }
    return result;
  }

  /**
   * UPDATE PRICES
   * @param jdbcAccounting
   * @param pricesModel
   * @return
   */
  public static boolean savePrices(JdbcTemplate jdbcAccounting, PricesModel pricesModel){
    try {
      jdbcAccounting.update("UPDATE price SET value = ? WHERE name = 'cpu'", pricesModel.getPriceCpu());
      jdbcAccounting.update("UPDATE price SET value = ? WHERE name = 'disk'", pricesModel.getPriceDisk());
      return true;
    } catch (Exception e){
      System.out.println(e);
      return false;
    }
  }

  /**
   * DO INVOICE FOR EVERY GROUP
   * @param jdbcAccounting
   * @param invoiceModel
   * @return
   */
  public static boolean doInvoice(JdbcTemplate jdbcAccounting, InvoiceModel invoiceModel) {
    try {
      // String data = LocalDate.now().toString();
      Integer i = 0;
        for(String vlan : invoiceModel.getVlans()) {
          String groupName = (String) jdbcAccounting.query(String.format("SELECT name FROM `groups` WHERE vlan = %s", vlan), (rs) -> {
              return Table.read().db(rs).get(0, 0);
          });
          
          jdbcAccounting.update(
              String.format(
                  "INSERT INTO %s (title, description, amount, data, userId, authorized, accepted) VALUES (?, ?, ?, ?, (SELECT id FROM users WHERE token = '%s'), 1, 1)", 
                  groupName, invoiceModel.getToken()),
              "VMachines",
              String.format("Proxmox VMachines. CPU: %S €, Disk: %s €", invoiceModel.getPrCpu()[i].toString(), invoiceModel.getPrDisk()[i].toString()),
              ((invoiceModel.getPrCpu()[i] + invoiceModel.getPrDisk()[i])*-1),
              // data
              invoiceModel.getData()
          );
          i++;
        }
        return true;
    } catch (Exception e ){ 
      System.out.println(e);
        return false;
    }
  }
  
  
}
