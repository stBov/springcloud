package com.yztsoft.zuul;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ZuulRoutesLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

	public static final String DEFAULT_ROUTE = "/**";

	private DiscoveryClient discovery;

	private ZuulProperties properties;

	/*
	 * private ServiceRouteMapper serviceRouteMapper; private
	 * AtomicReference<Map<String, ZuulRoute>> routes = new AtomicReference<>();
	 */

	public ZuulRoutesLocator(String servletPath, DiscoveryClient discovery, ZuulProperties properties) {
		super(servletPath, properties);

		/* this.serviceRouteMapper = new SimpleServiceRouteMapper(); */
		this.discovery = discovery;
		this.properties = properties;
	}

	@Override
	public Route getMatchingRoute(final String path) {

		Route router = getSimpleMatchingRoute(path);
		if (router != null) {
			// router.setFullPath(router.getFullPath().replaceAll("/"+router.getLocation()+"(.*)$",
			// "${1}"));
			if (router.getPath().matches("/" + router.getLocation() + "/v2/api-docs$")) {
				router.setFullPath("/v2/api-docs");
				router.setPath("/v2/api-docs");

			}
		}
		return router;
	}

	/*
	 * @Override public List<Route> getRoutes() { // if (this.routes.get() ==
	 * null) { this.routes.set(locateRoutes()); // } List<Route> values = new
	 * ArrayList<>(); for (String url : this.routes.get().keySet()) { ZuulRoute
	 * route = this.routes.get().get(url); String path = route.getPath(); Route
	 * myRoute = getRoute(route, path); myRoute.setFullPath("/" +
	 * route.getLocation() + "/v2/api-docs"); values.add(myRoute); } return
	 * values; }
	 * 
	 * public ZuulRoutesLocator(String servletPath, DiscoveryClient discovery,
	 * ZuulProperties properties, ServiceRouteMapper serviceRouteMapper) {
	 * this(servletPath, discovery, properties); this.serviceRouteMapper =
	 * serviceRouteMapper; }
	 */
	protected Route getRoute(ZuulRoute route, String path) {
		Route ret = super.getRoute(route, path);

		if (ret == null) {
			return null;
		}
		String changedFullPath = "/" + ret.getLocation() + "/v2/api-docs";
		ret.setFullPath(changedFullPath);

		return ret;
	}

	@Override
	public void refresh() {
		doRefresh();
	}

	@Override
	protected Map<String, ZuulRoute> locateRoutes() {
		LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
		LinkedHashMap<String, ZuulRoute> locateRoutesMap = new LinkedHashMap<String, ZuulRoute>();
		locateRoutesMap.putAll(super.locateRoutes());
		if (this.discovery != null) {
			Map<String, ZuulRoute> staticServices = new LinkedHashMap<String, ZuulRoute>();
			for (ZuulRoute route : locateRoutesMap.values()) {
				String serviceId = route.getServiceId();
				if (serviceId == null) {
					serviceId = route.getId();
				}
				if (serviceId != null) {
					staticServices.put(serviceId, route);
				}
			}
			// Add routes for discovery services by default
			List<String> services = this.discovery.getServices();
			for (String serviceId : services) {
				// Ignore specifically ignored services and those that were
				// manually
				// configured
				String key = null;
				/***************
				 * Pattern pattern = Pattern.compile("-([a-z]*)$"); Matcher
				 * matcher = pattern.matcher(serviceId); if (matcher.find()) {
				 * key = "/" + matcher.group(1) + "/**"; }
				 ****************/
				// String key="";
				key = /* "/" + serviceId + */"/v2/api-docs";
				ZuulRoute route = new ZuulRoute(key, serviceId);
				route.setId(serviceId + "Swagger");
				route.setStripPrefix(false);

				if (staticServices.containsKey(serviceId) && staticServices.get(serviceId).getUrl() == null) {
					// Explicitly configured with no URL, cannot be ignored
					// all static routes are already in routesMap
					// Update location using serviceId if location is null
					ZuulRoute staticRoute = staticServices.get(serviceId);
					if (!StringUtils.hasText(staticRoute.getLocation())) {
						staticRoute.setLocation(serviceId);
					}
				}
				if (!routesMap.containsKey(key)) {
					routesMap.put("/" + serviceId + route.getPath(), route);
				}
			}
		}

		if (routesMap.get(DEFAULT_ROUTE) != null) {
			ZuulRoute defaultRoute = routesMap.get(DEFAULT_ROUTE);
			// Move the defaultServiceId to the end
			routesMap.remove(DEFAULT_ROUTE);
			routesMap.put(DEFAULT_ROUTE, defaultRoute);
		}
		LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
		for (Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
			String path = entry.getKey();
			// Prepend with slash if not already present.
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			if (StringUtils.hasText(this.properties.getPrefix())) {
				path = this.properties.getPrefix() + path;
				if (!path.startsWith("/")) {
					path = "/" + path;
				}
			}
			values.put(path, entry.getValue());
		}
		return values;
	}
}
