package de.rockware.aem.rat.core.impl.config;

import de.rockware.aem.rat.core.api.security.Permission;
import de.rockware.aem.rat.core.api.security.PrincipalRule;

import org.apache.jackrabbit.api.security.user.Group;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Pojo with internal data for group creation.
 */
@Getter
@Slf4j
public final class GroupData {

    private GroupType groupType;
    private String groupName;
    private boolean isGlobalGroup = false;

    private Map<PrincipalRuleType, PrincipalRule> ruleMap = new HashMap<>();

    @Setter
    private Group group;
    @Setter
    private String prefix;
    @Setter
    private String suffix;
    @Setter
    private String groupId;

    /**
     * Create a new GroupData object based on the given group type.
     * @param groupType group type
     * @param groupName name (without prefix, suffix and separator) for this group
     */
    public GroupData(GroupType groupType, String groupName) {
        this.groupType = groupType;
        this.groupName = groupName;
        init();
    }

    /**
     * Initialize fields.
     */
    private void init() {
        switch (groupType) {
            case GLOBAL_READER:
                isGlobalGroup = true;
            case READ_INHERIT:
            case READER:
            case TOPLEVEL_READER:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(true, Permission.READ));
                break;
            case GLOBAL_EDITOR:
                isGlobalGroup = true;
            case EDITOR:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL));
                break;
            case GLOBAL_PUBLISHER:
                isGlobalGroup = true;
            case PUBLISHER:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL));
                break;
            case USER_ADMIN:
                // user admins may edit groups, but they cannot create or delete them.
                ruleMap.put(PrincipalRuleType.GROUP, new PrincipalRule(true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL));
                break;
            case GLOBAL_SUPPORT:
                isGlobalGroup = true;
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(true, Permission.ALL));
                break;
            case GLOBAL_USER_ADMIN:
                isGlobalGroup = true;
                // user admins may edit groups, but they cannot create or delete them.
                ruleMap.put(PrincipalRuleType.GROUP, new PrincipalRule(true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL));
                ruleMap.put(PrincipalRuleType.USER, new PrincipalRule(true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL, Permission.EDIT_ACL));
                break;
            case CUSTOM:
                // same as default - CUSTOM must use alternative constructor and set Principal rule outside this object.
                // TODO: check if no_access is used at all...
            case NO_ACCESS:
            default:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(false, Permission.ALL));
        }

    }

    /**
     * Types of rules - Group and User rules will be applied to /home for user admins.
     */
    public enum PrincipalRuleType {
        DEFAULT, GROUP, USER
    }
}
