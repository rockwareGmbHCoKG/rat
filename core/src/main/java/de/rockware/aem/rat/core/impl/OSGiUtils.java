package de.rockware.aem.rat.core.impl;

import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * OSGI Utility class.
 * Created by ogebert on 16.07.15.
 */
public final class OSGiUtils {

	/**
	 * Hide me!
	 */
	private OSGiUtils() {
		// hidden
	}

	/**
	 * Returns an auth info map used when a service wants to get an instance of ResourceResolver through the
	 * {@link ResourceResolverFactory}
	 *
	 * @param serviceClass - the subservice class
	 * @return a hash map with the service authentification info
	 * @see ResourceResolverFactory#SUBSERVICE
	 */
	public static Map<String, Object> getAuthInfoMap(Class<?> serviceClass) {
		Map<String, Object> authInfo = new HashMap<>();
		authInfo.put(ResourceResolverFactory.SUBSERVICE, serviceClass.getName());
		return authInfo;
	}

}
