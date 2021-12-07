package de.rockware.aem.rat.core.impl;

import de.rockware.aem.rat.core.api.resource.ResourceHelper;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.lenient;


@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class ResourceUtilsTest {

    private final AemContext ctx = new AemContext();

    @Mock
    ResourceResolverFactory factory;

    @Mock
    ResourceResolverFactory factory2;

    @Mock
    ResourceResolverFactory factory3;

    @Mock
    ResourceResolver resolver3;

    @BeforeEach
    void setUp() throws LoginException {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        lenient().when(factory.getServiceResourceResolver(anyMap())).thenReturn(ctx.resourceResolver());
        lenient().when(factory2.getServiceResourceResolver(anyMap())).thenThrow(LoginException.class);
        lenient().when(factory3.getServiceResourceResolver(anyMap())).thenReturn(resolver3);
        lenient().when(resolver3.isLive()).thenReturn(false);
    }

    @Test
    void getResolver() {
        assertNotNull(ResourceUtils.getResolver(factory, ResourceHelper.class));
        assertNull(ResourceUtils.getResolver(factory2, ResourceHelper.class));
    }

    @Test
    void closeResolver() {
        ResourceResolver resolver = ResourceUtils.getResolver(factory, ResourceHelper.class);
        ResourceUtils.closeResolver(resolver);
        ResourceResolver resolver2 = ResourceUtils.getResolver(factory3, ResourceHelper.class);
        ResourceUtils.closeResolver(resolver2);
    }

    @Test
    void getAuthInfoMap() {
    }
}