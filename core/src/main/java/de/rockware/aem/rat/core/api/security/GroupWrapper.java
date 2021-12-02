package de.rockware.aem.rat.core.api.security;

import org.apache.jackrabbit.api.security.user.Group;
import org.slf4j.Logger;

import java.util.List;

import javax.jcr.RepositoryException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A wrapper object for jackrabbit groups. The object has some additional metadata that is needed to entitle the included groups correctly.
 * Created by ogebert on 14.02.16.
 */
public final class GroupWrapper {

	private static final Logger logger = getLogger(GroupWrapper.class);


	private Group group;
	private GroupType type;
	private PrincipalRule rule;
	private PrincipalRule groupRule;
	private PrincipalRule userRule;
	private List<String> pathList;
	private String prefix;
	private String suffix;
	private String groupId;

	/**
	 * Constructor.
	 * @param group	group that needs acl
	 * @param type	group type
	 * @param pathList list with paths the group should gain access to
	 */
	public GroupWrapper(Group group, GroupType type, List<String> pathList) {
		this(group, type, pathList, "", "");
	}

	/**
	 * Constructor.
	 * @param group	group that needs acl
	 * @param type	group type
	 * @param pathList list with paths the group should gain access to
	 * @param prefix group name prefix
	 */
	public GroupWrapper(Group group, GroupType type, List<String> pathList, String prefix) {
		this(group, type, pathList, prefix, "");
	}

	/**
	 * Constructor.
	 * @param group	group that needs acl
	 * @param type	group type
	 * @param pathList list with paths the group should gain access to
	 * @param prefix group name prefix
	 * @param suffix group name suffix
	 */
	public GroupWrapper(Group group, GroupType type, List<String> pathList, String prefix, String suffix) {
		this.group = group;
		this.type = type;
		this.pathList = pathList;
		this.prefix = prefix;
		this.suffix = suffix;
		readGroupId();
		computePrincipalRule();
	}

	/**
	 * Constructor.
	 * @param group	group that needs acl - type is set to custom here
	 * @param rule	principal rule that is used to build the acl
	 * @param pathList list with paths the group should gain access to
	 */
	public GroupWrapper(Group group, PrincipalRule rule, List<String> pathList){
		this(group, rule, pathList, "", "");
	}

	/**
	 * Constructor.
	 * @param group	group that needs acl - type is set to custom here
	 * @param rule	principal rule that is used to build the acl
	 * @param pathList list with paths the group should gain access to
	 * @param prefix group name prefix
	 */
	public GroupWrapper(Group group, PrincipalRule rule, List<String> pathList, String prefix){
		this(group, rule, pathList, prefix, "");
	}

	/**
	 * Constructor.
	 * @param group	group that needs acl - type is set to custom here
	 * @param rule	principal rule that is used to build the acl
	 * @param pathList list with paths the group should gain access to
	 * @param prefix group name prefix
	 * @param suffix group name suffix
	 */
	public GroupWrapper(Group group, PrincipalRule rule, List<String> pathList, String prefix, String suffix){
		this.group = group;
		this.type = GroupType.CUSTOM;
		this.rule = rule;
		this.prefix = prefix;
		this.suffix = suffix;
		this.pathList = pathList;
		readGroupId();
	}

	/**
	 * Compute the principal rule based on the given data.
	 */
	private void computePrincipalRule() {
		rule = null;
		groupRule = null;
		userRule = null;
		switch (type) {
			case NO_ACCESS:
				rule = new PrincipalRule(prefix, suffix, false, Permission.ALL);
				break;
			case READ_INHERIT:
			case READER:
			case TOPLEVEL_READER:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ);
				break;
			case EDITOR:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL);
				break;
			case PUBLISHER:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL);
				break;
			case USER_ADMIN:
				// user admins may edit groups but they cannot create or delete them.
				groupRule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL);
				break;
			case GLOBAL_READER:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ);
				break;
			case GLOBAL_EDITOR:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL);
				break;
			case GLOBAL_PUBLISHER:
				rule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL);
				break;
			case GLOBAL_SUPPORT:
				rule = new PrincipalRule(prefix, suffix, true, Permission.ALL);
				break;
			case GLOBAL_USER_ADMIN:
				// user admins may edit groups but they cannot create or delete them.
				groupRule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL);
				userRule = new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL, Permission.EDIT_ACL);
				break;
			case CUSTOM:
				// same as default - CUSTOM must use alternative constructor and set Principal rule outside this object.
			default:
				rule = new PrincipalRule(prefix, suffix, false, Permission.ALL);
		}
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public GroupType getType() {
		return type;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public PrincipalRule getUserRule() {
		return userRule;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public PrincipalRule getGroupRule() {
		return groupRule;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public PrincipalRule getRule() {
		return rule;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public List<String> getPathList() {
		return pathList;
	}

	/**
	 * Getter.
	 * @return the value
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * Read the group ID without throwing an error.
	 */
	private void readGroupId() {
		try {
			groupId = group.getID();
		} catch (RepositoryException ex) {
			groupId = "";
			logger.error("Cannot read group id: {}", ex.getMessage());
		}
	}
}
