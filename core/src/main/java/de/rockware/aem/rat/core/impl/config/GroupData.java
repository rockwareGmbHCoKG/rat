package de.rockware.aem.rat.core.impl.config;

import de.rockware.aem.rat.core.api.security.Permission;
import de.rockware.aem.rat.core.api.security.PrincipalRule;
import lombok.Getter;
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
    private String prefix;
    private String suffix;

    private Map<PrincipalRuleType, PrincipalRule> ruleMap = new HashMap<>();

    /**
     * Create a new GroupData object based on the given group type.
     * @param groupType group type
     * @param groupName name (without prefix, suffix and separator) for this group
     */
    public GroupData(GroupType groupType, String groupName, String prefix, String suffix) {
        this.groupType = groupType;
        this.groupName = groupName;
        init();
    }

    /**
     * Initialize fields.
     */
    private void init() {
        switch (groupType) {
            case READ_INHERIT:
            case READER:
            case TOPLEVEL_READER:
            case GLOBAL_READER:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(prefix, suffix, true, Permission.READ));
                break;
            case EDITOR:
            case GLOBAL_EDITOR:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL));
                break;
            case PUBLISHER:
            case GLOBAL_PUBLISHER:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.REPLICATE, Permission.READ_ACL));
                break;
            case USER_ADMIN:
                // user admins may edit groups, but they cannot create or delete them.
                ruleMap.put(PrincipalRuleType.GROUP, new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL));
                break;
            case GLOBAL_SUPPORT:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(prefix, suffix, true, Permission.ALL));
                break;
            case GLOBAL_USER_ADMIN:
                // user admins may edit groups, but they cannot create or delete them.
                ruleMap.put(PrincipalRuleType.GROUP, new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.MODIFY, Permission.READ_ACL, Permission.EDIT_ACL));
                ruleMap.put(PrincipalRuleType.USER, new PrincipalRule(prefix, suffix, true, Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE, Permission.READ_ACL, Permission.EDIT_ACL));
                break;
            case CUSTOM:
                // same as default - CUSTOM must use alternative constructor and set Principal rule outside this object.
            case NO_ACCESS:
            default:
                ruleMap.put(PrincipalRuleType.DEFAULT, new PrincipalRule(prefix, suffix, false, Permission.ALL));
        }
    }

    /**
     * Types of rules - Group and User rules will be applied to /home for user admins.
     */
    public enum PrincipalRuleType {
        DEFAULT, GROUP, USER
    }
}
