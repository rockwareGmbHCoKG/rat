package de.rockware.aem.rat.core.impl.services;


import de.rockware.aem.rat.core.api.resource.ResourceHelper;
import de.rockware.aem.rat.core.api.config.GroupType;
import de.rockware.aem.rat.core.api.security.GroupWrapper;
import de.rockware.aem.rat.core.api.services.TenantSecurityService;
import de.rockware.aem.rat.core.api.services.GroupManagerService;
import de.rockware.aem.rat.core.api.config.RichConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import lombok.extern.slf4j.Slf4j;

/**
 * {@inheritDoc}
 */
@Component(service=TenantSecurityService.class, name = "RATTenantSecurityInterface", configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
@Slf4j
public class DefaultTenantSecurityServiceImpl implements TenantSecurityService {

	private static final String GROUP_PATH_PREFIX = "/home/groups";

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
	public void handleGroupsAndACLs(List<String> resourcePaths, RichConfiguration richConfig, int currentLevel, ResourceResolver resolver) {
		Map<GroupType, GroupWrapper> groupWrapperMap;
		Map<GroupType, GroupWrapper> topLevelWrapperMap = new HashMap<>();
		createGroupWrapper("", "/home/groups/toplevel", GroupType.TOPLEVEL_READER, topLevelWrapperMap, TOP_LEVEL_PATH_LIST, resolver, richConfig);
		for (GroupWrapper topLevelWrapper : topLevelWrapperMap.values()) {
			log.trace("Calling entitleGroup for toplevel groups.");
			entitleGroup(topLevelWrapper, true, resolver, richConfig);
			addGroupToAEMStandardGroups(topLevelWrapper.getGroup(), resolver);
		}
		int offset = currentLevel - richConfig.getEndLevel();
		String newPath = richConfig.getPath();
		List<String> newResourcePaths = new ArrayList<>(resourcePaths);
		int newCurrentLevel = Math.min(currentLevel, richConfig.getEndLevel());
		if (offset > 0) {
			log.debug("Entry level is bigger than endLevel, computing endlevel paths to start processing there.");
			for (int counter = 0; counter < offset; counter++) {
				List<String> tempResourcePaths = new ArrayList<>(newResourcePaths.stream().map(tempPath -> StringUtils.substringBeforeLast(tempPath, "/")).collect(Collectors.toList()));
				newPath = StringUtils.substringBeforeLast(newPath, "/");
				newResourcePaths.clear();
				newResourcePaths.addAll(tempResourcePaths);
			}
		}
		groupWrapperMap = createGroupsForPath(newPath, newResourcePaths, resolver, richConfig);
		for (Map.Entry<GroupType, GroupWrapper> entry : groupWrapperMap.entrySet()) {
			log.trace("Calling entitleGroup for group {}.", entry.getValue().getGroupId());
			entitleGroup(entry.getValue(), (richConfig.isEnableReadInheritance() && entry.getKey() != GroupType.READ_INHERIT) || newCurrentLevel == richConfig.getEndLevel(), resolver, richConfig);
		}
		checkGroupInheritance(groupWrapperMap, resourcePaths, resolver, richConfig);
	}

	@Override
	public Map<GroupType, GroupWrapper> createGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig) {
		resolver.refresh();
		Map<GroupType, GroupWrapper> wrappedGroups = new HashMap<>();
		int level = ResourceHelper.getResourceLevel(path);
		// TODO: create inheritance group on parent level if readInherit is ON and then add created groups to it
		if (richConfig.isValidLevel(level)) {
			log.trace("Start creating groups for path {} with level {}.", path, level);
			String groupPath = GROUP_PATH_PREFIX + path;

			for (Map.Entry<GroupType, String> mapEntry : richConfig.getGroupMap().entrySet()) {
				GroupType currentType = mapEntry.getKey();
				if (currentType.isLocalGroup() && (currentType == GroupType.READ_INHERIT || !richConfig.isReadAccessLevel(level))) {
					log.trace("Creating group {}.", mapEntry.getValue());
					createGroupWrapper(path, groupPath, mapEntry.getKey(), wrappedGroups, resourcePaths, resolver, richConfig);
				}
			}
		} else {
			log.info("Level {} is out of range - doing nothing.", level);
		}
		return wrappedGroups;
	}


	@Override
	public Map<GroupType, GroupWrapper> createGlobalGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig) {
		Map<GroupType, GroupWrapper> wrappedGroups = new HashMap<>();
		int level = ResourceHelper.getResourceLevel(path);
		if (richConfig.isValidLevel(level)) {
			log.trace("Start creating groups for path {} with level {}.", path, level);
			for (Map.Entry<GroupType, String> mapEntry : richConfig.getGroupMap().entrySet()) {
				GroupType currentType = mapEntry.getKey();
				if (currentType.isGlobalGroup()) {
					log.trace("Creating global group {}.", mapEntry.getValue());
					createGroupWrapper(path, "/home/groups/global", mapEntry.getKey(), wrappedGroups, resourcePaths, resolver, richConfig);
				}
			}
		} else {
			log.info("Level {} is out of range - doing nothing.", level);
		}
		return wrappedGroups;
	}

	@Override
	public Group entitleGroup(GroupWrapper wrapper, boolean isReadInheritance, ResourceResolver resolver, RichConfiguration richConfig) {
		resolver.refresh();
		Session session = resolver.adaptTo(Session.class);
		switch (wrapper.getType()) {
			case GLOBAL_READER:
			case GLOBAL_EDITOR:
			case GLOBAL_PUBLISHER:
			case GLOBAL_SUPPORT:
			case TOPLEVEL_READER:
				log.info("Set top level rules for {}.", wrapper.getGroupId());
				groupManagerService.setGroupACLs(wrapper.getGroup(), wrapper.getPathList(), wrapper.getRule(), session, true);
				break;
			case GLOBAL_USER_ADMIN:
				log.trace("Set user/group rules for {}.", wrapper.getGroupId());
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
					log.trace("Set rules for {}.", wrapper.getGroupId());
					int level = ResourceHelper.getResourceLevel(wrapper.getPathList().get(0));
					groupManagerService.setGroupACLs(wrapper.getGroup(), wrapper.getPathList(), wrapper.getRule(), session, isReadInheritance || level == richConfig.getEndLevel());
				}
				if (wrapper.getType() == GroupType.USER_ADMIN && wrapper.getGroupRule() != null) {
					log.trace("Set group rules for {}.", wrapper.getGroupId());
					List<String> groupPathList = new ArrayList<>();
					groupPathList.add(StringUtils.replace(wrapper.getPathList().get(0), "/content/", "/home/groups/content/"));
					groupManagerService.setGroupACLs(wrapper.getGroup(), groupPathList, wrapper.getGroupRule(), session, true);
				}
				break;
		}
		return wrapper.getGroup();
	}

	@Override
	public void checkGroupInheritance(Map<GroupType, GroupWrapper> wrapperMap, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig) {
		resolver.refresh();
		Session session = resolver.adaptTo(Session.class);
		Group parentReadInheritGroup = null;
		Group topLevelGroup = groupManagerService.getGroup(richConfig.getGroupMap().get(GroupType.TOPLEVEL_READER), session);
		if (wrapperMap.isEmpty()) {
			log.debug("Map is empty. Cannot do anything here.");
		} else {
			// first make sure that all parent groups exist.
			GroupWrapper firstMapper = wrapperMap.values().iterator().next();
			String path = firstMapper.getPathList().get(0);
			log.debug("Checking parent groups for path {}.", path);
			int level = ResourceHelper.getResourceLevel(path);
			if (level > 1 && level >= richConfig.getStartLevel()) {
				String parentPath = StringUtils.substringBeforeLast(path, "/");
				List<String> parentResourcePaths = resourcePaths.stream().map(tempPath -> StringUtils.substringBeforeLast(tempPath, "/")).collect(Collectors.toList());
				Map<GroupType, GroupWrapper> parentGroupsMap = createGroupsForPath(parentPath, parentResourcePaths, resolver, richConfig);
				if(parentGroupsMap.isEmpty()) {
					log.trace("No relevant parent groups exist for path {}. Inheritance is ok.", path);
				} else {
					log.trace("Checking ACL settings for parent groups ({})", path);
					for(GroupWrapper parentWrapper : parentGroupsMap.values()) {
						log.trace("Calling entitleGroup for group {}.", parentWrapper.getGroupId());
						entitleGroup(parentWrapper, parentWrapper.getType() != GroupType.READ_INHERIT && richConfig.isEnableReadInheritance(), resolver, richConfig);
					}
					log.trace("Checking inheritance on parent path {}", parentPath);
					if(parentGroupsMap.containsKey(GroupType.READ_INHERIT)) {
						parentReadInheritGroup = parentGroupsMap.get(GroupType.READ_INHERIT).getGroup();
					}
					checkGroupInheritance(parentGroupsMap, parentResourcePaths, resolver, richConfig);
				}
				if(level == 2 || level == richConfig.getStartLevel()) {
					// create full access for administrators and deny all for everyone
					List<String> allPathsList = new ArrayList<>();
					allPathsList.add(path);
					allPathsList.addAll(resourcePaths);
					groupManagerService.setStartLevelNodesACLs(allPathsList, session, topLevelGroup);
					log.debug("Create global groups now for path {}", path);

					Map<GroupType, GroupWrapper> globalGroupsWrapperMap = createGlobalGroupsForPath(path, resourcePaths, resolver, richConfig);
					for(GroupWrapper parentWrapper : globalGroupsWrapperMap.values()) {
						log.trace("Calling entitleGroup for group {}.", parentWrapper.getGroupId());
						entitleGroup(parentWrapper, richConfig.isEnableReadInheritance(), resolver, richConfig);
						addGlobalGroupToLocalGroup(parentWrapper.getGroup(), topLevelGroup, session);
					}
				}
			}
			addGroupsToParentGroups(wrapperMap, parentReadInheritGroup, path, resolver, richConfig);
		}
	}

	/**
	 * Add groups to the respective parent groups. This method ensures for example that /content/b/a editors will have read access to /content/b and /content.
	 * @param wrapperMap			map with group wrappers
	 * @param parentReadersGroup	parent readers group
	 * @param path					path
	 * @param resolver				resource resolver
	 * @param richConfig 			configuration
	 */
	private void addGroupsToParentGroups(Map<GroupType, GroupWrapper> wrapperMap, Group parentReadersGroup, String path, ResourceResolver resolver, RichConfiguration richConfig) {
		Session session = resolver.adaptTo(Session.class);
		Group topLevelGroup = groupManagerService.getGroup(richConfig.getGroupMap().get(GroupType.TOPLEVEL_READER), session);
		int level = ResourceHelper.getResourceLevel(path);
		// now iterate the current groups, add them to the respective parent groups member list
		for (Map.Entry<GroupType, GroupWrapper> wrapperEntry : wrapperMap.entrySet()) {
			GroupWrapper wrapper = wrapperEntry.getValue();
			GroupType type = wrapperEntry.getKey();
			switch (type) {
				case NO_ACCESS:
					log.trace("Nothing to do for group {}", wrapper.getGroupId());
					break;
				case READER:
				case EDITOR:
				case PUBLISHER:
				case READ_INHERIT:
					if (parentReadersGroup != null) {
						// all groups are member of the parent readers group - even true for publisher or editor groups.
						addGlobalGroupToLocalGroup(wrapper.getGroup(), parentReadersGroup, session);
					}
					if(level == 2 || level == richConfig.getStartLevel()) {
						log.trace("Setting deny entry for toplevel and everyone on {} and all additional paths.", path);
						addGlobalGroupToLocalGroup(wrapper.getGroup(), topLevelGroup, session);
					}
					break;
				case USER_ADMIN:
				case GLOBAL_READER:
				case GLOBAL_EDITOR:
				case GLOBAL_PUBLISHER:
				case GLOBAL_USER_ADMIN:
				default:
					log.debug("Nothing to do.");
			}
			try {
				resolver.commit();
			} catch (PersistenceException ex) {
				log.error("Could not save group {}: {}", wrapper.getGroupId(), ex.getMessage());
			}
		}
	}

	@Override
	public void checkGroupInheritance(GroupWrapper wrapper, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig) {
		Map<GroupType, GroupWrapper> map = new HashMap<>();
		map.put(wrapper.getType(), wrapper);
		checkGroupInheritance(map, resourcePaths, resolver, richConfig);
	}

	/**
	 * Activates the service.
	 *
	 * @param context
	 *            ComponentContext
	 */
	@Activate
	public void activate(ComponentContext context) {
		log.info("Activating Tenant Security API service.");
	}

	/**
	 * Deactivate the service.
	 *
	 * @param context component Context
	 */
	@Deactivate
	public void deactivate(ComponentContext context) {
		log.info("Deactivating Tenant Security API service with context {}.", context.toString());
	}

	@Override
	public void createGroupWrapper(String contentPath, String groupPath, GroupType type, Map<GroupType, GroupWrapper> wrappedGroups, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig) {
		if (resolver == null || type == null || wrappedGroups == null) {
			log.info("Null values not allowed.");
			throw new IllegalArgumentException("Null values not allowed.");
		} else {
			Session session = resolver.adaptTo(Session.class);
			String groupName = computeGroupName(type, type.isGlobalGroup() ? "" : contentPath, richConfig);
			Group group = groupManagerService.createGroup(groupPath, groupName, session);
			if (group != null) {
				log.trace("Created group {} on path {}.", groupName, groupPath);
				List<String> allPaths = new ArrayList<>();
				if (StringUtils.isNotEmpty(contentPath)) {
					allPaths.add(contentPath);
				}
				if (resourcePaths != null) {
					allPaths.addAll(resourcePaths);
				}
				GroupWrapper wrapper = new GroupWrapper(group, type, allPaths);
				wrappedGroups.put(type, wrapper);
			} else {
				log.info("Group could not be created.");
			}
		}
	}

	/**
	 * Add a global group as a member of a local group if the respective group exists in the map.
	 * @param globalGroup	global group
	 * @param localGroup	local group
	 * @param session valid session
	 */
	private void addGlobalGroupToLocalGroup(Group globalGroup, Group localGroup, Session session) {
		try {
			if (localGroup.isMember(globalGroup)) {
				log.trace("Global group already is member of local group");
			} else {
				localGroup.addMember(globalGroup);
				session.save();
			}
		} catch (RepositoryException ex) {
			log.error("Cannot set group inheritance for global group: {}", ex.getMessage());
		}
	}

	@Override
	public void addGroupToAEMStandardGroups(Group group, ResourceResolver resolver) {
		try {
			Session session = resolver.adaptTo(Session.class);
			if (session != null) {
				session.refresh(true);
				log.info("Adding top level group to workflow users.");
				Group workflowUsers = groupManagerService.getGroup("workflow-users", session);
				if (workflowUsers != null) {
					workflowUsers.addMember(group);
					session.save();
				} else {
					log.info("Group workflow-users has not been found. Cannot add {}.", group.getID());
				}
			}
		} catch (RepositoryException ex) {
			log.error("Could not add group to AEM standard groups: {}", ex.getMessage());
		}
	}

	@Override
	public String computeGroupName(GroupType type, String path, RichConfiguration richConfig) {
		if (type == null || richConfig == null) {
			throw new IllegalArgumentException("Type and richConfig must not be null.");
		} else {
			StringBuilder groupName = new StringBuilder();
			if (StringUtils.isNotEmpty(path)) {
				if (!StringUtils.isEmpty(richConfig.getGroupNamePrefix())) {
					groupName.append(richConfig.getGroupNamePrefix()).append(".");
				}
				String groupPath = StringUtils.replace(StringUtils.substringAfter(path, "/content/"), "/", richConfig.getGroupNameSeperator());
				groupName.append(groupPath).append(".");
			} else {
				log.trace("Path is empty - will return standard group name for type {}.", type.name());
			}
			groupName.append(richConfig.getGroupMap().get(type));
			return groupName.toString();
		}
	}

}
