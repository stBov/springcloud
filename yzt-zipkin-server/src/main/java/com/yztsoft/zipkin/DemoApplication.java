package com.yztsoft.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import zipkin.server.EnableZipkinServer;
import zipkin.storage.mysql.MySQLStorage;

import javax.sql.DataSource;

@EnableZipkinServer
@EnableDiscoveryClient
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 *
	 * @param datasource
	 * @return
     */
	@Bean
	public MySQLStorage mySQLStorage(DataSource datasource) {
		return MySQLStorage.builder().datasource(datasource).executor(Runnable::run).build();
	}
}
