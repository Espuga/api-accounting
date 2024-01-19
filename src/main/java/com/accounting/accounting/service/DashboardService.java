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
import com.accounting.accounting.model.TransactionData;

@Service
public class DashboardService {
	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
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
	
	public boolean createGroup(CreateGroupData groupData) {
		return DashboardModel.createGroup(myAccounting, groupData);
	}
	
	public boolean changeMembers(String token, Integer groupId, String users) {
		return DashboardModel.changeMembers(myAccounting, token, groupId, users);
	}
	
	public Map<String, Object> getUsers(Integer groupId) {
		return DashboardModel.getUsers(myAccounting, groupId);
	}
	
	public boolean quitUsers(String groupId, String userId) {
		return DashboardModel.quitUsers(myAccounting, groupId, userId);
	}
	public boolean deleteGroup(String groupId) {
		return DashboardModel.deleteGroup(myAccounting, groupId);
	}
}
