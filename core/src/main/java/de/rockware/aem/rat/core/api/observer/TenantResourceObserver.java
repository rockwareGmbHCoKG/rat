package de.rockware.aem.rat.core.api.observer;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

/**
 * @author diwakar
 * This is common observer interface for the page modification.
 */
public interface TenantResourceObserver {

	/**
	 * This Api is used to create new resource at a given path
	 * @param resolver resource resolver
	 * @param resourcePath	resource path
	 * @return	list with paths that have been processed.
	 */
	List<String> createResources(ResourceResolver resolver, String resourcePath);

	/**
	 * This Api is used to delete selected resource
	 * @param resolver resource resolver
	 * @param path path of resource to be deleted
	 * @return	list with paths that have been processed
	 */
	List<String> deleteResources(ResourceResolver resolver, String path);

	/**
	 * This Api is used to relocate selected resource from source to destination path 
	 * @param resolver	resource resolver
	 * @param resourcePath	old resource path
	 * @param destinationPath	new resource path
	 * @return	list with paths that have been processed.
	 */
	List<String> moveResources(ResourceResolver resolver, String resourcePath, String destinationPath);


}
