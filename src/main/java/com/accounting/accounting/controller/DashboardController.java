package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.model.TransactionData;
import com.accounting.accounting.service.DashboardService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/accounting")
public class DashboardController {
	@Autowired
	DashboardService dashboardService;
	
	@GetMapping("/getInit")
	public Map<String, Object> getInit() {
		return dashboardService.getInit();
	}
	
	@PostMapping("/setTransaction")
	public Map<String, Object> setTransaction(@RequestBody TransactionData transactionData) {
		return dashboardService.setTransaction(transactionData);
	}

}
