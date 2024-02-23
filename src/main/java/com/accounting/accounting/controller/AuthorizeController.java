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

import com.accounting.accounting.model.TransactionStateData;
import com.accounting.accounting.service.AuthorizeService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/authorize")
public class AuthorizeController {

  @Autowired
  AuthorizeService authorizeService;
	
  /**
   * TEACHERS GET TRANSACTIONS TO AUTHORIZE
   * @return
   */
	@GetMapping("/getToAuthorize")
	public Map<String, Object> getToAuthorize() {
		return authorizeService.getToAuthorize();
	}
	
  /**
   * TEACHERS AUTHORIZE THE TRANSACTION
   * @param transactionStateData
   * @return
   */
	@PostMapping("accpetTransaction")
	public boolean accpetTransaction(@RequestBody TransactionStateData transactionStateData) {
		return authorizeService.accpetTransaction(transactionStateData);
	}

  /**
   * TEACHERS UNAUTHORIZE THE TRANSACTION
   * @param groupId
   * @param id
   * @return
   */
	@DeleteMapping("dropTransaction/{groupId}/{id}")
	public boolean dropTransaction(@PathVariable("groupId") String groupId, @PathVariable("id") String id) {
		return authorizeService.dropTransaction(groupId, id);
	}
	
  /**
   * STUDENS GET THE TRANSACTIONS TO ACCEPT
   * @param groupId
   * @return
   */
	@GetMapping("/getToAccept")
	public Map<String, Object> getToAccept(@RequestParam String groupId) {
		return authorizeService.getToAccept(groupId);
	}
	
  /**
   * STUDENS ACCEPT THE TRANSACTION
   * @param transactionStateData
   * @return
   */
	@PostMapping("accpetTheTransaction")
	public boolean accpetTheTransaction(@RequestBody TransactionStateData transactionStateData) {
		return authorizeService.accpetTheTransaction(transactionStateData);
	}
}
