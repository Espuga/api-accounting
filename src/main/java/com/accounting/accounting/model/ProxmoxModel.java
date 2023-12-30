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

public class ProxmoxModel {
	private static Map<String, Object> getTableVlan(Table global, String vlan2, double priceCpu, double priceDisk) {
    	Map<String, Object> result2 = new HashMap<>();
    	
    	Table vlan = global.where(global.stringColumn("vmid").startsWith(vlan2).and(global.stringColumn("vmid").lengthEquals(6)));
	    
	    List<Map<String, Object>> listVlan = new ArrayList<>();
	    		    		    
	    Table aux = vlan.summarize("cpu", AggregateFunctions.median).by("hostname", "vmid");
	    Table aux2 = vlan.summarize("cpus", AggregateFunctions.median).by("hostname", "vmid");
	    Table aux3 = vlan.summarize("maxdisk", AggregateFunctions.median).by("hostname", "vmid");
	    Table result = aux.joinOn("hostname", "vmid").inner(aux2, aux3);
	    //System.out.println(result);
	    
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
	    	afegir.put("diskPrice", diskPrice.toString()+"€");
	    	listVlan.add(afegir);
	    }
	    result2.put("listVlan", listVlan);
	    
	    Double sumaritzatCpu = (Double) result.summarize("Median [cpu]", AggregateFunctions.sum).apply().get(0, 0);
	    sumaritzatCpu = Math.round(sumaritzatCpu * Math.pow(10, 2)) / Math.pow(10, 2);
	    
	    result2.put("sum", sumaritzatCpu);
	    
	    Double sumaritzatMaxdisk = (Double) result.summarize("Median [maxdisk]", AggregateFunctions.sum).apply().get(0, 0);
	    sumaritzatMaxdisk = Math.round(sumaritzatMaxdisk * Math.pow(10, 2)) / Math.pow(10, 2);
	    
	    result2.put("sumDisk", sumaritzatMaxdisk);
	    
	    
	    return result2;
    }
	
    
	public static Map<String, Object> getInit(JdbcTemplate jdbc, String start, String end, double priceCpu, double priceDisk) {
		Map<String, Object> result = new HashMap<>();
		result.put("ok", true);
		
		try {
			if(!start.equals("") && !end.equals("")) {
				Table cpus = jdbc.query(String.format("SELECT data, vmid, hostname, cpus FROM cpus WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
			        return Table.read().db(rs);
			    });
			    Table cpu = jdbc.query(String.format("SELECT data, vmid, hostname, cpu FROM cpu WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
			        return Table.read().db(rs);
			    });
			    Table maxdisk = jdbc.query(String.format("SELECT data, vmid, hostname, maxdisk FROM maxdisk WHERE data >= '%s' AND data  <= '%s'", start, end), rs -> {
			        return Table.read().db(rs);
			    });
			    
			    Table global = cpus.joinOn("hostname", "data", "vmid").inner(cpu, maxdisk);
			    
			    Map<String, Object> vlan22 = getTableVlan(global, "22", priceCpu, priceDisk);
			    Map<String, Object> vlan23 = getTableVlan(global, "23", priceCpu, priceDisk);
			    Map<String, Object> vlan24 = getTableVlan(global, "24", priceCpu, priceDisk);
			    
			    result.put("tableVlan22", vlan22.get("listVlan"));
			    result.put("tableVlan23", vlan23.get("listVlan"));
			    result.put("tableVlan24", vlan24.get("listVlan"));
			    result.put("sumCpu22", vlan22.get("sum"));
			    result.put("sumCpu23", vlan23.get("sum"));
			    result.put("sumCpu24", vlan24.get("sum"));
			    result.put("sumDisk22", vlan22.get("sumDisk"));
			    result.put("sumDisk23", vlan23.get("sumDisk"));
			    result.put("sumDisk24", vlan24.get("sumDisk"));
			    
			}	
		    
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		
		return result;
	}
	public static List<String> getDaysBetweenDates(String startDate, String endDate) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate start = LocalDate.parse(startDate, formatter);
	    LocalDate end = LocalDate.parse(endDate, formatter);

	    // Llista de les dates
	    List<String> dateList = new ArrayList<>();

	    // afegir cada dia a la llista
	    while (!start.isAfter(end)) {
	        dateList.add(start.format(formatter));
	        start = start.plusDays(1); //Seguent dia
	    }

	    return dateList;
	}
	
	public static Map<String, Object> getCpuUsage(JdbcTemplate jdbc, String start, String end){
		Map<String, Object> result = new HashMap<>();
		result.put("ok", true);

		result.put("dies", getDaysBetweenDates(start, end));
		
		Table cpu = jdbc.query(String.format("SELECT data, vmid, hostname, cpu FROM cpu WHERE data >= '%s' AND data  < '%s'", start, end), rs -> {
	        return Table.read().db(rs);
	    });
		Table cpu2 = cpu.where(cpu.stringColumn("vmid").startsWith("23"));
		
		// Fer llistat de maquines, per cada maquina, fer una serie
		List<String> maquines = new ArrayList<>();
		maquines = cpu2.stringColumn("hostname").unique().asList();
		
		List<Object> cpuVlan22 = new ArrayList<>();
		
		maquines.forEach((maquina) -> {
			Map<String, Object> aux = new HashMap<>();
			List<Double> dadesMaquina = new ArrayList<>();
			Table maquinaAux = cpu2.where(cpu2.stringColumn("hostname").isEqualTo(maquina));	
			// fer bucle de primer dia fins a primer dia que hi ha registres
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    LocalDate start2 = LocalDate.parse(start, formatter);
		    LocalDate end2 = LocalDate.parse(end, formatter);

		    // afegir cada dia a la llista
		    while (!start2.isAfter(end2)) {
		    	dadesMaquina.add(null);
		        start2 = start2.plusDays(1); //Seguent dia
		    }
			
			for(Row row: maquinaAux) {
				// ha de afegir null fins el dia de creacio de la maquina
				
				dadesMaquina.add((Double) row.getObject("cpu"));
			}
			aux.put("nom", maquina);
			aux.put("valor", dadesMaquina);
			cpuVlan22.add(aux);
		});
		result.put("maquines", cpuVlan22);
		
		
		
		
		/*Map<String, Object> listMaquines = new HashMap<>();
		List<Object> result2 = new ArrayList<>();
		
		maquines.forEach((maquina) -> {
			List<Double> listMaquina = new ArrayList<>();
			Table aux = cpu2.where(cpu2.stringColumn("hostname").isEqualTo(maquina));
			for(Row row: aux) {
				listMaquina.add((Double) row.getObject("cpu"));
			}
			listMaquines.put("maquina", maquina);
			listMaquines.put("dades", listMaquina);
			result2.add(listMaquines);
		});
	    result.put("list", result2);*/
	    
	    // maquines : [{name: "maquina", valor: values}]
	    
		return result;
	}
}
