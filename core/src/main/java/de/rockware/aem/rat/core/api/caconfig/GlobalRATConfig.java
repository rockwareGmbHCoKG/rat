package de.rockware.aem.rat.core.api.caconfig;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Global RAT Config", description = "Control global acl settings.")
public @interface GlobalRATConfig {
    @Property(label = "", description = "If set to false no missing ACLs and groups will be created. If unchecked, all tenant configs will be diabled as well.")
    boolean isActive() default false;

    @Property(label = "Create Global Readers", description = "Create a global readers group.")
    boolean createGlobalReaders() default true;

    @Property(label = "Group Name For Global Readers", description = "Group name for global readers group.")
    String groupNameGlobalReaders() default "global-readers";

    @Property(label = "Create Global Editors", description = "Create a global editors group.")
    boolean createGlobalEditors() default true;

    @Property(label = "Group Name For Global Editors", description = "Group name for global editors group.")
    String groupNameGlobalEditors() default "global-editors";

    @Property(label = "Create Global Publishers", description = "Create a global publishers group.")
    boolean createGlobalPublishers() default true;

    @Property(label = "Group Name For Global Publishers", description = "Group name for global publishers group.")
    String groupNameGlobalPublishers() default "global-publishers";

    @Property(label = "Create Global Useradmins", description = "Create a global useradmins group.")
    boolean createGlobalUseradmins() default true;

    @Property(label = "Group Name For Global Useradmins", description = "Group name for global useradmin group.")
    String groupNameGlobalUseradmin() default "global-useradmins";

    @Property(label = "Create Global Support", description = "Create a global support group.")
    boolean createGlobalSupport() default true;

    @Property(label = "Group Name For Global Support", description = "Group name for global support group.")
    String groupNameGlobalSupport() default "global-support";

    @Property(label = "Group Name For Toplevel Readers", description = "Group name for toplevel readers.")
    String groupNameToplevelReaders() default "toplevel-readers";

    @Property(label = "Group Name Prefix", description = "Group name prefix for all groups.")
    String groupNamePrefix() default "a";

    @Property(label = "Group Name Suffix", description = "Group name suffix for all groups.")
    String groupNameSuffix() default "";

    @Property(label = "Group Name Separator", description = "Group name separator for all groups.")
    String groupNameSeparator() default ".";

    @Property(label = "Enable Read Inheritance", description = "If set to true all users with access to a certain page automatically have at least read access to all subpages.")
    boolean enableReadInheritance() default false;

    @Property(label = "Read Access Level", description = "All users will have read access from 1 to the level specified here.")
    int readAccessLevel() default 3;

}
