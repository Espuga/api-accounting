package com.accounting.accounting.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accounting.accounting.service.HomeService;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

@RestController 
@CrossOrigin(origins = "*")
@RequestMapping("/home")
public class HomeController {
  @Autowired
  HomeService homeService;

  /**
   * GET THE AMOUNT OF A GROUP
   * @param groupId
   * @return
   */
	@GetMapping("/getHome")
	public Map<String, Object> getHome(@RequestParam("groupId") Integer groupId) {
		return homeService.getHome(groupId);
	}

  /**
   * GET HOME n OF MACHINES
   * @param groupId
   * @return
   */
  @GetMapping("/getNMachines")
  public Map<String, Object> getNMachines(@RequestParam("groupId") String groupId) {
    return homeService.getNMachines(groupId);
  }

  @GetMapping("/provaCron")
  public void provaCron() {
    homeService.provaCron();
  }
}
