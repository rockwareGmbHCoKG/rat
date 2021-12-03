package de.rockware.aem.rat.core.impl.config;

import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfig;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration plus some plausibility checks.
 */
@Slf4j
@Getter
public final class RichConfiguration {

    private final List<ResourcePathType> pathsToCreateList = new ArrayList<>();

    private final Map<GroupType, GroupData> groupMap = new HashMap<>();

    private boolean isTenantActive = false;

    private boolean isGlobalActive = false;

    private int readAccessLevel = 0;

    private int endLevel = 0;

    private int startLevel = 0;

    private String groupNamePrefix = "";

    private String groupNameSuffix = "";

    private String groupNameSeperator = ".";

    private boolean enableReadInheritance = false;

    /**
     * C'tor.
     * @param tenantRATConfig   the context aware configuration data.
     * @param globalRATConfig global configuration data.
     */
    public RichConfiguration(TenantRATConfig tenantRATConfig, GlobalRATConfig globalRATConfig) {
        if (tenantRATConfig != null) {
            initData(tenantRATConfig, globalRATConfig);
        }
    }

    /**
     * Init local data.
     * @param tenantRATConfig   CaConfig
     * @param globalRATConfig   global config
     */
    private void initData(TenantRATConfig tenantRATConfig, GlobalRATConfig globalRATConfig) {
        if (tenantRATConfig.createCampaignsFolder()) {
            pathsToCreateList.add(ResourcePathType.CAMPAIGNS);
        }
        if (tenantRATConfig.createCommunitiesFolder()) {
            pathsToCreateList.add(ResourcePathType.COMMUNITIES);
        }
        if (tenantRATConfig.createCatalogsFolder()) {
            pathsToCreateList.add(ResourcePathType.CATALOGS);
        }
        if (tenantRATConfig.createDAMFolder()) {
            pathsToCreateList.add(ResourcePathType.DAM);
        }
        if (tenantRATConfig.createFormsFolder()) {
            pathsToCreateList.add(ResourcePathType.FORMS);
        }
        if (tenantRATConfig.createLaunchesFolder()) {
            pathsToCreateList.add(ResourcePathType.LAUNCHES);
        }
        if (tenantRATConfig.createProjectsFolder()) {
            pathsToCreateList.add(ResourcePathType.PROJECTS);
        }
        if (tenantRATConfig.createXFFolder()) {
            pathsToCreateList.add(ResourcePathType.EXPERIENCE_FRAGMENTS);
        }
        if (tenantRATConfig.createTagsFolder()) {
            pathsToCreateList.add(ResourcePathType.TAGS);
        }
        if (tenantRATConfig.createScreensFolder()) {
            pathsToCreateList.add(ResourcePathType.SCREENS);
        }
        if (tenantRATConfig.createUGCFolder()) {
            pathsToCreateList.add(ResourcePathType.UGC);
        }
        startLevel = tenantRATConfig.startLevel();
        endLevel = tenantRATConfig.endLevel();
        isGlobalActive = globalRATConfig.isActive();
        isTenantActive = isGlobalActive && tenantRATConfig.isActive();
        readAccessLevel = globalRATConfig.readAccessLevel();
        groupNamePrefix = globalRATConfig.groupNamePrefix();
        groupNameSuffix = globalRATConfig.groupNameSuffix();
        groupNameSeperator = globalRATConfig.groupNameSeparator();
        enableReadInheritance = globalRATConfig.enableReadInheritance();
        if (tenantRATConfig.createEditors()) {
            // TODO: check if CaConfigs can work with enums
            // TODO: otherwise do this stuff for each group
            groupMap.put(GroupType.EDITOR, new GroupData(GroupType.EDITOR, tenantRATConfig.groupNameEditors(), groupNamePrefix, groupNameSuffix));
        }
    }

    /**
     * Convenience method...
     * @param type  type to test for
     * @return  true, if type exists
     */
    public boolean needsCreation(ResourcePathType type) {
        return pathsToCreateList.contains(type);
    }

}
