package de.rockware.aem.rat.core.api.resource;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class ResourceHelperTest {

    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
    }

    @Test
    void createResource() {
        Resource resource = ResourceHelper.createResource("/content/dam/myfolder", "sling:folder", ctx.resourceResolver());
        assertNotNull(resource);
    }

    @Test
    void createResourceClosedResolver() {
        ctx.resourceResolver().close();
        Resource resource = ResourceHelper.createResource("/content/dam/myfolder", "sling:folder", ctx.resourceResolver());
        assertNotNull(resource);
    }

    @Test
    void createResourceRoot() {
        Resource resource = ResourceHelper.createResource("/", "sling:folder", ctx.resourceResolver());
        assertNotNull(resource);
    }

    @Test
    void createResourceEmptyPath() {
        Resource resource = ResourceHelper.createResource("", "sling:folder", ctx.resourceResolver());
        assertNotNull(resource);
    }


    @Test
    void isValidCustomTopLevel() {
        assertFalse(ResourceHelper.isValidCustomTopLevel("/content"));
    }

    @Test
    void getResourceLevel() {
        assertEquals(ResourceHelper.getResourceLevel("/content/testpath/deeperinside"), 3);
    }

    @Test
    void getResourceLevelEmpytPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            ResourceHelper.getResourceLevel(null);
        });
    }

    @Test
    void getResourceLevelIllegalPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            ResourceHelper.getResourceLevel("noLeading_slash/");
        });
    }

}