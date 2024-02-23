package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.CreateGroupData;
import com.accounting.accounting.model.DashboardModel;
import com.accounting.accounting.model.RightsData;
import com.accounting.accounting.model.SaveVlanData;
import com.accounting.accounting.model.SettingsModel;
import com.accounting.accounting.model.SprintsData;

@Service
public class SettingsService {
   	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
    
	
	/**
	 * CREATE NEW GROUP
	 * @param groupData
	 * @return
	 */
	public Map<String, Object> createGroup(CreateGroupData groupData) {
		return SettingsModel.createGroup(myAccounting, groupData);
	}

    /**
	 * ADD MEMBERS TO GROUP
	 * @param token
	 * @param groupId
	 * @param users
	 * @return
	 */
	public boolean changeMembers(String token, Integer groupId, String users) {
		return SettingsModel.changeMembers(myAccounting, token, groupId, users);
	}
	
	/**
	 * QUIT USER FROM GROUP
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public boolean quitUsers(String groupId, String userId) {
		return SettingsModel.quitUsers(myAccounting, groupId, userId);
	}
	
	/**
	 * GET GROUP USERS
	 * @param groupId
	 * @return
	 */
	public Map<String, Object> getUsers(Integer groupId) {
		return SettingsModel.getUsers(myAccounting, groupId);
	}
	
	/**
	 * DELETE GROUP
	 * @param groupId
	 * @return
	 */
	public boolean deleteGroup(String groupId) {
		return SettingsModel.deleteGroup(myAccounting, groupId);
	}
	
	/**
	 * GET SPRINTS + DATES
	 * @return
	 */
	public Map<String, Object> getSprints() {
		return SettingsModel.getSprints(myAccounting);
	}
	
	/**
	 * UPDATE SPRINTS PERIODS
	 * @param sprintsData
	 * @return
	 */
	public boolean updateSprints(SprintsData[] sprintsData) {
		return SettingsModel.updateSprints(myAccounting, sprintsData);
	}

	/**
	 * GET ALL RIGHTS TO LIST
	 * @return
	 */
	public Map<String, Object> getRights() {
		return SettingsModel.getRights(myAccounting);
	}

	/**
	 * SAVE USER RIGHTS
	 * @param rightsData
	 * @return
	 */
	public boolean saveRights(RightsData rightsData) {
		return SettingsModel.saveRights(myAccounting, rightsData);
	}
	
	/**
	 * GET TELEGRAM CHAT ID
	 * @return
	 */
	public Map<String, Object> getChartId() {
		return SettingsModel.getChartId(myAccounting);
	}
	
	/**
	 * SAVE TELEGRAM CHAT ID
	 * @param chatId
	 * @return
	 */
	public boolean saveChatId(String chatId) {
		return SettingsModel.saveChatId(myAccounting, chatId);
	}

  /**
   * GET THE VLAN OF THE GROUP
   * @param id
   * @return
   */
  public Map<String, Object> getVlan(Integer id) {
    return SettingsModel.getVlan(myAccounting, id);
  }

  /**
   * UPDATE GROUP VLAN
   * @param saveVlanData
   * @return
   */
  public boolean saveVlan(SaveVlanData saveVlanData) {
		return SettingsModel.saveVlan(myAccounting, saveVlanData);
	}
}  
