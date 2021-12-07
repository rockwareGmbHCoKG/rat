package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.services.InstanceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DefaultInstanceServiceTest {

    private final AemContext ctx = new AemContext();
    private InstanceService instanceService;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        instanceService = ctx.registerInjectActivateService(new DefaultInstanceService());
    }

    @Test
    void isAuthor() {
        assertFalse(instanceService.isAuthor());
    }

    @Test
    void isPublish() {
        assertTrue(instanceService.isPublish());
    }

    @Test
    void isLocal() {
        assertFalse(instanceService.isLocal());
    }

    @Test
    void hasRunModeValue() {
        assertTrue(instanceService.hasRunModeValue("publish"));
    }
}