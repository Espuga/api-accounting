package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.accounting.accounting.model.AuthModel;
import com.accounting.accounting.model.CreateGroupData;
import com.accounting.accounting.model.DashboardModel;
import com.accounting.accounting.model.RightsData;
import com.accounting.accounting.model.SprintsData;
import com.accounting.accounting.model.TransactionData;

@Service
public class DashboardService {
	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
	public Map<String, Object> getHome(Integer groupId) {
		return DashboardModel.getHome(myAccounting, groupId);
	}
	
	/**
	 * RETURN TABLE & CHART DATA
	 * @param groupId
	 * @return
	 */
	public Map<String, Object> getInit(Integer groupId){
		return DashboardModel.getInit(myAccounting, groupId);
	}
	
	/**
	 * NEW TRANSACTION
	 * @param transactionData
	 * @return
	 */
	public Map<String, Object> setTransaction(TransactionData transactionData){
		return DashboardModel.setTransaction(myAccounting, transactionData);
	}
	
	/**
	 * RETURN GROUPS LIST
	 * @param token
	 * @return
	 */
	public Map<String, Object> getGroups(String token) {
		return DashboardModel.getGroups(myAccounting, token);
	}
}
