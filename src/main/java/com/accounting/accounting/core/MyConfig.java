package com.accounting.accounting.core;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MyConfig {
	@Bean(name = "myaccounting")
	DataSource myAccounting() {
		try {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	        //dataSource.setUrl("jdbc:mysql://192.168.0.121:3306/accounting"); 
	        //dataSource.setUrl("jdbc:mysql://192.168.1.48:3306/accounting"); 
			dataSource.setUrl("jdbc:mysql://192.168.0.121:3306/accounting"); 
			dataSource.setUsername("marc");
			dataSource.setPassword("perprotegir");
			return dataSource;
		} catch (Exception e) {
			return null;
		}
	}
	@Bean(name = "jdbcaccounting")
	@Primary
	JdbcTemplate produccioJdbcTemplate(@Qualifier("myaccounting") DataSource ds) {
		return new JdbcTemplate(ds);
	}
	
	@Bean(name = "myproxmox")
	DataSource myProxmox() {
		try {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	        //dataSource.setUrl("jdbc:mysql://192.168.0.121:3306/accounting"); 
	        //dataSource.setUrl("jdbc:mysql://192.168.1.48:3306/proxmox"); 
			dataSource.setUrl("jdbc:mysql://192.168.0.121:3306/proxmox"); 
			dataSource.setUsername("marc");
			dataSource.setPassword("perprotegir");
			return dataSource;
		} catch (Exception e) {
			return null;
		}
	}
	@Bean(name = "jdbcproxmox")
	JdbcTemplate proxmoxJdbcTemplate(@Qualifier("myproxmox") DataSource ds) {
		return new JdbcTemplate(ds);
	}
}
