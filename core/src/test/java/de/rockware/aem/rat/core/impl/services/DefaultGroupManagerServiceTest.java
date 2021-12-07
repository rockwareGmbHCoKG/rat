package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.services.GroupManagerService;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DefaultGroupManagerServiceTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private GroupManagerService gmService;

    @Mock
    Session session;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        gmService = ctx.registerInjectActivateService(new DefaultGroupManagerService());
    }

    @Test
    void createGroup() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            gmService.createGroup("", "blubbsi", session);
        });
    }

    @Test
    void addGroupsToList() throws RepositoryException {
        List<String> groupIds = new ArrayList<>();
        groupIds.add("blubbsi");
        UserManager userManager = mock(UserManager.class);
        ((DefaultGroupManagerService) gmService).addGroupsToList(groupIds, userManager, "/content/blubb", session);
    }

    @Test
    void getGroup() {
        assertNull(gmService.getGroup("abc", ctx.resourceResolver().adaptTo(Session.class)));
    }

    @Test
    void setGroupACLs() {
        assertThrows(NullPointerException.class, () -> {
            gmService.setGroupACLs(null, new ArrayList<>(), null, null, false);
        });
    }

    @Test
    void addMember() {
        assertThrows(NullPointerException.class, () -> {
            gmService.addMember(null, null, null);
        });
    }

    @Test
    void cleanup() {
        gmService.cleanup(ctx.resourceResolver().adaptTo(Session.class));
    }

    @Test
    void setStartLevelNodesACLs() {
        gmService.setStartLevelNodesACLs(new ArrayList<String>(), ctx.resourceResolver().adaptTo(Session.class), null);
    }

    @Test
    void reArrangeAcls() throws RepositoryException {
        JackrabbitAccessControlManager acM = mock(JackrabbitAccessControlManager.class);
        assertThrows(UnsupportedOperationException.class, () -> {
            gmService.reArrangeAcls("/content", ctx.resourceResolver().adaptTo(Session.class));
        });
    }

    @Test
    void getTenantRATConfig() {
        gmService.getTenantRATConfig(ctx.currentResource());
    }

    @Test
    void getGlobalRATConfig() {
        gmService.getGlobalRATConfig(ctx.currentResource());
    }
}