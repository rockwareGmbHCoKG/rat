package de.rockware.aem.rat.core.api.services;


import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfig;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfig;
import de.rockware.aem.rat.core.api.security.PrincipalRule;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.Resource;

import java.util.List;

import javax.jcr.Session;

/**
 * Creates / Deletes and updates groups.
 * Created by ogebert on 12.02.16.
 */
public interface GroupManagerService {

	/**
	 * Create the group for the given path.
	 * @param path	group path
	 * @param groupId	group id
	 * @param session valid jcr session
	 * @return	List with groups
	 */
	Group createGroup(String path, String groupId, Session session);

	/**
	 * Create the group(s) for the given path.
	 * @param path	group path
	 * @param groupIds	list with group ids
	 * @param session valid jcr session
	 * @return	List with groups
	 */
	List<Group> createGroups(String path, List<String> groupIds, Session session);

	/**
	 * Set the group acls.
	 * @param group	group
	 * @param  pathList list with paths
	 * @param rule principal rule
	 * @param session valid jcr session
	 * @param readInheritance if true, read access is inherited down the tree
	 */
	void setGroupACLs(Group group, List<String> pathList, PrincipalRule rule, Session session, boolean readInheritance);

	/**
	 * Adds an authorizable as group member.
	 * @param group				group to add the authorizable to
	 * @param authorizable		authorizable to add
	 * @param session valid jcr session
	 */
	void addMember(Group group, Authorizable authorizable, Session session);

	/**
	 * Get a group with the given ID.
	 * @param groupId	group id
	 * @param session	valid jcr session
	 * @return	group or null
	 */
	Group getGroup(String groupId, Session session);

	/**
	 * Create the permissions for start level groups - only needed if inheritance is set to "NO".
	 * @param pathList		list of paths tp set group permissions for
	 * @param session		valid session
	 * @param topLevelGroup	this is the top level group that loses access where the special groups start.
	 */
	void setStartLevelNodesACLs(List<String> pathList, Session session, Group topLevelGroup);


	/**
	 * Cleanup groups.
	 * @param session valid jcr session
	 */
	void cleanup(Session session);

	/**
	 * Sort ACE lists so that all deny rules are at the beginning of the list.
	 * @param path		path to get the ace list from
	 * @param session	valid session
	 */
	void reArrangeAcls(String path, Session session);

	/**
	 * Get a valid tenant config object.
	 * @param resource	current resource
	 * @return	a tenant config or null
	 */
	TenantRATConfig getTenantRATConfig(Resource resource);

	/**
	 * Get the global config.
	 * @param resource	current resource
	 * @return	global config
	 */
	GlobalRATConfig getGlobalRATConfig(Resource resource);
}
