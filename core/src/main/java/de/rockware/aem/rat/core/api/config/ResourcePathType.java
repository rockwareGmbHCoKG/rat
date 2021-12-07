package de.rockware.aem.rat.core.api.config;

/**
 * All required resource path types.
 */
public enum ResourcePathType {
    DAM("dam"), CAMPAIGNS("campaigns"), EXPERIENCE_FRAGMENTS("experience-fragments"), TAGS("cq:tags"), UGC("usergenerated"),
    LAUNCHES("launches"), COMMUNITIES("communities"), FORMS("forms"), CATALOGS("catalogs"), SCREENS("screens"), PROJECTS("projects");

    private final String path;

    ResourcePathType(String path) {
        this.path = "/content/" + path;
    }

    public String getPath() {
            return path;
    }
}
