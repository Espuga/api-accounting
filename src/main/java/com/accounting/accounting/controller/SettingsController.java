package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.CreateGroupData;
import com.accounting.accounting.model.RightsData;
import com.accounting.accounting.model.SprintsData;
import com.accounting.accounting.service.DashboardService;
import com.accounting.accounting.service.SettingsService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/settings")
public class SettingsController {
    @Autowired
	SettingsService settingsService;

    /**
	 * CREATE NEW GROUP
	 * @param groupData
	 * @return
	 */
	@PostMapping("/createGroup")
	public Map<String, Object> createGroup(@RequestBody CreateGroupData groupData){
		return settingsService.createGroup(groupData);
	}

    /**
	 * ADD MEMBERS TO GROUP
	 * @param token
	 * @param groupId
	 * @param users
	 * @return
	 */
	@GetMapping("/changeMembers")
	public boolean changeMembers(
			@RequestParam("token") String token,
			@RequestParam("groupId") Integer groupId,
			@RequestParam("users") String users
			) {
		return settingsService.changeMembers(token, groupId, users);
	}

    /**
	 * QUIT USER FROM GROUP
	 * @param groupId
	 * @param userId
	 * @return
	 */
	@DeleteMapping("/quitUser")
	public boolean quitUser(
			@RequestParam ("groupId") String groupId,
			@RequestParam ("userId") String userId
			) {
		return settingsService.quitUsers(groupId, userId);
	}
	
	/**
	 * GET GROUP USERS
	 * @param groupId
	 * @return
	 */
	@GetMapping("/getUsers")
	public Map<String, Object> getUsers (
			@RequestParam("groupId") Integer groupId
			) {
		return settingsService.getUsers(groupId);
	}
	
	/**
	 * DELETE GROUP
	 * @param groupId
	 * @return
	 */
	@DeleteMapping("/deleteGroup")
	public boolean deleteGroup(@RequestParam("groupId") String groupId) {
		return settingsService.deleteGroup(groupId);
	}
	
	/**
	 * GET SPRINTS + DATES
	 * @return
	 */
	@GetMapping("/getSprints")
	public Map<String, Object> getSprints() {
		return settingsService.getSprints();
	}
	
	/**
	 * UPDATE SPRINTS PERIODS
	 * @param sprintsData
	 * @return
	 */
	@PostMapping("/updateSprints")
	public boolean updateSprints(@RequestBody SprintsData[] sprintsData) {
		return settingsService.updateSprints(sprintsData);
	}

	/**
	 * GET ALL RIGHTS TO LIST
	 * @return
	 */
	@GetMapping("/getRights")
	public Map<String, Object> getRights() {
		return settingsService.getRights();
	}

	/**
	 * SAVE USER RIGHTS
	 * @param rightsData
	 * @return
	 */
	@PostMapping("/saveRights")
	public boolean saveRights(@RequestBody RightsData rightsData) {
		return settingsService.saveRights(rightsData);
	}
	
	/**
	 * GET TELEGRAM CHAT ID
	 * @return
	 */
	@GetMapping("/getChatId")
	public Map<String, Object> getChartId() {
		return settingsService.getChartId();
	}
	
	/**
	 * SAVE TELEGRAM CHAT ID
	 * @param chatId
	 * @return
	 */
	@PostMapping("/saveChatId")
	public boolean saveChatId(@RequestBody String chatId) {
		return settingsService.saveChatId(chatId);
	}
}
