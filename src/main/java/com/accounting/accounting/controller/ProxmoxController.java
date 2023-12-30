package com.accounting.accounting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.service.ProxmoxService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/proxmox")
public class ProxmoxController {
	@Autowired
	ProxmoxService proxmoxService;
	
	@GetMapping("/getInit")
	public Map<String, Object> getInit(
			@RequestParam("start") String start, 
			@RequestParam("end") String end,
			@RequestParam("priceCpu") double priceCpu,
			@RequestParam("priceDisk") double priceDisk
			) {
		return proxmoxService.getInit(start, end, priceCpu, priceDisk);
	}
	@GetMapping("/getCpuUsage")
	public Map<String, Object> getCpuUsage(@RequestParam("start") String start, @RequestParam("end") String end) {
		return proxmoxService.getCpuUsage(start, end);
	}
}
