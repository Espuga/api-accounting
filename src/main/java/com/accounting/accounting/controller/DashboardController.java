package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.CreateGroupData;
import com.accounting.accounting.model.TransactionData;
import com.accounting.accounting.service.DashboardService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/accounting")
public class DashboardController {
	@Autowired
	DashboardService dashboardService;
	
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
	
	
	@PostMapping("/createGroup")
	public boolean createGroup(@RequestBody CreateGroupData groupData){
		return dashboardService.createGroup(groupData);
	}
	
	@GetMapping("/changeMembers")
	public boolean changeMembers(
			@RequestParam("token") String token,
			@RequestParam("groupId") Integer groupId,
			@RequestParam("users") String users
			) {
		return dashboardService.changeMembers(token, groupId, users);
	}
	
	@GetMapping("/getTotal")
	public Map<String, Object> getTotal(@RequestParam("groupId") Integer groupId){
		return dashboardService.getTotal(groupId);
	}

}
