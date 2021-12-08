package de.rockware.aem.rat.core.api.security;

import de.rockware.aem.rat.core.api.config.GroupType;
import org.apache.jackrabbit.api.security.user.Group;

import java.util.List;

import javax.jcr.RepositoryException;

import lombok.extern.slf4j.Slf4j;

/**
 * A wrapper object for jackrabbit groups. The object has some additional metadata that is needed to entitle the included groups correctly.
 */
@Slf4j
public class GroupWrapper {
	private final Group group;
	private final GroupType type;
	private PrincipalRule rule;
	private PrincipalRule groupRule;
	private PrincipalRule userRule;
	private final List<String> pathList;
	private String groupId;

	/**
	 * Constructor.
	 * @param group	group that needs acl
	 * @param type	group type
	 * @param pathList list with paths the group should gain access to
	 */
	public GroupWrapper(Group group, GroupType type, List<String> pathList) {
		this.group = group;
		this.type = type;
		this.pathList = pathList;
		readGroupId();
		computePrincipalRule();
	}

	/**
	 * Compute the principal rule based on the given data.
	 */
	private void computePrincipalRule() {
		rule = null;
		groupRule = null;
		userRule = null;
		switch (type) {
			case READ_INHERIT:
			case READER:
			case TOPLEVEL_READER:
				rule = new PrincipalRule(true, Permission.READ);
				break;
			case EDITOR:
				rule = new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL);
				break;
			case PUBLISHER:
				rule = new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL);
				break;
			case USER_ADMIN:
				// user admins may edit groups but they cannot create or delete them.
				groupRule = new PrincipalRule(true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL);
				break;
			case GLOBAL_READER:
				rule = new PrincipalRule(true, Permission.READ);
				break;
			case GLOBAL_EDITOR:
				rule = new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL);
				break;
			case GLOBAL_PUBLISHER:
				rule = new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL);
				break;
			case GLOBAL_SUPPORT:
				rule = new PrincipalRule(true, Permission.ALL);
				break;
			case GLOBAL_USER_ADMIN:
				// user admins may edit groups but they cannot create or delete them.
				groupRule = new PrincipalRule(true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL);
				userRule = new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL, Permission.EDIT_ACL);
				break;
			case CUSTOM:
				// same as default - CUSTOM must use alternative constructor and set Principal rule outside this object.
			case NO_ACCESS:
			default:
				rule = new PrincipalRule(false, Permission.ALL);
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
			log.error("Cannot read group id: {}", ex.getMessage());
		}
	}
}
