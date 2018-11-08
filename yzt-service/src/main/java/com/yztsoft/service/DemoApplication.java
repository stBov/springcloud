package com.yztsoft.service;

import com.yztsoft.service.utils.EurekaRandomPortConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients
public class DemoApplication   extends EurekaRandomPortConfig {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
