package de.rockware.aem.rat.core.impl.events;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;

import de.rockware.aem.rat.core.api.resource.ResourceHelper;
import de.rockware.aem.rat.core.api.services.TenantSecurityService;
import de.rockware.aem.rat.core.api.services.GroupManagerService;
import de.rockware.aem.rat.core.api.services.InstanceService;
import de.rockware.aem.rat.core.impl.ResourceUtils;
import de.rockware.aem.rat.core.api.config.RichConfiguration;
import de.rockware.aem.rat.core.impl.crud.ResourceCreator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Eventhandler. This service tracks the creation of new pages and creates matching dam folders, cloud configs, user etc if needed.
 * Created by ogebert on 28.01.16.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL, property = {
		"event.topics=" + PageEvent.EVENT_TOPIC,
		JobConsumer.PROPERTY_TOPICS + "=" + CreateContentPageEventHandlerImpl.JOB_TOPICS
})
@Slf4j
public final class CreateContentPageEventHandlerImpl implements EventHandler, JobConsumer {

	private static final String PAGE_EVENT = "pageEvent";

	public static final String JOB_TOPICS = "de/rockware/aem/tenants";

	@Reference
	private InstanceService instanceService;

	@Reference
	GroupManagerService gMService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private JobManager jobManager;
	
	@Reference
	private TenantSecurityService securityService;

	@Override
	public void handleEvent(Event event) {
		log.trace("Handle event now.");
		PageEvent pageEvent = PageEvent.fromEvent(event);
		if (instanceService.isAuthor()) {
			Map<String, Object> properties = new HashMap<>();
			properties.put(PAGE_EVENT, pageEvent);
			jobManager.addJob(JOB_TOPICS, properties);
		} else {
			log.trace("Nothing to do on a publish instance.");
		}
	}

	@Override
	public JobResult process(Job job) {
		log.trace("Start job processing.");
		PageEvent pageEvent = (PageEvent) job.getProperty(PAGE_EVENT);
		ResourceResolver resolver = ResourceUtils.getResolver(resourceResolverFactory, this.getClass());
		if (resolver != null && pageEvent != null && pageEvent.isLocal()) {
			Iterator<PageModification> modificationsIterator = pageEvent.getModifications();
			while (modificationsIterator.hasNext()) {
				PageModification modification = modificationsIterator.next();
				if (PageModification.ModificationType.CREATED.equals(modification.getType())) {
					try {
						List<String> resourcePaths = new ArrayList<>();
						String path = modification.getPath();
						int currentLevel = ResourceHelper.getResourceLevel(path);
						Resource currentResource = resolver.getResource(path);
						if (currentResource != null) {
							RichConfiguration richConfig = new RichConfiguration(gMService.getTenantRATConfig(currentResource), gMService.getGlobalRATConfig(currentResource), path);
							if (isRelevantResource(path, richConfig)) {
								switch (modification.getType()) {
									case CREATED:
										resourcePaths.addAll(ResourceCreator.createResources(path, richConfig, resolver));
										break;
									case DELETED:
									case MOVED:
										log.debug("Nothing to do here");
								}
							} else {
								log.debug("Path {} is not a content page path. Will not create dam path, acls and more.", path);
							}
							securityService.handleGroupsAndACLs(resourcePaths, richConfig, currentLevel, resolver);
						}
					} catch (Exception ex) {
						log.error("Could not process job: {}", ex.getMessage());
					}
				}
			}
			ResourceUtils.closeResolver(resolver);
		}
		return JobResult.OK;
	}

	/**
	 * Check if the path level is ok.
	 * @param resourcePath	current path
	 * @param richConfig configuration
	 * @return	true if actions are needed to be taken
	 */
	private boolean isRelevantResource(String resourcePath, RichConfiguration richConfig) {
		int currentLevel = ResourceHelper.getResourceLevel(resourcePath);
		return richConfig.isTenantActive() &&  currentLevel >= richConfig.getStartLevel() && currentLevel <= richConfig.getEndLevel();
	}
}
