package de.rockware.aem.rat.core.impl.services;

import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import de.rockware.aem.rat.core.api.services.CreateTenantPageService;
import de.rockware.aem.rat.core.api.services.CreateTenantSubservicesRegistry;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


/**
 * Implements CreateTenantPageService
 * this class provides methods to manipulate the pages for the List of additional path 
 */
@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
@Slf4j
public class CreateTenantPageServiceImpl implements CreateTenantPageService {

	@Reference(bind = "bindMe", unbind = "unbindMe")
	private CreateTenantSubservicesRegistry registry;
	
	
	@Activate
	public void activate(ComponentContext context) {
		log.info("Activating Tenant API service.");
		registry.register(this);
	}
	
	@Deactivate
	public void deactivate(ComponentContext context){
		registry.unregister(this);
	}

	@Override
	public List<String> createResources(ResourceResolver resolver, String resourcePath) {
		List<String> paths = new ArrayList<>();
		Resource resource = resolver.getResource(resourcePath);
		PageManager pageManager = resolver.adaptTo(PageManager.class);
		String templatePath = "";
		String pagePath;
		String parentPagePath;
		String pageTitle = resource.getChild("jcr:content").getValueMap().get("jcr:title", resource.getName());
		return paths;
	}

	
	@Override
	public List<String> deleteResources(ResourceResolver resolver, String resourcePath) {
		//currently this method is not being used
		List<String> paths = new ArrayList<>();
		return paths;
	}

	
	@Override
	public List<String> moveResources(ResourceResolver resolver, String resourcePath, String destinationPath) {
		//currently this method is not being used
		List<String> paths = new ArrayList<>();
		return paths;
	}

	/**
	 * Bind the service.
	 *
	 * @param registry
	 *            provider service
	 */
	public void bindMe(CreateTenantSubservicesRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Unbind the service.
	 *
	 * @param registry
	 *            provider service
	 */
	public void unbindMe(CreateTenantSubservicesRegistry registry) {
		this.registry = null;
	}

}
