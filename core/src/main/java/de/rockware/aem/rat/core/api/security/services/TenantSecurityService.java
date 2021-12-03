package de.rockware.aem.rat.core.api.security.services;

import de.rockware.aem.rat.core.impl.config.GroupType;
import de.rockware.aem.rat.core.api.security.GroupWrapper;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;

/**
 * Deal with de.rockware.aem.tenant groups, acls and more.
 * Created by ogebert on 14.02.16.
 */
public interface TenantSecurityService {

	/**
	 * Create groups, create and update acls, check group inheritance and more.
	 * @param resourcePaths	all the resources that need acls
	 * @param path			path that has been created / modified
	 * @param currentLevel	current level in the node tree
	 * @param resolver		resource resolver
	 */
	void handleGroupsAndACLs(List<String> resourcePaths, String path, int currentLevel, ResourceResolver resolver);

	/**
	 * Create all the groups for a given path.
	 * @param path		path
	 * @param resourcePaths paths of all the resources that have been created automatically.
	 * @param resolver		resource resolver
	 */
	Map<GroupType, GroupWrapper> createGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver);

	/**
	 * Create all the global groups for a given path.
	 * @param path		path
	 * @param resourcePaths paths of all the resources that have been created automatically.
	 * @param resolver		resource resolver
	 */
	Map<GroupType, GroupWrapper> createGlobalGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver);

	/**
	 * Set group entitlements.
	 * @param wrapper	group wrapper
	 * @param readInheritance if true, read access will be inherited
	 * @param resolver		resource resolver
	 * @return	modified group
	 */
	Group entitleGroup(GroupWrapper wrapper, boolean readInheritance, ResourceResolver resolver);

	/**
	 * Check if groups are members of their respective parent groups, if not add them to the member list.
	 * @param wrapperList	list of groups
	 * @param resourcePaths  paths to all modified resources
	 * @param resolver		resource resolver
	 */
	void checkGroupInheritance(Map<GroupType, GroupWrapper> wrapperList, List<String> resourcePaths, ResourceResolver resolver);

	/**
	 * Check if group is a member of its respective parent group, if not add it to the member list.
	 * @param wrapper	group wrapper
	 * @param resourcePaths  paths to all modified resources
	 * @param resolver		resource resolver
	 */
	void checkGroupInheritance(GroupWrapper wrapper, List<String> resourcePaths, ResourceResolver resolver);

	/**
	 * Create a group wrapper with the given data.
	 * @param contentPath	content path of newly created page
	 * @param groupPath	path where group should be stored
	 * @param type	group type
	 * @param wrappedGroups Map to add the group wrapper to
	 * @param resourcePaths path of all resources that have been created / deleted / moved
	 * @param resolver		resource resolver
	 */
	void createGroupWrapper(String contentPath, String groupPath, GroupType type, Map<GroupType, GroupWrapper> wrappedGroups, List<String> resourcePaths, ResourceResolver resolver);

	/**
	 * Add the given group to the AEM standard groups.
	 * @param group	group to add
	 * @param resolver		resource resolver
	 */
	void addGroupToAEMStandardGroups(Group group, ResourceResolver resolver);
}
