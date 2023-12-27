package com.accounting.accounting.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class DashboardModel {
	
	public static List<Map<String, Object>> getDataTable(JdbcTemplate jdbcAccounting) {
		Table data = jdbcAccounting.query("SELECT * FROM nokia", (rs) -> {
			return Table.read().db(rs);
		});
		
		List<Map<String, Object>> dataReturn = new ArrayList<>();
		
		for(Row row : data) {
			Map<String, Object> aux = new HashMap<>();
			aux.put("id", row.getInt("id"));
			aux.put("title", row.getString("title"));
			aux.put("description", row.getString("description"));
			aux.put("amount", row.getDouble("amount"));
			aux.put("data", row.getDate("data").toString());
			dataReturn.add(aux);
		}
		return dataReturn;
	}
	
	public static Map<String, Object> getDataChart(JdbcTemplate jdbcAccounting) {
		Map<String, Object> result = new HashMap<>();
		// addedData []
		// withdrawedData []
		// totalData []
		
		String query = "SELECT id, title, description, amount, data FROM nokia ORDER BY data";
		
		Table data = jdbcAccounting.query(query, (rs) -> {
			return Table.read().db(rs);
		});
		
		LocalDate firstMonday = (LocalDate) data.get(0, 4);
		
		while(firstMonday.getDayOfWeek().getValue() != 1) {
			firstMonday = firstMonday.minusDays(1);
		}
		LocalDate lastDayWeek = firstMonday.plusDays(6);
		
		LocalDate currentDate = LocalDate.now();
		
		List<String> dates = new ArrayList<>();
		List<Double> addedData = new ArrayList<>();
		List<Double> withdrawedData = new ArrayList<>();
		List<Double> totalData = new ArrayList<>();
		
		while(firstMonday.isBefore(currentDate)) {
			Table current = data.where(data.dateColumn("data").isBetweenIncluding(firstMonday, lastDayWeek));
			if(!current.isEmpty()) {
				//System.out.println(current);
				dates.add(firstMonday.toString());
				totalData.add(current.doubleColumn("amount").sum());
				Table added = current.where(current.doubleColumn("amount").isGreaterThan(0));
				Table withdrawed = current.where(current.doubleColumn("amount").isLessThan(0));
				addedData.add(added.doubleColumn("amount").sum());
				withdrawedData.add(Math.abs(withdrawed.doubleColumn("amount").sum()));
				/*for(Row row : current) {
					if(row.getDouble("amount") > 0) {
						addedData.add(row.getDouble("amount"));
					}else {
						withdrawedData.add(row.getDouble("amount"));
					}
				}*/
			}
			firstMonday = firstMonday.plusDays(7);
			lastDayWeek = lastDayWeek.plusDays(7);
		}
		
		result.put("dates", dates);
		result.put("totalData", totalData);
		result.put("addedData", addedData);
		result.put("withdrawedData", withdrawedData);
		
		return result;
	}
	
	public static Map<String, Object> getInit(JdbcTemplate jdbcAccounting) {
		Map<String, Object> result = new HashMap<>();
		result.put("ok", true);
		
		result.put("dataTable", getDataTable(jdbcAccounting));
		result.put("dataChart", getDataChart(jdbcAccounting));
		
		return result;
	}
	
	public static Map<String, Object> setTransaction(JdbcTemplate jdbcAccounting, TransactionData transactionData) {
		Map<String, Object> result = new HashMap<>();
		try {
	        //String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String insertQuery = "INSERT INTO nokia (title, description, amount, data) values (?, ?, ?, ?)";
			 jdbcAccounting.update(
			            insertQuery,
			            transactionData.getTitle(),
			            transactionData.getDescription(),
			            transactionData.getAmount(),
			            transactionData.getDate()
			        );
			result.put("ok", true);
			result.put("dataTable", getDataTable(jdbcAccounting));
		}catch (Exception e) {
			result.put("ok", false);
		}
		return result;
	}
}
