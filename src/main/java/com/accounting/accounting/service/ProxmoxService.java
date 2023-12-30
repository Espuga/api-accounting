package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.ProxmoxModel;

@Service
public class ProxmoxService {
	@Autowired
	@Qualifier("jdbcproxmox")
	JdbcTemplate myProxmox;
	
	public Map<String, Object> getInit(String start, String end, double priceCpu, double priceDisk){
		return ProxmoxModel.getInit(myProxmox, start, end, priceCpu, priceDisk);
	}
	public Map<String, Object> getCpuUsage(String start, String end){
		return ProxmoxModel.getCpuUsage(myProxmox, start, end);
	}
}
