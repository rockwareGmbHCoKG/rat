package de.rockware.aem.rat.core.impl.config;

import de.rockware.aem.rat.core.api.caconfig.TenantRATConfig;
import de.rockware.aem.rat.core.api.resource.ResourcePathType;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration plus some plausibility checks.
 */
@Slf4j
@Getter
public class RichConfiguration {

    private final List<ResourcePathType> pathsToCreateList = new ArrayList<>();

    private boolean isActive = false;

    private int endLevel = 0;

    private int startLevel = 0;

    /**
     * C'tor.
     * @param pConfig   the context aware configuration data.
     */
    public RichConfiguration(TenantRATConfig pConfig) {
        if (pConfig != null) {
            initData(pConfig);
        }
    }

    /**
     * Init local data.
     * @param pConfig   CaConfig
     */
    private void initData(TenantRATConfig pConfig) {
        if (pConfig.createCampaignsFolder()) {
            pathsToCreateList.add(ResourcePathType.CAMPAIGNS);
        }
        if (pConfig.createCommunitiesFolder()) {
            pathsToCreateList.add(ResourcePathType.COMMUNITIES);
        }
        if (pConfig.createCatalogsFolder()) {
            pathsToCreateList.add(ResourcePathType.CATALOGS);
        }
        if (pConfig.createDAMFolder()) {
            pathsToCreateList.add(ResourcePathType.DAM);
        }
        if (pConfig.createFormsFolder()) {
            pathsToCreateList.add(ResourcePathType.FORMS);
        }
        if (pConfig.createLaunchesFolder()) {
            pathsToCreateList.add(ResourcePathType.LAUNCHES);
        }
        if (pConfig.createProjectsFolder()) {
            pathsToCreateList.add(ResourcePathType.PROJECTS);
        }
        if (pConfig.createXFFolder()) {
            pathsToCreateList.add(ResourcePathType.EXPERIENCE_FRAGMENTS);
        }
        if (pConfig.createTagsFolder()) {
            pathsToCreateList.add(ResourcePathType.TAGS);
        }
        if (pConfig.createScreensFolder()) {
            pathsToCreateList.add(ResourcePathType.SCREENS);
        }
        if (pConfig.createUGCFolder()) {
            pathsToCreateList.add(ResourcePathType.UGC);
        }
        startLevel = pConfig.startLevel();
        endLevel = pConfig.endLevel();
        isActive = pConfig.isActive();
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
