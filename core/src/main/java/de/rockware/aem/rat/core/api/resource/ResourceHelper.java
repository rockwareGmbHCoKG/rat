package de.rockware.aem.rat.core.api.resource;

import com.day.cq.commons.jcr.JcrConstants;

import de.rockware.aem.rat.core.impl.ResourceUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Simple utility class that provides some useful helpers.
 */
public class ResourceHelper {

    private static final Logger logger = getLogger(ResourceHelper.class);

    private static List<String> invalidCustomTopLevel = new ArrayList<String>(){
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
    public static Resource createResource(String absPath, String primaryType, ResourceResolver resolver) {
        Resource returnValue = resolver.getResource(absPath);
        if (returnValue == null) {
            String parentPath = StringUtils.substringBeforeLast(absPath, "/");
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
            // no parent . we are on top level
            if (parentPath.equals("")) {
                if(isValidCustomTopLevel(absPath)) {
                    returnValue = createResource(resolver.getResource("/"),StringUtils.substringAfter(absPath, "/"), properties);
                } else {
                    logger.error("Could not create resource with path {}. Toplevel path is reserved by AEM and should already by there.", absPath);
                }
            } else if (!parentPath.equals(absPath)) {
                Resource parentResource = createResource(parentPath, primaryType, resolver);
                if (parentResource == null) {
                    logger.error("Cannot create resource {}. Parent is null.", absPath);
                } else {
                    returnValue = createResource(parentResource, StringUtils.substringAfterLast(absPath, "/"), properties);
                }
            } else {
				logger.info("Could not get root resource to create. Path {} may be corrupted.", absPath);
			}
        } else {
            logger.debug("Resource {} is already there.", absPath);
        }
        return returnValue;
    }

    /**
     * Checks if the top level is valid. That is true for all top levels that are not predefined by AEM.
     * @param path  top level path
     * @return  true if the path is a valid custom toplevel path
     */
    public static boolean isValidCustomTopLevel(String path) {
        return !invalidCustomTopLevel.contains(path);
    }

    /**
     * Create a resource and return it or null if exception is thrown.
     * @param parentResource    parent resource
     * @param resourceName      resource name
     * @param properties        property map
     * @return                  resource or null
     */
    private static Resource createResource(Resource parentResource, String resourceName, Map<String, Object> properties) {
        Resource returnValue = null;
        try {
        	ResourceResolver resolver = parentResource.getResourceResolver();
			Resource resource = resolver.getResource(parentResource, resourceName);
			if (resource == null) {
				resource = resolver.create(parentResource, resourceName, properties);
			} else {
				logger.trace("Resource {} on path {} already exists.", resourceName, parentResource.getPath());
			}
            resolver.commit();
			returnValue = resource;
        } catch (PersistenceException ex) {
            logger.error("Could not create resource with name {}: {}", resourceName, ex.getMessage());
        }
        return returnValue;
    }
    
	/**
	 * delete a resource and return void.
	 * 
	 * @param resource resource
	 */
	public static void deleteResource(Resource resource) {
		try {
			if (resource != null) {
				ResourceResolver resolver = resource.getResourceResolver();
				resolver.delete(resource);
				resolver.commit();
			}
		} catch (PersistenceException ex) {
			logger.error("Could not create resource with name {}: {}", resource.getName(), ex.getMessage());
		}
	}

	/**
	 * rename a resource and return void.
	 * 
	 * @param resource resource
	 * @param sourcePath Source Path
	 * @param destinationPath destination Path
	 */
	public static void moveResource(Resource resource, String sourcePath, String destinationPath) {
		try {
			if (resource != null && sourcePath != null && destinationPath != null) {
				ResourceResolver resolver = resource.getResourceResolver();
				Session session = resolver.adaptTo(Session.class);
				session.move(sourcePath, destinationPath);
				session.save();
			}
		} catch (RepositoryException ex) {
			logger.error("Could not create resource with name {}: {}", resource.getName(), ex.getMessage());
		}
	}

    /**
     * Get the level of the resource. "/" is level 0, "/content" level 1 and so on.
     * @param resource  resource
     * @return  the level
     */
    public static int getResourceLevel(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource must not be null!");
        }
        return getResourceLevel(resource.getPath());
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

	/**
	 * Get a new resource resolver - don't forget to close it or refresh it if needed.
	 *
	 * @return resolver or null
	 */
	public static ResourceResolver getResolver(ResourceResolverFactory resourceResolverFactory, Class clazz) {
		ResourceResolver resolver = null; //NOPMD
		try {
			resolver = resourceResolverFactory.getServiceResourceResolver(ResourceUtils.getAuthInfoMap(clazz));
		} catch (LoginException ex) {
			logger.debug("Cannot get resourceresolver for class {}: {}.", clazz.getName(), ex.getMessage());
		}
		return resolver;
	}

	/**
	 * Savely close a resource resolver.
	 */
	public static void closeResolver(ResourceResolver resolver) {
		if (resolver.isLive()) {
			resolver.close();
		} else {
			logger.info("Cannot close resolver. Resolver is not live.");
		}
	}

}
