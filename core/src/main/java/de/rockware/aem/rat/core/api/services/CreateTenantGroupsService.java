package de.rockware.aem.rat.core.api.services;


import de.rockware.aem.rat.core.impl.config.GroupType;

import java.util.Map;

/**
 * @author diwakar
 *
 * This Interface is used to configure the all types of groups like reader, editor, publisher.
 */
public interface CreateTenantGroupsService {

	
    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateReadersGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getReadersGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateGlobalReadersGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGlobalReadersGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateEditorsGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getEditorsGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateGlobalEditorsGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGlobalEditorsGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreatePublishersGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getPublishersGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateGlobalPublishersGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGlobalPublishersGroupName();

	/**
	 * Getter for this param
	 * @return	param value
	 */
	boolean isCreateUserAdminsGroup();

	/**
	 * Getter for this param
	 * @return	param value
	 */
	String getUserAdminsGroupName();

	/**
	 * Getter for this param
	 * @return	param value
	 */
	boolean isCreateGlobalUserAdminsGroup();

	/**
	 * Getter for this param
	 * @return	param value
	 */
	String getGlobalUserAdminsGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	boolean isCreateGlobalSupportGroup();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGlobalSupportGroupName();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGroupNamePrefix();

    /**
     * Getter for this param
     * @return	param value
     */
	String getGroupNameSuffix();

	/**
	 * Getter for the group name seperator.
	 * @return group name separator
	 */
	String getGroupNameSeparator();

	/**
	 * Getter for read inheritance.
	 * @return	read inheritance
	 */
	boolean isReadInheritance();

	/**
	 * Getter for toplevel groupname.
	 * @return	group name for toplevel readers.
	 */
	String getTopLevelReadersGroupName();
	
	/**
	 * Computes a group name.
	 * @param type group type
	 * @param path	group path
	 * @return	group name
	 */
	String computeGroupName(GroupType type, String path);

	/**
	 * Get all group names of global groups that are configured to be created.
	 * @return map with groups
	 */
	Map<GroupType, String> getGlobalGroupNamesMap();

	/**
	 * Get all group names of local groups that are configured to be created.
	 * @return map with groups
	 */
	Map<GroupType, String> getLocalGroupNamesMap();

}
