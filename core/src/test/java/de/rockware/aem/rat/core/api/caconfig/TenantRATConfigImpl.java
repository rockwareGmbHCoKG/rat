package de.rockware.aem.rat.core.api.caconfig;

import java.lang.annotation.Annotation;

public class TenantRATConfigImpl implements TenantRATConfig {
    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public int startLevel() {
        return 2;
    }

    @Override
    public int endLevel() {
        return 4;
    }

    @Override
    public boolean createXFFolder() {
        return true;
    }

    @Override
    public boolean createDAMFolder() {
        return true;
    }

    @Override
    public boolean createCampaignsFolder() {
        return true;
    }

    @Override
    public boolean createProjectsFolder() {
        return true;
    }

    @Override
    public boolean createTagsFolder() {
        return true;
    }

    @Override
    public boolean createUGCFolder() {
        return true;
    }

    @Override
    public boolean createLaunchesFolder() {
        return true;
    }

    @Override
    public boolean createCommunitiesFolder() {
        return true;
    }

    @Override
    public boolean createFormsFolder() {
        return true;
    }

    @Override
    public boolean createCatalogsFolder() {
        return true;
    }

    @Override
    public boolean createScreensFolder() {
        return true;
    }

    @Override
    public boolean createReaders() {
        return true;
    }

    @Override
    public String groupNameReaders() {
        return "readers";
    }

    @Override
    public boolean createEditors() {
        return true;
    }

    @Override
    public String groupNameEditors() {
        return "editors";
    }

    @Override
    public boolean createPublishers() {
        return true;
    }

    @Override
    public String groupNamePublishers() {
        return "publishers";
    }

    @Override
    public boolean createUseradmins() {
        return true;
    }

    @Override
    public String groupNameUseradmins() {
        return "user-admins";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
