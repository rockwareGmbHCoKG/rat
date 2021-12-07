package de.rockware.aem.rat.core.api.caconfig;

import java.lang.annotation.Annotation;

public class GlobalRATConfigImpl implements GlobalRATConfig {
    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean createGlobalReaders() {
        return true;
    }

    @Override
    public String groupNameGlobalReaders() {
        return "global-readers";
    }

    @Override
    public boolean createGlobalEditors() {
        return true;
    }

    @Override
    public String groupNameGlobalEditors() {
        return "global-editors";
    }

    @Override
    public boolean createGlobalPublishers() {
        return true;
    }

    @Override
    public String groupNameGlobalPublishers() {
        return "global-publishers";
    }

    @Override
    public boolean createGlobalUseradmins() {
        return true;
    }

    @Override
    public String groupNameGlobalUseradmin() {
        return "global-useradmins";
    }

    @Override
    public boolean createGlobalSupport() {
        return true;
    }

    @Override
    public String groupNameGlobalSupport() {
        return "global-support";
    }

    @Override
    public String groupNameToplevelReaders() {
        return "top-level-readers";
    }

    @Override
    public String groupNamePrefix() {
        return "a";
    }

    @Override
    public String groupNameSuffix() {
        return "";
    }

    @Override
    public String groupNameSeparator() {
        return ".";
    }

    @Override
    public boolean enableReadInheritance() {
        return false;
    }

    @Override
    public int readAccessLevel() {
        return 2;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
