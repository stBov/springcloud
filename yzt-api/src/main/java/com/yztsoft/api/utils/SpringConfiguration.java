package com.yztsoft.api.utils;


import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "eureka.random")
	public EurekaConfig getEurekaConfig() {
		return new EurekaConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.application")
	public ApplicationInfo getApplicationInfo() {
		return new ApplicationInfo();
	}

}