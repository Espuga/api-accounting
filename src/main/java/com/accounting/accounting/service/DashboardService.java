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
	
	/**
	 * CREATE NEW GROUP
	 * @param groupData
	 * @return
	 */
	public Map<String, Object> createGroup(CreateGroupData groupData) {
		return DashboardModel.createGroup(myAccounting, groupData);
	}
	
	/**
	 * ADD MEMBERS TO GROUP
	 * @param token
	 * @param groupId
	 * @param users
	 * @return
	 */
	public boolean changeMembers(String token, Integer groupId, String users) {
		return DashboardModel.changeMembers(myAccounting, token, groupId, users);
	}
	
	/**
	 * GET GROUP USERS
	 * @param groupId
	 * @return
	 */
	public Map<String, Object> getUsers(Integer groupId) {
		return DashboardModel.getUsers(myAccounting, groupId);
	}
	
	/**
	 * QUIT USER FROM GROUP
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean quitUsers(String groupId, String userId) {
		return DashboardModel.quitUsers(myAccounting, groupId, userId);
	}
	
	/**
	 * DELETE GROUP
	 * @param groupId
	 * @return
	 */
	public boolean deleteGroup(String groupId) {
		return DashboardModel.deleteGroup(myAccounting, groupId);
	}
	
	/**
	 * GET SPRINTS + DATES
	 * @return
	 */
	public Map<String, Object> getSprints() {
		return DashboardModel.getSprints(myAccounting);
	}
	
	/**
	 * UPDATE SPRINTS PERIODS
	 * @param sprintsData
	 * @return
	 */
	public boolean updateSprints(SprintsData[] sprintsData) {
		return DashboardModel.updateSprints(myAccounting, sprintsData);
	}

	/**
	 * GET ALL RIGHTS TO LIST
	 * @return
	 */
	public Map<String, Object> getRights() {
		return DashboardModel.getRights(myAccounting);
	}

	/**
	 * SAVE USER RIGHTS
	 * @param rightsData
	 * @return
	 */
	public boolean saveRights(RightsData rightsData) {
		return DashboardModel.saveRights(myAccounting, rightsData);
	}
}
