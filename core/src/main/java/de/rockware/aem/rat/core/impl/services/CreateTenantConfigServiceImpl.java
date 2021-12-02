package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.services.CreateTenantConfigService;

import org.apache.jackrabbit.oak.commons.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;

import java.util.Dictionary;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author diwakar
 * Implements CreateTenantConfigService 
 * this class hold the start and end level configuration for the de.rockware.aem.tenant services.
 */
@Component(label = "Rockware AEM - Tenant Interface", policy = ConfigurationPolicy.OPTIONAL,  metatype = true, immediate = true, description = "Implementation of the de.rockware.aem.tenant interface for AEM Tenant..")
public class CreateTenantConfigServiceImpl implements CreateTenantConfigService {

	private static final Logger logger = getLogger(CreateTenantConfigServiceImpl.class);

	/**
	 * Configuration property.
	 */
	@Property(label = "Start Level", intValue = 2, description = "Levels that are lower than the start level will not be processed.")
	public static final String PROP_START_LEVEL = "create.de.rockware.aem.tenant.startLevel";

	/**
	 * Configuration property.
	 */
	@Property(label = "End Level", intValue = 5, description = "Levels that are higher than the end level will not be processed.")
	public static final String PROP_END_LEVEL = "create.de.rockware.aem.tenant.endLevel";

	/**
	 * Configuration property.
	 */
	@Property(label = "Read Access Level", intValue = 3, description = "Levels lower or equal to the configured value will only have groups with read access..")
	public static final String PROP_READ_ACCESS_LEVEL = "create.de.rockware.aem.tenant.readAccessLevel";


	private int startLevel;

	private int endLevel;

	private int readAccessLevel;

	


	/**
	 * Activates the service.
	 *
	 * @param context
	 *            ComponentContext
	 */
	@Activate
	public void activate(ComponentContext context) {
		logger.info("Activating Tenant Config API service.");
		Dictionary<String, Object> dictionary = context.getProperties();
		startLevel = PropertiesUtil.toInteger(dictionary.get(PROP_START_LEVEL), 2);
		endLevel = PropertiesUtil.toInteger(dictionary.get(PROP_END_LEVEL), 5);
		readAccessLevel = PropertiesUtil.toInteger(dictionary.get(PROP_READ_ACCESS_LEVEL), 3);
		
	}

	/**
	 * Deactivates the service.
	 *
	 * @param context component Context
	 */
	@Deactivate
	public void deactivate(ComponentContext context) {
		logger.info("Deactivating Tenant API service with context {}.", context.toString());
	}

	@Override
	public int getStartLevel() {
		return startLevel;
	}

	@Override
	public int getEndLevel() {
		return endLevel;
	}

	@Override
	public int getReadAccessLevel() {
		return readAccessLevel;
	}

	@Override
	public boolean isValidLevel(int level) {
		return (level > 0) && (startLevel <= level) && (level <= endLevel);
	}

	@Override
	public boolean isReadAccessLevel(int level) {
		return level <= readAccessLevel;
	}
}
