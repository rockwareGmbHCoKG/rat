package de.rockware.aem.rat.core.impl.crud;

import de.rockware.aem.rat.core.api.resource.ResourceHelper;
import de.rockware.aem.rat.core.api.resource.ResourcePathType;
import de.rockware.aem.rat.core.impl.config.RichConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource creator.
 */
public class ResourceCreator {

    /**
     *
     * @param resourcePath
     * @param richConfiguration
     * @param resolver
     * @return
     */
    public static List<String> createResources(String resourcePath, RichConfiguration richConfiguration, ResourceResolver resolver) {
        List<String> pathList = new ArrayList<>();
        for (ResourcePathType pathType : ResourcePathType.values()) {
            if (richConfiguration.needsCreation(pathType)) {
                String newPath = StringUtils.replace(resourcePath, "/content", pathType.getPath());
                ResourceHelper.createResource(newPath, "sling:OrderedFolder", resolver);
                pathList.add(newPath);
            }
        }
        // TODO: create more resources:
        // TODO: additional folders, pages with certain primary types and templates
        return pathList;
    }
}
