package de.rockware.aem.rat.core.impl.services;

import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfig;
import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfigImpl;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfig;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfigImpl;
import de.rockware.aem.rat.core.api.config.GroupType;
import de.rockware.aem.rat.core.api.config.RichConfiguration;
import de.rockware.aem.rat.core.api.security.GroupWrapper;
import de.rockware.aem.rat.core.api.security.PrincipalRule;
import de.rockware.aem.rat.core.api.services.TenantSecurityService;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DefaultTenantSecurityServiceImplTest {

    private final AemContext ctx = new AemContext(ResourceResolverType.JCR_MOCK);

    private TenantSecurityService tSS;

    private RichConfiguration richConfig;

    private String path;

    @Mock
    private GroupWrapper wrapper;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        ctx.registerInjectActivateService(new DefaultGroupManagerService());
        path = "/content/mademyday/inner";
        richConfig = new RichConfiguration(new TenantRATConfigImpl(), new GlobalRATConfigImpl(), path);
        tSS = ctx.registerInjectActivateService(new DefaultTenantSecurityServiceImpl());
        lenient().when(wrapper.getGroupId()).thenReturn("groupId-1");
        List<String> pathList = new ArrayList<>();
        pathList.add(path);
        PrincipalRule rule = new PrincipalRule(true);
        lenient().when(wrapper.getGroupRule()).thenReturn(rule, null, rule);
        lenient().when(wrapper.getUserRule()).thenReturn(rule, null);
        lenient().when(wrapper.getPathList()).thenReturn(pathList);
        lenient().when(wrapper.getType()).thenReturn(GroupType.PUBLISHER, GroupType.TOPLEVEL_READER, GroupType.GLOBAL_USER_ADMIN, GroupType.GLOBAL_USER_ADMIN, GroupType.USER_ADMIN);
        lenient().when(wrapper.getRule()).thenReturn(rule, null);
    }

    @Test
    void computeGroupName() {
        assertEquals("a.mademyday.inner.editors", tSS.computeGroupName(GroupType.EDITOR, path, richConfig));
        RichConfiguration rConfig = new RichConfiguration(new TenantRATConfigImpl(), new GlobalRATConfigImpl(), path);
        assertEquals(rConfig.getGroupMap().get(GroupType.EDITOR), tSS.computeGroupName(GroupType.EDITOR, "", rConfig));
        assertThrows(IllegalArgumentException.class, () -> {
            tSS.computeGroupName(null, path, rConfig);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tSS.computeGroupName(GroupType.EDITOR, path, null);
        });
    }

    @Test
    void handleGroupsAndACLs() {
        tSS.handleGroupsAndACLs(new ArrayList<>(), richConfig, 2, ctx.resourceResolver());
    }

    @Test
    void createGlobalGroupsForPath() {
        assertTrue(tSS.createGlobalGroupsForPath(path, new ArrayList<>(), ctx.resourceResolver(), richConfig).size() == 0);
        tSS.createGlobalGroupsForPath(path + "/a/b/c/d/e/f/g", new ArrayList<>(), ctx.resourceResolver(), richConfig);
    }

    @Test
    void entitleGroup() {
        for (int i = 0; i < 5; i++) {
            assertThrows(UnsupportedOperationException.class, () -> {
                tSS.entitleGroup(wrapper, false, ctx.resourceResolver(), richConfig);
            });
        }
    }

    @Test
    void testCheckGroupInheritance() {
        tSS.checkGroupInheritance(wrapper, new ArrayList<>(), ctx.resourceResolver(), richConfig);
    }

    @Test
    void createGroupWrapper() {
        assertThrows(IllegalArgumentException.class, () -> {
            tSS.createGroupWrapper("", "", null, null, null, null, null);
        });
        Map<GroupType, GroupWrapper> groupMap = new HashMap<>();
        tSS.createGroupWrapper(path, "", GroupType.EDITOR, groupMap, new ArrayList<>(), ctx.resourceResolver(), richConfig);
        assertTrue(groupMap.size() == 0);
    }

    @Test
    void addGroupToAEMStandardGroups() {
        tSS.addGroupToAEMStandardGroups(mock(Group.class), ctx.resourceResolver());
    }
}