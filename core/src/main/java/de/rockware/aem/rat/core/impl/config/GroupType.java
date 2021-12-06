package de.rockware.aem.rat.core.impl.config;

/**
 * Different group types.
 */
public enum GroupType {
	NO_ACCESS(false, false),
	READER(true, false),
	/** This group is used to grant low level groups read access to the upper levels. */
	READ_INHERIT(true, false),
	EDITOR(true, false),
	PUBLISHER(true, false),
	USER_ADMIN(true, false),
	GLOBAL_READER(false, true),
	GLOBAL_EDITOR(false, true),
	GLOBAL_PUBLISHER(false, true),
	GLOBAL_SUPPORT(false, true),
	GLOBAL_USER_ADMIN(false, true),
	CUSTOM(false, false),
	TOPLEVEL_READER(false, false);

	private final boolean local;

	private final boolean global;

	GroupType(boolean local, boolean global) {
		this.local = local;
		this.global = global;
	}

	/**
	 * Check if this is a global group or not.
	 * @return	true if group type is global
	 */
	public boolean isGlobalGroup() {
		return global;
	}

	public boolean isLocalGroup() {
		return local;
	}
}
