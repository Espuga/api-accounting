package com.accounting.accounting.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.accounting.accounting.model.AuthorizeModel;
import com.accounting.accounting.model.TransactionStateData;

@Service
public class AuthorizeService {
	@Autowired
	@Qualifier("jdbcaccounting")
	JdbcTemplate myAccounting;
	
  /**
   * TEACHERS GET TRANSACTIONS TO AUTHORIZE
   * @return
   */
	public Map<String, Object> getToAuthorize() {
		return AuthorizeModel.getToAuthorize(myAccounting);
	}
	
  /**
   * TEACHERS AUTHORIZE THE TRANSACTION
   * @param transactionStateData
   * @return
   */
	public boolean accpetTransaction(TransactionStateData transactionStateData) {
		return AuthorizeModel.accpetTransaction(myAccounting, transactionStateData);
	}

  /**
   * TEACHERS UNAUTHORIZE THE TRANSACTION
   * @param groupId
   * @param id
   * @return
   */
	public boolean dropTransaction(String groupId, String id) {
		return AuthorizeModel.dropTransaction(myAccounting, groupId, id);
	}
	
  /**
   * STUDENS GET THE TRANSACTIONS TO ACCEPT
   * @param groupId
   * @return
   */
	public Map<String, Object> getToAccept(String groupId) {
		return AuthorizeModel.getToAccept(myAccounting, groupId);
	}
	
  /**
   * STUDENS ACCEPT THE TRANSACTION
   * @param transactionStateData
   * @return
   */
	public boolean accpetTheTransaction(TransactionStateData transactionStateData) {
		return AuthorizeModel.accpetTheTransaction(myAccounting, transactionStateData);
	}
}
