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
	        dataSource.setUrl("jdbc:mysql://localhost:3306/accounting"); 
			dataSource.setUsername("marc2");
			dataSource.setPassword("marc1234");
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
}
