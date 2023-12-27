package com.accounting.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountingApplication {

	public static void main(String[] args) {
		System.setProperty("server.port", "5173");
		SpringApplication.run(AccountingApplication.class, args);
	}

}
