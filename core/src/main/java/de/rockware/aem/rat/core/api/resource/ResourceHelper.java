package de.rockware.aem.rat.core.api.resource;

import com.day.cq.commons.jcr.JcrConstants;
import com.drew.lang.annotations.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple utility class that provides some useful helpers.
 */
@Slf4j
public class ResourceHelper {

    private static final List<String> INVALID_CUSTOM_TOP_LEVEL = new ArrayList<String>(){
        {
            add("/content");
            add("/etc");
            add("/home");
            add("/bin");
            add("/apps");
            add("/libs");
        }
    };


    /**
     * Create a resource and all the parents if needed.
     * @param absPath       resource path
     * @param primaryType   primary type
     * @param resolver      resource resolver
     * @return              newly created resource
     */
    public static Resource createResource(String absPath, String primaryType, @NotNull ResourceResolver resolver) {
        Resource returnValue = resolver.getResource(absPath);
        if (returnValue == null) {
            String parentPath = StringUtils.substringBeforeLast(absPath, "/");
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
            // no parent . we are on top level
            if (parentPath.equals("")) {
                if(isValidCustomTopLevel(absPath)) {
                    returnValue = createResource(resolver.getResource("/"), StringUtils.substringAfter(absPath, "/"), properties);
                } else {
                    log.error("Could not create resource with path {}. Toplevel path is reserved by AEM and should already by there.", absPath);
                }
            } else if (!parentPath.equals(absPath)) {
                Resource parentResource = createResource(parentPath, primaryType, resolver);
                if (parentResource == null) {
                    log.error("Cannot create resource {}. Parent is null.", absPath);
                } else {
                    returnValue = createResource(parentResource, StringUtils.substringAfterLast(absPath, "/"), properties);
                }
            } else {
				log.info("Could not get root resource to create. Path {} may be corrupted.", absPath);
			}
        } else {
            log.debug("Resource {} is already there.", absPath);
        }
        return returnValue;
    }

    /**
     * Checks if the top level is valid. That is true for all top levels that are not predefined by AEM.
     * @param path  top level path
     * @return  true if the path is a valid custom toplevel path
     */
    public static boolean isValidCustomTopLevel(String path) {
        return !INVALID_CUSTOM_TOP_LEVEL.contains(path);
    }

    /**
     * Create a resource and return it or null if exception is thrown.
     * @param parentResource    parent resource
     * @param resourceName      resource name
     * @param properties        property map
     * @return                  resource or null
     */
    private static Resource createResource(@NotNull Resource parentResource, String resourceName, Map<String, Object> properties) {
        Resource returnValue = null;
        try {
        	ResourceResolver resolver = parentResource.getResourceResolver();
			Resource resource = resolver.getResource(parentResource, resourceName);
			if (resource == null) {
				resource = resolver.create(parentResource, resourceName, properties);
			} else {
				log.trace("Resource {} on path {} already exists.", resourceName, parentResource.getPath());
			}
            resolver.commit();
			returnValue = resource;
        } catch (PersistenceException ex) {
            log.error("Could not create resource with name {}: {}", resourceName, ex.getMessage());
        }
        return returnValue;
    }
    
    /**
     * Get the level of the resource path by counting the "/". This method does not a lot of sanity checks on the path.
     * So paths like "//" or "/../bla/" are accepted and there will be a result. The method doesn't even check if the resource
     * exists.
     * @param resourcePath  a resource path
     * @return  level
     */
    public static int getResourceLevel(String resourcePath) {
        if (StringUtils.isEmpty(resourcePath)) {
            throw new IllegalArgumentException("Resource path must not be null or empty!");
        } else if (!resourcePath.startsWith("/")) {
            throw new IllegalArgumentException("Resource path must start with a slash (/). Path: " + resourcePath);
        }

        return resourcePath.equals("/") ? 0 : StringUtils.countMatches(resourcePath, "/");
    }
}
