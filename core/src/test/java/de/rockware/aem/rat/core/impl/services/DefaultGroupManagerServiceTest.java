package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.services.GroupManagerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DefaultGroupManagerServiceTest {

    private final AemContext ctx = new AemContext();

    private GroupManagerService gmService;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        gmService = ctx.registerInjectActivateService(new DefaultGroupManagerService());
    }

    @Test
    void createGroup() {
    }

    @Test
    void createGroups() {
    }

    @Test
    void getGroup() {
    }

    @Test
    void setGroupACLs() {
    }

    @Test
    void addMember() {
    }

    @Test
    void cleanup() {
    }

    @Test
    void activate() {
    }

    @Test
    void deactivate() {
    }

    @Test
    void setStartLevelNodesACLs() {
    }

    @Test
    void reArrangeAcls() {
    }

    @Test
    void getTenantRATConfig() {
    }

    @Test
    void getGlobalRATConfig() {
    }
}