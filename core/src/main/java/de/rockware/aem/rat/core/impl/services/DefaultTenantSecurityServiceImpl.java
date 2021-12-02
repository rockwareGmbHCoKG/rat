package de.rockware.aem.rat.core.impl.services;


import de.rockware.aem.rat.core.api.resource.ResourceHelper;
import de.rockware.aem.rat.core.api.security.GroupType;
import de.rockware.aem.rat.core.api.security.GroupWrapper;
import de.rockware.aem.rat.core.api.security.services.TenantSecurityService;
import de.rockware.aem.rat.core.api.services.CreateTenantConfigService;
import de.rockware.aem.rat.core.api.services.CreateTenantGroupsService;
import de.rockware.aem.rat.core.api.services.CreateTenantPageService;
import de.rockware.aem.rat.core.api.services.GroupManagerService;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * {@inheritDoc}
 * Created by ogebert on 14.02.16.
 */
@Component(service=TenantSecurityService.class, name = "RAT - Tenant Security Interface", configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
public class DefaultTenantSecurityServiceImpl implements TenantSecurityService {

	private static final Logger logger = getLogger(DefaultTenantSecurityServiceImpl.class);

	private static final String GROUP_PATH_PREFIX = "/home/groups";

	@Reference
	private CreateTenantConfigService createTenantConfigService;
	
	@Reference
	private CreateTenantPageService tenantPageService;
	
	@Reference
	private CreateTenantGroupsService createTenantGroupService;

	@Reference
	private GroupManagerService groupManagerService;

	/**
	 * List with top level paths.
	 */
	public static final List<String> TOP_LEVEL_PATH_LIST = new ArrayList<>();
	static {
		TOP_LEVEL_PATH_LIST.add("/");
	}

	@Override
	public void handleGroupsAndACLs(List<String> resourcePaths, String path, int currentLevel, ResourceResolver resolver) {
		Map<GroupType, GroupWrapper> groupWrapperMap;
		Map<GroupType, GroupWrapper> topLevelWrapperMap = new HashMap<>();
		createGroupWrapper("", "/home/groups/toplevel", GroupType.TOPLEVEL_READER, topLevelWrapperMap, TOP_LEVEL_PATH_LIST, resolver);
		for (GroupWrapper topLevelWrapper : topLevelWrapperMap.values()) {
			logger.trace("Calling entitleGroup for toplevel groups.");
			entitleGroup(topLevelWrapper, true, resolver);
			addGroupToAEMStandardGroups(topLevelWrapper.getGroup(), resolver);
		}
		int offset = currentLevel - createTenantConfigService.getEndLevel();
		String newPath = path;
		List<String> newResourcePaths = new ArrayList<>();
		newResourcePaths.addAll(resourcePaths);
		int newCurrentLevel = Math.min(currentLevel, createTenantConfigService.getEndLevel());
		if (offset > 0) {
			logger.debug("Entry level is bigger than endLevel, computing endlevel paths to start processing there.");
			for (int counter = 0; counter < offset; counter++) {
				List<String> tempResourcePaths = new ArrayList<>();
				newPath = StringUtils.substringBeforeLast(newPath, "/");
				tempResourcePaths.addAll(newResourcePaths.stream().map(tempPath -> StringUtils.substringBeforeLast(tempPath, "/")).collect(Collectors.toList()));
				newResourcePaths.clear();
				newResourcePaths.addAll(tempResourcePaths);
			}
		}
		groupWrapperMap = createGroupsForPath(newPath, newResourcePaths, resolver);
		for (Map.Entry<GroupType, GroupWrapper> entry : groupWrapperMap.entrySet()) {
			logger.trace("Calling entitleGroup for group {}.", entry.getValue().getGroupId());
			entitleGroup(entry.getValue(), (createTenantGroupService.isReadInheritance() && entry.getKey() != GroupType.READ_INHERIT) || newCurrentLevel == createTenantConfigService.getEndLevel(), resolver);
		}
		checkGroupInheritance(groupWrapperMap, resourcePaths, resolver);
	}

	@Override public Map<GroupType, GroupWrapper> createGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver) {
		resolver.refresh();
		Map<GroupType, GroupWrapper> wrappedGroups = new HashMap<>();
		int level = ResourceHelper.getResourceLevel(path);
		// TODO: create inheritance group on parent level if readInherit is ON and then add created groups to it
		if (createTenantConfigService.isValidLevel(level)) {
			logger.trace("Start creating groups for path {} with level {}.", path, level);
			String groupPath = GROUP_PATH_PREFIX + path;

			for (Map.Entry<GroupType, String> mapEntry : createTenantGroupService.getLocalGroupNamesMap().entrySet()) {
				if (mapEntry.getKey() == GroupType.READ_INHERIT || !createTenantConfigService.isReadAccessLevel(level)) {
					logger.trace("Creating group {}.", mapEntry.getValue());
					createGroupWrapper(path, groupPath, mapEntry.getKey(), wrappedGroups, resourcePaths, resolver);
				}
			}
		} else {
			logger.info("Level {} is out of range - doing nothing.", level);
		}
		return wrappedGroups;
	}


	@Override
	public Map<GroupType, GroupWrapper> createGlobalGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver) {
		Map<GroupType, GroupWrapper> wrappedGroups = new HashMap<>();
		int level = ResourceHelper.getResourceLevel(path);
		if (createTenantConfigService.isValidLevel(level)) {
			logger.trace("Start creating groups for path {} with level {}.", path, level);
			for (Map.Entry<GroupType, String> mapEntry : createTenantGroupService.getGlobalGroupNamesMap().entrySet()) {
				logger.trace("Creating global group {}.", mapEntry.getValue());
				createGroupWrapper(path, "/home/groups/global", mapEntry.getKey(), wrappedGroups, resourcePaths, resolver);
			}
		} else {
			logger.info("Level {} is out of range - doing nothing.", level);
		}
		return wrappedGroups;
	}


	@Override public Group entitleGroup(GroupWrapper wrapper, boolean isReadInheritance, ResourceResolver resolver) {
		resolver.refresh();
		Session session = resolver.adaptTo(Session.class);
		switch (wrapper.getType()) {
			case GLOBAL_READER:
			case GLOBAL_EDITOR:
			case GLOBAL_PUBLISHER:
			case GLOBAL_SUPPORT:
			case TOPLEVEL_READER:
				logger.info("Set top level rules for {}.", wrapper.getGroupId());
				groupManagerService.setGroupACLs(wrapper.getGroup(), wrapper.getPathList(), wrapper.getRule(), session, true);
				break;
			case GLOBAL_USER_ADMIN:
				logger.trace("Set user/group rules for {}.", wrapper.getGroupId());
				if (wrapper.getGroupRule() != null) {
					List<String> groupPathsList = new ArrayList<>();
					groupPathsList.add("/home/groups");
					groupManagerService.setGroupACLs(wrapper.getGroup(), groupPathsList, wrapper.getGroupRule(), session, true);
				}
				if (wrapper.getUserRule() != null) {
					List<String> userPathList = new ArrayList<>();
					userPathList.add("/home/users");
					groupManagerService.setGroupACLs(wrapper.getGroup(), userPathList, wrapper.getUserRule(), session, true);
				}
				break;
			default:
				if (wrapper.getRule() != null) {
					logger.trace("Set rules for {}.", wrapper.getGroupId());
					int level = ResourceHelper.getResourceLevel(wrapper.getPathList().get(0));
					groupManagerService.setGroupACLs(wrapper.getGroup(), wrapper.getPathList(), wrapper.getRule(), session, isReadInheritance || level == createTenantConfigService.getEndLevel());
				}
				if (wrapper.getType() == GroupType.USER_ADMIN && wrapper.getGroupRule() != null) {
					logger.trace("Set group rules for {}.", wrapper.getGroupId());
					List<String> groupPathList = new ArrayList<>();
					groupPathList.add(StringUtils.replace(wrapper.getPathList().get(0), "/content/", "/home/groups/content/"));
					groupManagerService.setGroupACLs(wrapper.getGroup(), groupPathList, wrapper.getGroupRule(), session, true);
				}
				break;
		}
		return wrapper.getGroup();
	}

	@Override public void checkGroupInheritance(Map<GroupType, GroupWrapper> wrapperMap, List<String> resourcePaths, ResourceResolver resolver) {
		resolver.refresh();
		Session session = resolver.adaptTo(Session.class);
		Group parentReadInheritGroup = null;
		Group topLevelGroup = groupManagerService.getGroup(createTenantGroupService.getTopLevelReadersGroupName(), session);
		if (wrapperMap.isEmpty()) {
			logger.debug("Map is empty. Cannot do anything here.");
		} else {
			// first make sure that all parent groups exist.
			GroupWrapper firstMapper = wrapperMap.values().iterator().next();
			String path = firstMapper.getPathList().get(0);
			logger.debug("Checking parent groups for path {}.", path);
			int level = ResourceHelper.getResourceLevel(path);
			if (level > 1 && level >= createTenantConfigService.getStartLevel()) {
				String parentPath = StringUtils.substringBeforeLast(path, "/");
				List<String> parentResourcePaths = resourcePaths.stream().map(tempPath -> StringUtils.substringBeforeLast(tempPath, "/")).collect(Collectors.toList());
				Map<GroupType, GroupWrapper> parentGroupsMap = createGroupsForPath(parentPath, parentResourcePaths, resolver);
				if(parentGroupsMap.isEmpty()) {
					logger.trace("No relevant parent groups exist for path {}. Inheritance is ok.", path);
				} else {
					logger.trace("Checking ACL settings for parent groups ({})", path);
					for(GroupWrapper parentWrapper : parentGroupsMap.values()) {
						logger.trace("Calling entitleGroup for group {}.", parentWrapper.getGroupId());
						entitleGroup(parentWrapper, parentWrapper.getType() != GroupType.READ_INHERIT && createTenantGroupService.isReadInheritance(), resolver);
					}
					logger.trace("Checking inheritance on parent path {}", parentPath);
					if(parentGroupsMap.containsKey(GroupType.READ_INHERIT)) {
						parentReadInheritGroup = parentGroupsMap.get(GroupType.READ_INHERIT).getGroup();
					}
					checkGroupInheritance(parentGroupsMap, parentResourcePaths, resolver);
				}
				if(level == 2 || level == createTenantConfigService.getStartLevel()) {
					// create full access for administrators and deny all for everyone
					List<String> allPathsList = new ArrayList<>();
					allPathsList.add(path);
					allPathsList.addAll(resourcePaths);
					groupManagerService.setStartLevelNodesACLs(allPathsList, session, topLevelGroup);
					logger.debug("Create global groups now for path {}", path);

					Map<GroupType, GroupWrapper> globalGroupsWrapperMap = createGlobalGroupsForPath(path, resourcePaths, resolver);
					for(GroupWrapper parentWrapper : globalGroupsWrapperMap.values()) {
						logger.trace("Calling entitleGroup for group {}.", parentWrapper.getGroupId());
						entitleGroup(parentWrapper, createTenantGroupService.isReadInheritance(), resolver);
						addGlobalGroupToLocalGroup(parentWrapper.getGroup(), topLevelGroup, session);
					}
				}
			}
			addGroupsToParentGroups(wrapperMap, parentReadInheritGroup, path, resolver);
		}
	}

	/**
	 * Add groups to the respective parent groups. This method ensures for example that /content/b/a editors will have read access to /content/b and /content.
	 * @param wrapperMap			map with group wrappers
	 * @param parentReadersGroup	parent readers group
	 * @param path					path
	 * @param resolver				resource resolver
	 */
	private void addGroupsToParentGroups(Map<GroupType, GroupWrapper> wrapperMap, Group parentReadersGroup, String path, ResourceResolver resolver) {
		Session session = resolver.adaptTo(Session.class);
		Group topLevelGroup = groupManagerService.getGroup(createTenantGroupService.getTopLevelReadersGroupName(), session);
		int level = ResourceHelper.getResourceLevel(path);
		// now iterate the current groups, add them to the respective parent groups member list
		for (Map.Entry<GroupType, GroupWrapper> wrapperEntry : wrapperMap.entrySet()) {
			GroupWrapper wrapper = wrapperEntry.getValue();
			GroupType type = wrapperEntry.getKey();
			switch (type) {
				case NO_ACCESS:
					logger.trace("Nothing to do for group {}", wrapper.getGroupId());
					break;
				case READER:
				case EDITOR:
				case PUBLISHER:
				case READ_INHERIT:
					if (parentReadersGroup != null) {
						// all groups are member of the parent readers group - even true for publisher or editor groups.
						addGlobalGroupToLocalGroup(wrapper.getGroup(), parentReadersGroup, session);
					}
					if(level == 2 || level == createTenantConfigService.getStartLevel()) {
						logger.trace("Setting deny entry for toplevel and everyone on {} and all additional paths.", path);
						addGlobalGroupToLocalGroup(wrapper.getGroup(), topLevelGroup, session);
					}
					break;
				case USER_ADMIN:
				case GLOBAL_READER:
				case GLOBAL_EDITOR:
				case GLOBAL_PUBLISHER:
				case GLOBAL_USER_ADMIN:
				default:
					logger.debug("Nothing to do.");
			}
			try {
				resolver.commit();
			} catch (PersistenceException ex) {
				logger.error("Could not save group {}: {}", wrapper.getGroupId(), ex.getMessage());
			}
		}
	}

	@Override public void checkGroupInheritance(GroupWrapper wrapper, List<String> resourcePaths, ResourceResolver resolver) {
		Map<GroupType, GroupWrapper> map = new HashMap<>();
		map.put(wrapper.getType(), wrapper);
		checkGroupInheritance(map, resourcePaths, resolver);
	}

	/**
	 * Activates the service.
	 *
	 * @param context
	 *            ComponentContext
	 */
	@Activate
	public void activate(ComponentContext context) {
		logger.info("Activating Tenant Security API service.");
	}

	/**
	 * Deactivate the service.
	 *
	 * @param context component Context
	 */
	@Deactivate
	public void deactivate(ComponentContext context) {
		logger.info("Deactivating Tenant Security API service with context {}.", context.toString());
	}

	@Override
	public void createGroupWrapper(String contentPath, String groupPath, GroupType type, Map<GroupType, GroupWrapper> wrappedGroups, List<String> resourcePaths, ResourceResolver resolver) {
		Session session = resolver.adaptTo(Session.class);
		String groupName = createTenantGroupService.computeGroupName(type, type.isGlobalGroup() ? "" : contentPath);
		Group group = groupManagerService.createGroup(groupPath, groupName, session);
		logger.trace("Created group {} on path {}.", groupName, groupPath);
		List<String> allPaths = new ArrayList<>();
		if (StringUtils.isNotEmpty(contentPath)){
			allPaths.add(contentPath);
		}
		allPaths.addAll(resourcePaths);
		GroupWrapper wrapper = new GroupWrapper(group, type, allPaths);
		wrappedGroups.put(type, wrapper);
	}

	/**
	 * Add a global group as a member of a local group if the respective group exists in the map.
	 * @param globalGroup	global group
	 * @param localGroup	local group
	 * @param session valid session
	 * @return true if group has been added, false otherwise
	 */
	private boolean addGlobalGroupToLocalGroup(Group globalGroup, Group localGroup, Session session) {
		boolean returnValue = false;
		try {
			if (localGroup.isMember(globalGroup)) {
				logger.trace("Global group already is member of local group");
			} else {
				localGroup.addMember(globalGroup);
				returnValue = true;
				session.save();
			}
		} catch (RepositoryException ex) {
			logger.error("Cannot set group inheritance for global group: {}", ex.getMessage());
		}
		return returnValue;
	}

	@Override
	public void addGroupToAEMStandardGroups(Group group, ResourceResolver resolver) {
		try {
			Session session = resolver.adaptTo(Session.class);
			session.refresh(true);
			logger.info("Adding top level group to workflow users.");
			Group workflowUsers = groupManagerService.getGroup("workflow-users", session);
			workflowUsers.addMember(group);
			session.save();
		} catch (RepositoryException ex) {
			logger.error("Could not add group to AEM standard groups: {}", ex.getMessage());
		}
	}

}
