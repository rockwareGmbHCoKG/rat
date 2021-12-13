package de.rockware.aem.rat.core.impl.crud;

import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;

import de.rockware.aem.rat.core.api.resource.ResourceHelper;
import de.rockware.aem.rat.core.api.config.ResourcePathType;
import de.rockware.aem.rat.core.api.config.RichConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Resource creator.
 */
@Slf4j
public class ResourceCreator {

    /**
     * Create resources.
     * @param resourcePath          path to current resource
     * @param richConfiguration     configuration
     * @param resolver              valid resource resolver
     * @return list with resource paths
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

    public static List<String> createPageResources(@NotNull ResourceResolver resolver, String resourcePath) {
        List<String> paths = new ArrayList<>();
        Resource resource = resolver.getResource(resourcePath);
        if (resource != null) {
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            String templatePath = "";
            String pagePath;
            String parentPagePath;
            Resource contentResource = resource.getChild("jcr:content");
            if (contentResource != null) {
                String pageTitle = contentResource.getValueMap().get("jcr:title", resource.getName());
            }
            // TODO: is this method needed at all?
        }
        return paths;
    }
}
