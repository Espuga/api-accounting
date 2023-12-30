package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.DashboardModel;
import com.accounting.accounting.model.TransactionData;

@Service
public class DashboardService {
	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
	
	public Map<String, Object> getInit(){
		return DashboardModel.getInit(myAccounting);
	}
	
	public Map<String, Object> setTransaction(TransactionData transactionData){
		return DashboardModel.setTransaction(myAccounting, transactionData);
	}
}
