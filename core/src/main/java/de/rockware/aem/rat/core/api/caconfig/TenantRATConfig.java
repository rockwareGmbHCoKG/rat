package de.rockware.aem.rat.core.api.caconfig;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Tenant RAT Config", description = "Control automatic page and folder creation and acl settings for your tenant.")
public @interface TenantRATConfig {
    @Property(label = "", description = "If set to false no missing ACLs and groups will be created.")
    boolean isActive() default false;

    @Property(label = "Start Level", description = "Groups will be set from this level (/content has level 0).")
    int startLevel() default 1;

    @Property(label = "End Level", description = "Groups will be set up to this level (/content has level 0).")
    int endLevel() default 3;

    @Property(label = "Create XF Folder", description = "Create a folder for experience fragments.")
    boolean createXFFolder() default false;

    @Property(label = "Create DAM Folder", description = "Create a DAM folder for assets.")
    boolean createDAMFolder() default false;

    @Property(label = "Create Campaigns Folder", description = "Create a Campaigns folder.")
    boolean createCampaignsFolder() default false;

    @Property(label = "Create Projects Folder", description = "Create a folder for projects.")
    boolean createProjectsFolder() default false;

    @Property(label = "Create Tags Folder", description = "Create a folder for tags.")
    boolean createTagsFolder() default false;

    @Property(label = "Create UGC Folder", description = "Create a folder for user generated content.")
    boolean createUGCFolder() default false;

    @Property(label = "Create Launches Folder", description = "Create a folder for launches.")
    boolean createLaunchesFolder() default false;

    @Property(label = "Create Communities Folder", description = "Create a folder for communities.")
    boolean createCommunitiesFolder() default false;

    @Property(label = "Create Forms Folder", description = "Create a folder for forms.")
    boolean createFormsFolder() default false;

    @Property(label = "Create Catalogs Folder", description = "Create a folder for catalogs.")
    boolean createCatalogsFolder() default false;

    @Property(label = "Create Screens Folder", description = "Create a folder for screens.")
    boolean createScreensFolder() default false;

    @Property(label = "Create Readers", description = "Create a readers group.")
    boolean createReaders() default true;

    @Property(label = "Group Name For Readers", description = "Group name for readers group.")
    String groupNameReaders() default "readers";

    @Property(label = "Create Editors", description = "Create a editors group.")
    boolean createEditors() default true;

    @Property(label = "Group Name For Editors", description = "Group name for editors group.")
    String groupNameEditors() default "editors";

    @Property(label = "Create Publishers", description = "Create a publishers group.")
    boolean createPublishers() default true;

    @Property(label = "Group Name For Publishers", description = "Group name for publishers group.")
    String groupNamePublishers() default "publishers";

    @Property(label = "Create Useradmins", description = "Create a useradmins group.")
    boolean createUseradmins() default true;

    @Property(label = "Group Name For Useradmins", description = "Group name for useradmin group.")
    String groupNameUseradmins() default "useradmins";


}
