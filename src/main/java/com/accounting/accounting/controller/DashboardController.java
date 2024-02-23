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
import com.accounting.accounting.model.UpdateTransactionData;
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

  /**
   * GET SPRINTS + SPRINTS PERIODS
   * @return
   */
  @GetMapping("/getSprints")
  public Map<String, Object> getSprints() {
    return dashboardService.getSprints();
  }

  /**
   * UPDATE TRANSACTION INFO
   * @param updateTransactionData
   * @return
   */
  @PostMapping("/updateTransaction")
  public boolean updateTransaction(@RequestBody UpdateTransactionData updateTransactionData) {
    return dashboardService.updateTransaction(updateTransactionData);
  }

  /**
   * DELETE TRANSACTION
   * @param groupId
   * @param id
   * @return
   */
  @DeleteMapping("/deleteTransaction/{groupId}/{id}")
  public boolean deleteTransaction(@PathVariable("groupId") String groupId, @PathVariable("id") String id) {
    return dashboardService.deleteTransaction(groupId, id);
  }
}
