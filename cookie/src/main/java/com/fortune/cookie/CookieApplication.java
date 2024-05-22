package com.fortune.cookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@EnableCaching
@SpringBootApplication
public class CookieApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookieApplication.class, args);
	}
	@Bean
	public DataSource dataSource() {
		// Configure and return your DataSource
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:mariadb://localhost:3306/fortune_cookie");
		dataSource.setUsername("root");
		dataSource.setPassword("");
		return dataSource;
	}

}
