package com.accounting.accounting.core;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class MyConfig {
  public static final String DB_SERVER = "127.0.0.1";
  public static final String DB_PORT = "3306";
  public static final String DB_SCHEMA = "accounting2";
  public static final String DB_SCHEMA_PROXMOX = "proxmox";
  public static final String DB_USER = "marc";
  public static final String DB_PASSWD = "marc1234";

	@Bean(name = "myaccounting")
	DataSource myAccounting() {
		try {
      System.out.println(String.format("jdbc:mysql://%s:%s/%s", DB_SERVER, DB_PORT, DB_SCHEMA));
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", DB_SERVER, DB_PORT, DB_SCHEMA)); 
			dataSource.setUsername(DB_USER);
			dataSource.setPassword(DB_PASSWD);
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
      dataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s", DB_SERVER, DB_PORT, DB_SCHEMA_PROXMOX)); 
			dataSource.setUsername(DB_USER);
			dataSource.setPassword(DB_PASSWD);
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
