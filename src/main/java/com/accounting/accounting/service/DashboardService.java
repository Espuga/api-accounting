package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.DashboardModel;
import com.accounting.accounting.model.TransactionData;
import com.accounting.accounting.model.UpdateTransactionData;

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

  public Map<String, Object> getSprints() {
    return DashboardModel.getSprints(myAccounting);
  }

  public boolean updateTransaction(UpdateTransactionData updateTransactionData) {
    return DashboardModel.updateTransaction(myAccounting, updateTransactionData);
  }

  public boolean deleteTransaction(String groupId, String id) {
    return DashboardModel.deleteTransaction(myAccounting, groupId, id);
  }
}
