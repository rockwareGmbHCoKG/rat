package de.rockware.aem.rat.core.api.security;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

import javax.jcr.security.Privilege;

/**
 * Permission. Contains all or at least most of the possible peprmission sets.
 */
public enum Permission {

	/**
	 * A permission.
	 */
	ALL(Privilege.JCR_ALL),
	/**
	 * A permission.
	 */
	READ(Privilege.JCR_READ),
	/**
	 * A permission.
	 */
	READ_INHERIT(Privilege.JCR_READ),
	/**
	 * A permission.
	 */
	MODIFY(Privilege.JCR_MODIFY_PROPERTIES, Privilege.JCR_VERSION_MANAGEMENT, Privilege.JCR_LOCK_MANAGEMENT),
	/**
	 * A permission.
	 */
	CREATE(Privilege.JCR_ADD_CHILD_NODES, Privilege.JCR_NODE_TYPE_MANAGEMENT),
	/**
	 * A permission.
	 */
	DELETE(Privilege.JCR_REMOVE_NODE, Privilege.JCR_REMOVE_CHILD_NODES),
	/**
	 * A permission.
	 */
	READ_ACL(Privilege.JCR_READ_ACCESS_CONTROL),
	/**
	 * A permission.
	 */
	EDIT_ACL(Privilege.JCR_MODIFY_ACCESS_CONTROL),
	/**
	 * A permission.
	 */
	REPLICATE("crx:replicate");

	/**
	 * Constructor.
	 *
	 * @param privileges privileges
	 */
	Permission(String... privileges) {
		this.privileges = privileges;
	}

	public String[] getPrivileges() {
		return Arrays.copyOf(this.privileges, this.privileges.length);
	}

	/**
	 * Check presence of privilege
	 *
	 * @param privilege privilege
	 * @return true if privilege exists
	 */
	public boolean isAllowedTo(String privilege) {
		return ArrayUtils.contains(privileges, privilege);
	}

	private final String[] privileges;
}
