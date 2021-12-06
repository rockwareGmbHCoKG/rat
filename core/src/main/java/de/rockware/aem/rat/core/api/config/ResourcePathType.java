package de.rockware.aem.rat.core.api.config;

import com.day.cq.commons.jcr.JcrConstants;

/**
 * All required resource path types.
 */
public enum ResourcePathType {
    DAM("dam"), CAMPAIGNS("campaigns"), EXPERIENCE_FRAGMENTS("experience-fragments"), TAGS("cq:tags"), UGC("usergenerated"),
    LAUNCHES("launches"), COMMUNITIES("communities"), FORMS("forms"), CATALOGS("catalogs"), SCREENS("screens"), PROJECTS("projects");

    private final String path;

    ResourcePathType(String path) {
        this.path = "/" + JcrConstants.JCR_CONTENT + "/" + path;
    }

    public String getPath() {
            return path;
    }
}
