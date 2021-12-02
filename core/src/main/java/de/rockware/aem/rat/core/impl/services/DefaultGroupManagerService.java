package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.caconfig.TenantRATConfig;
import de.rockware.aem.rat.core.api.security.Permission;
import de.rockware.aem.rat.core.api.security.PrincipalRule;
import de.rockware.aem.rat.core.api.services.GroupManagerService;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Default group manager implementation.
 */
@Component(name = "Rockware default GroupManager Service", immediate = true)
public class DefaultGroupManagerService implements GroupManagerService {

	private static final Logger logger = getLogger(DefaultGroupManagerService.class);

	/**
	 * Permissions <code>RULE_EVERYONE</code> for everyone.
	 */
	private static final PrincipalRule RULE_EVERYONE = new PrincipalRule("", EveryonePrincipal.NAME, false, Permission.ALL);

	private static final PrincipalRule RULE_ADMINISTRATORS = new PrincipalRule("", "administrators", true, Permission.ALL);

	/** path home */
	private static final String PATH_HOME = "/home";
	/** path home groups */
	private static final String PATH_HOME_GROUPS = "/home/groups";

	@Override
	public Group createGroup(String path, String groupId, Session session) {
		List<String> list = new ArrayList<>();
		list.add(groupId);
		return createGroups(path, list, session).get(0);
	}

	@Override
	public List<Group> createGroups(String path, List<String> groupIds, Session session) {
		logger.trace("Start creating groups.");
		List<Group> groupList = new ArrayList<>();
		try {
			UserManager userManager = AccessControlUtil.getUserManager(session);
			for(String groupId : groupIds) {
				logger.trace("Try to get data to create group {}.", groupId);
				Authorizable authorizable = userManager.getAuthorizable(groupId);
				if(authorizable == null) {
					StringBuilder homePath = new StringBuilder(path);
					if(!path.startsWith(PATH_HOME)) {
						homePath.insert(0, PATH_HOME_GROUPS);
					}
					logger.debug("Creating group with id: {}, path: {}", new Object[] { groupId, homePath.toString() });
					Group group = userManager.createGroup(groupId, new PrincipalImpl(groupId), homePath.toString());
					session.save();
					groupList.add(group);
				} else if (authorizable.isGroup()) {
					logger.debug("Group with id {} already exists.", groupId);
					groupList.add((Group) authorizable);
				} else {
					logger.error("Authorizable with id {} exists but is a user, not a group.", groupId);
				}
			}
		} catch (RepositoryException ex) {
			logger.error("Repository Exception: {}", ex.getMessage());
		}
		return groupList;
	}

	@Override
	public Group getGroup(String groupId, Session session) {
		Group group = null;
		try {
			UserManager userManager = AccessControlUtil.getUserManager(session);
			Authorizable authorizable = userManager.getAuthorizable(groupId);
			if (authorizable!=null){
				if (authorizable.isGroup()) {
					group = (Group) userManager.getAuthorizable(groupId);
				} else {
					logger.info("Authorizable {} is no group. Cannot convert.", authorizable.getID());
				}
			}
		} catch (RepositoryException ex) {
			logger.error("Repository Exception: {}", ex.getMessage());
		}
		return group;
	}

	@Override
	public void setGroupACLs(Group group, List<String> pathList, PrincipalRule rule, Session session, boolean readInheritance) {
		Map<Group, PrincipalRule> groupRuleMap = new LinkedHashMap<>();
		groupRuleMap.put(group, rule);
		doSetGroupACLs(groupRuleMap, pathList, session, readInheritance);
	}

	@Override
	public void addMember(Group group, Authorizable authorizable, Session session) {
		try {
			group.addMember(authorizable);
			session.save();
		} catch (RepositoryException ex) {
			logger.error("Cannot add authorizable {} to group {}: {}", new Object[] {authorizable, group, ex.getMessage()});
		}
	}

	@Override
	public void cleanup(Session session) {
		// TODO: check existing groups, add missing groups, delete obsolete groups
	}

	/**
	 * Activates the service.
	 *
	 * @param context
	 *            ComponentContext
	 */
	@Activate
	public void activate(ComponentContext context) {
		logger.info("Activating Default Group Manager service.");
	}

	/**
	 * Deactivate the service.
	 *
	 * @param context component Context
	 */
	@Deactivate
	public void deactivate(ComponentContext context) {
		logger.info("Deactivating Default Group Manager service with context {}.", context.toString());
	}

	@Override
	public void setStartLevelNodesACLs(List<String> pathList, Session session, Group topLevelGroup) {
		try {
			logger.trace("Creating start level ACL entries.");
			UserManager userManager = AccessControlUtil.getUserManager(session);
			Map<Group, PrincipalRule> groupRulesMap = new LinkedHashMap<>();
			Group everyone = (Group) userManager.getAuthorizable(EveryonePrincipal.NAME);
			groupRulesMap.put(everyone, RULE_EVERYONE);
			groupRulesMap.put(topLevelGroup, RULE_EVERYONE);
			Group administrators = (Group) userManager.getAuthorizable("administrators");
			groupRulesMap.put(administrators, RULE_ADMINISTRATORS);
			doSetGroupACLs(groupRulesMap, pathList, session, true);
			session.save();
		} catch (RepositoryException ex) {
			logger.error("Cannot set acl for start level nodes: {}", ex.getMessage());
		}
	}

	/**
	 * Create the permissions.
	 * @param groupRulesMap		map with groups to set permissions for
	 * @param pathList			list of paths tp set group permissions for
	 * @param session			valid session
	 * @param readInheritance 	true if access rights include all children.
	 */
	private void doSetGroupACLs(Map<Group, PrincipalRule> groupRulesMap, List<String> pathList, Session session, boolean readInheritance) {
		try {
			logger.trace("Processing acls for a set of groups and paths.");
			JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) session.getAccessControlManager();
			ValueFactory vf = session.getValueFactory();
			for(String path : pathList) {
				logger.trace("Starting with path {}.", path);
				JackrabbitAccessControlList controlList = AccessControlUtils.getAccessControlList(session, path);
				for(Map.Entry<Group, PrincipalRule> entry : groupRulesMap.entrySet()) {
					Group group = entry.getKey();
					PrincipalRule rule = entry.getValue();
					Principal definedPrincipal = group.getPrincipal();
					logger.trace("Building rules for group {}.", group.getID());
					for(Permission permission : rule.getPermissions()) {
						Map<String, Value> restrictions = new HashMap<>();
						Privilege[] definedPrivileges = AccessControlUtils.privilegesFromNames(session, permission.getPrivileges());
						if (!readInheritance) {
							restrictions.put("rep:glob", vf.createValue(""));
						}
						controlList.addEntry(definedPrincipal, definedPrivileges, rule.isAllowRule(), restrictions);
						if (!readInheritance) {
							restrictions.put("rep:glob", vf.createValue("/jcr:*"));
							controlList.addEntry(definedPrincipal, definedPrivileges, rule.isAllowRule(), restrictions);
						}
					}
				}
				acMgr.setPolicy(controlList.getPath(), controlList);
				logger.trace("********************************************************");
				if (logger.isTraceEnabled()) {
					logger.trace("ControlList order before sorting it:");
					for(AccessControlEntry entry : controlList.getAccessControlEntries()) {
						logger.trace(entry.getPrincipal().getName());
					}
				}
				reArrangeAcls(path, session);
				if (logger.isTraceEnabled()) {
					logger.trace("ControlList order after sorting it:");
					for(AccessControlEntry entry : AccessControlUtils.getAccessControlList(session, path).getAccessControlEntries()) {
						logger.trace(entry.getPrincipal().getName());
					}
				}
				logger.trace("********************************************************");
			}
			logger.trace("Finished processing.");
		} catch (RepositoryException ex) {
			logger.error("Repository Exception: {}", ex.getMessage());
		}
	}

	/** this code is not needed anymore */
	public void sortACLEntries(String path, Session session) {
		try {
			session.refresh(true);
			JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) session.getAccessControlManager();
			logger.trace("Sorting acl-entries for path {}.", path);
			JackrabbitAccessControlList controlList = AccessControlUtils.getAccessControlList(session, path);
			AccessControlEntry[] entries = controlList.getAccessControlEntries();
			List<AccessControlEntry> denyEntryList = new ArrayList<>();
			AccessControlEntry allowEntry = null;
			for(AccessControlEntry entry : entries) {
				if(((JackrabbitAccessControlEntry) entry).isAllow()) {
					if(allowEntry == null) {
						allowEntry = entry;
						logger.trace("First allow entry in list is: {}.", allowEntry.getPrincipal().getName());
					}
				} else {
					denyEntryList.add(entry);
					logger.trace("Added deny entry {}.", entry.getPrincipal().getName());
				}
			}
			for(AccessControlEntry entry : denyEntryList) {
				logger.debug("Inserting {} in front of {}.", entry.getPrincipal().getName(), allowEntry == null ? "(NOTHING)" : allowEntry.getPrincipal().getName());
				controlList.orderBefore(entry, allowEntry);
			}
			acMgr.setPolicy(controlList.getPath(), controlList);
		} catch (RepositoryException ex) {
			logger.error("Cannot sort ACL Entries: {}.", ex.getMessage());
		}
	}



	@Override
	public void reArrangeAcls(String path, Session session){
		try {
            session.refresh(true);
            JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) session.getAccessControlManager();
            logger.trace("Sorting acl-entries for path {}.", path);
            JackrabbitAccessControlList controlList = AccessControlUtils.getAccessControlList(session, path);
            AccessControlEntry[] entries = controlList.getAccessControlEntries();
            List<AccessControlEntry> denyEntryList = new ArrayList<>();
            int counter = 0;
            for(AccessControlEntry entry : entries) {
            	counter++;
            	logger.trace("Checking entry ().", entry.getPrincipal().getName());
                if(!((JackrabbitAccessControlEntry) entry).isAllow()) {
					logger.trace("Entry is Deny-Entry.", entry.getPrincipal().getName());
                	denyEntryList.add(entry);
                } else {
					logger.trace("Entry is Allow-Entry.", entry.getPrincipal().getName());
				}
            }
            for(AccessControlEntry entry : denyEntryList) {
                logger.debug("Inserting {} in front of {}.", entry.getPrincipal().getName(),
							 controlList.getAccessControlEntries()[0].getPrincipal().getName());
                controlList.orderBefore(entry, controlList.getAccessControlEntries()[0]);
            }
            acMgr.setPolicy(controlList.getPath(), controlList);
        } catch (RepositoryException ex) {
            logger.error("Cannot sort ACL Entries: {}.", ex.getMessage());
        }
	}

	/**
	 * Get the token configuration.
	 * @param resource  current resource
	 * @return  token config
	 */
	@Override
	public TenantRATConfig getTenantRATConfig(Resource resource) {
		TenantRATConfig pConfig = null;
		if (resource != null) {
			ConfigurationBuilder cBuilder = resource.adaptTo(ConfigurationBuilder.class);
			if (cBuilder != null) {
				pConfig = cBuilder.as(TenantRATConfig.class);
			}
		}
		return pConfig;
	}

}
