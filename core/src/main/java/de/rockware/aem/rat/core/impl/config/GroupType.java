package de.rockware.aem.rat.core.impl.config;

/**
 * Different group types.
 */
public enum GroupType {
	NO_ACCESS,
	READER,
	/** This group is used to grant low level groups read access to the upper levels. */
	READ_INHERIT,
	EDITOR,
	PUBLISHER,
	USER_ADMIN,
	GLOBAL_READER,
	GLOBAL_EDITOR,
	GLOBAL_PUBLISHER,
	GLOBAL_SUPPORT,
	GLOBAL_USER_ADMIN,
	CUSTOM,
	TOPLEVEL_READER;

	/**
	 * Check if this is a global group or not.
	 * @return	true if group type is global
	 */
	public boolean isGlobalGroup() {
		return this.name().startsWith("GLOBAL_");
	}
}
