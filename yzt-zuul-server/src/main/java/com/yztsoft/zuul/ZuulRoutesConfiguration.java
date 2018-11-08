package com.yztsoft.zuul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuulRoutesConfiguration {
	
	@Autowired
	private DiscoveryClient discovery;
	@Autowired
	protected ZuulProperties zuulProperties;
	@Autowired
	protected ServerProperties server;

	@Bean
	public ZuulRoutesLocator routeLocator() {
		return new ZuulRoutesLocator(this.server.getServletPrefix(), this.discovery, this.zuulProperties/*,
				this.serviceRouteMapper*/);
	}
}
