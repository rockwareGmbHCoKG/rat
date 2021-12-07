package de.rockware.aem.rat.core.impl.crud;

import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfigImpl;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfigImpl;
import de.rockware.aem.rat.core.api.config.RichConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class ResourceCreatorTest {

    private final AemContext ctx = new AemContext();
    private RichConfiguration richConfig;

    @BeforeEach
    void setUp() {
        richConfig = new RichConfiguration(new TenantRATConfigImpl(), new GlobalRATConfigImpl(), "");
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
    }

    @Test
    void createResources() {
        List<String> pathList = ResourceCreator.createResources("/content/hans", richConfig, ctx.resourceResolver());
        assertTrue(pathList.contains("/content/dam/hans"));
    }

    @Test
    void createPageResources() {
        ResourceCreator.createPageResources(ctx.resourceResolver(), "/content/experiencepage");
    }
}