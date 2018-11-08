package com.yztsoft.zuul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {
	@Autowired
	private DiscoveryClient discovery;

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<>();
		if (this.discovery != null) {
			List<String> services = this.discovery.getServices();
			for (String serviceId : services) {
				if (!Pattern.compile("-(server|schedule)").matcher(serviceId).find()) {
					resources.add(swaggerResource(serviceId, "/" + serviceId + "/v2/api-docs", "2.0"));
				}
			}
		}
		return resources;
	}

	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}
}
