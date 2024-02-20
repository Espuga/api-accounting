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
	
	public Map<String, Object> getToAuthorize() {
		return AuthorizeModel.getToAuthorize(myAccounting);
	}
	
	public boolean accpetTransaction(TransactionStateData transactionStateData) {
		return AuthorizeModel.accpetTransaction(myAccounting, transactionStateData);
	}
	public boolean dropTransaction(String groupId, String id) {
		return AuthorizeModel.dropTransaction(myAccounting, groupId, id);
	}
	
	public Map<String, Object> getToAccept(String groupId) {
		return AuthorizeModel.getToAccept(myAccounting, groupId);
	}
	
	public boolean accpetTheTransaction(TransactionStateData transactionStateData) {
		return AuthorizeModel.accpetTheTransaction(myAccounting, transactionStateData);
	}
}
