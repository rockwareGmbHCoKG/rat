package de.rockware.aem.rat.core.api.security;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * Store a principal's permissions and more in one object.
 */
public class PrincipalRule {
	/**
	 * Replication permission <code>PRIVILEGE_CRX_REPLICATE</code>
	 */
	public static final String PRIVILEGE_CRX_REPLICATE = "crx:replicate";
	private final Permission[] permissions;
	private final boolean allowRule;

	/**
	 * Constructor.
	 *
	 * @param allowRule true if allow else false
	 * @param permissions rule permissions
	 */
	public PrincipalRule(boolean allowRule, Permission... permissions) {
		this.permissions = permissions;
		this.allowRule = allowRule;
	}

	/**
	 * Getter...
	 * @return	the value
	 */
	public Permission[] getPermissions() {
		return Arrays.copyOf(permissions, permissions.length);
	}

	/**
	 * Getter...
	 * @return	the value
	 */
	public boolean isAllowRule() {
		return allowRule;
	}

	/**
	 * Getter...
	 * @return	the value
	 */
	public boolean isDenyRule() {
		return !allowRule;
	}

	/**
	 * Check read ability
	 *
	 * @return true if exists read permission
	 */
	public boolean canRead() {
		return isCapableOf(Permission.READ);
	}

	/**
	 * Check modify ability
	 *
	 * @return true if exists modify permission
	 */
	public boolean canModify() {
		return isCapableOf(Permission.MODIFY);
	}

	/**
	 * Check create ability
	 *
	 * @return true if exists create permission
	 */
	public boolean canCreate() {
		return isCapableOf(Permission.CREATE);
	}

	/**
	 * Check delete ability
	 *
	 * @return true if exists delete permission
	 */
	public boolean canDelete() {
		return isCapableOf(Permission.DELETE);
	}

	/**
	 * Check read ability
	 *
	 * @return true if exists read permission
	 */
	public boolean canReadAcl() {
		return isCapableOf(Permission.READ_ACL);
	}

	/**
	 * Check edit ability
	 *
	 * @return true if exists edit permission
	 */
	public boolean canEditAcl() {
		return isCapableOf(Permission.EDIT_ACL);
	}

	/**
	 * Check replication ability
	 *
	 * @return true if exists replication permission
	 */
	public boolean canReplicate() {
		return isCapableOf(Permission.REPLICATE);
	}

	/**
	 * Check presence of the permission
	 *
	 * @param permission the requested permission
	 * @return if the requested permission is there
	 */
	public boolean isCapableOf(Permission permission) {
		return ArrayUtils.contains(this.permissions, permission);
	}

}
