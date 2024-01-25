package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.CreateGroupData;
import com.accounting.accounting.model.RightsData;
import com.accounting.accounting.model.SprintsData;
import com.accounting.accounting.model.TransactionData;
import com.accounting.accounting.service.DashboardService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/accounting")
public class DashboardController {
	@Autowired
	DashboardService dashboardService;
	
	@GetMapping("/getHome")
	public Map<String, Object> getHome(@RequestParam("groupId") Integer groupId) {
		return dashboardService.getHome(groupId);
	}
	
	/**
	 * RETURN TABLE & CHART DATA
	 * @param groupId
	 * @return
	 */
	@GetMapping("/getInit")
	public Map<String, Object> getInit(@RequestParam("groupId") Integer groupId) {
		return dashboardService.getInit(groupId);
	}
	
	/**
	 * NEW TRANSACTION
	 * @param transactionData
	 * @return
	 */
	@PostMapping("/setTransaction")
	public Map<String, Object> setTransaction(@RequestBody TransactionData transactionData) {
		return dashboardService.setTransaction(transactionData);
	}
	
	/**
	 * RETURN GROUPS LIST
	 * @param token
	 * @return
	 */
	@GetMapping("/getGroups")
	public Map<String, Object> getGroups(@RequestParam("token") String token) {
		return dashboardService.getGroups(token);
	}
	
	/**
	 * CREATE NEW GROUP
	 * @param groupData
	 * @return
	 */
	@PostMapping("/createGroup")
	public Map<String, Object> createGroup(@RequestBody CreateGroupData groupData){
		return dashboardService.createGroup(groupData);
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
		return dashboardService.changeMembers(token, groupId, users);
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
		return dashboardService.getUsers(groupId);
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
		return dashboardService.quitUsers(groupId, userId);
	}
	
	/**
	 * DELETE GROUP
	 * @param groupId
	 * @return
	 */
	@DeleteMapping("/deleteGroup")
	public boolean deleteGroup(@RequestParam("groupId") String groupId) {
		return dashboardService.deleteGroup(groupId);
	}
	
	/**
	 * GET SPRINTS + DATES
	 * @return
	 */
	@GetMapping("/getSprints")
	public Map<String, Object> getSprints() {
		return dashboardService.getSprints();
	}
	
	/**
	 * UPDATE SPRINTS PERIODS
	 * @param sprintsData
	 * @return
	 */
	@PostMapping("/updateSprints")
	public boolean updateSprints(@RequestBody SprintsData[] sprintsData) {
		return dashboardService.updateSprints(sprintsData);
	}

	/**
	 * GET ALL RIGHTS TO LIST
	 * @return
	 */
	@GetMapping("/getRights")
	public Map<String, Object> getRights() {
		return dashboardService.getRights();
	}

	/**
	 * SAVE USER RIGHTS
	 * @param rightsData
	 * @return
	 */
	@PostMapping("/saveRights")
	public boolean saveRights(@RequestBody RightsData rightsData) {
		return dashboardService.saveRights(rightsData);
	}
}
