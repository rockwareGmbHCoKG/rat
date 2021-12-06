package de.rockware.aem.rat.core.api.services;

import de.rockware.aem.rat.core.impl.config.GroupType;
import de.rockware.aem.rat.core.api.security.GroupWrapper;
import de.rockware.aem.rat.core.impl.config.RichConfiguration;

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
     * @param resourcePaths    all the resources that need acls
     * @param richConfig            path that has been created / modified
     * @param currentLevel    current level in the node tree
     * @param resolver        resource resolver
     */
	void handleGroupsAndACLs(List<String> resourcePaths, RichConfiguration richConfig, int currentLevel, ResourceResolver resolver);

	/**
	 * Create all the groups for a given path.
     * @param path        path
     * @param resourcePaths paths of all the resources that have been created automatically.
     * @param resolver        resource resolver
     * @param richConfig	configuration
     */
	Map<GroupType, GroupWrapper> createGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Create all the global groups for a given path.
	 * @param path		path
	 * @param resourcePaths paths of all the resources that have been created automatically.
	 * @param resolver		resource resolver
	 * @param richConfig 	configuration
	 */
	Map<GroupType, GroupWrapper> createGlobalGroupsForPath(String path, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Set group entitlements.
	 * @param wrapper	group wrapper
	 * @param readInheritance if true, read access will be inherited
	 * @param resolver		resource resolver
	 * @param richConfig 	configuration
	 * @return	modified group
	 */
	Group entitleGroup(GroupWrapper wrapper, boolean readInheritance, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Check if groups are members of their respective parent groups, if not add them to the member list.
	 * @param wrapperList	list of groups
	 * @param resourcePaths  paths to all modified resources
	 * @param resolver		resource resolver
	 * @param richConfig 	configuration
	 */
	void checkGroupInheritance(Map<GroupType, GroupWrapper> wrapperList, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Check if group is a member of its respective parent group, if not add it to the member list.
	 * @param wrapper	group wrapper
	 * @param resourcePaths  paths to all modified resources
	 * @param resolver		resource resolver
	 * @param richConfig 	configuration
	 */
	void checkGroupInheritance(GroupWrapper wrapper, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Create a group wrapper with the given data.
	 * @param contentPath	content path of newly created page
	 * @param groupPath	path where group should be stored
	 * @param type	group type
	 * @param wrappedGroups Map to add the group wrapper to
	 * @param resourcePaths path of all resources that have been created / deleted / moved
	 * @param resolver		resource resolver
	 * @param richConfig 	configuration
	 */
	void createGroupWrapper(String contentPath, String groupPath, GroupType type, Map<GroupType, GroupWrapper> wrappedGroups, List<String> resourcePaths, ResourceResolver resolver, RichConfiguration richConfig);

	/**
	 * Add the given group to the AEM standard groups.
	 * @param group	group to add
	 * @param resolver		resource resolver
	 */
	void addGroupToAEMStandardGroups(Group group, ResourceResolver resolver);
}
