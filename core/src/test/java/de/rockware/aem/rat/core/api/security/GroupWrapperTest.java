package de.rockware.aem.rat.core.api.security;

import de.rockware.aem.rat.core.api.config.GroupType;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.api.security.user.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class GroupWrapperTest {

    private final AemContext ctx = new AemContext();

    private GroupWrapper wrapper;

    @Mock
    Group group;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/rat/core/api/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        List<String> pathList = new ArrayList<>();
        pathList.add("/content/dam/mypath");
        pathList.add("/content/experience-fragments/mypath");
        wrapper = new GroupWrapper(group, GroupType.EDITOR, pathList);
    }

    @Test
    void getGroup() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.READER, pathList);
        assertEquals(wrapper.getGroup(), group);
    }

    @Test
    void getType() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.PUBLISHER, pathList);
        assertEquals(wrapper.getType(), GroupType.PUBLISHER);
    }

    @Test
    void getUserRule() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.GLOBAL_USER_ADMIN, pathList);
        assertNotNull(wrapper.getUserRule());
    }

    @Test
    void getGroupRule() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.USER_ADMIN, pathList);
        assertNotNull(wrapper.getGroupRule());
    }

    @Test
    void getRule() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.GLOBAL_READER, pathList);
        assertNull(wrapper.getGroupRule());
        assertNotNull(wrapper.getRule());
    }

    @Test
    void getPathListEmpty() {
        List<String> pathList = new ArrayList<>();
        wrapper = new GroupWrapper(group, GroupType.GLOBAL_EDITOR, pathList);
        assertTrue(wrapper.getPathList().size() == 0);
    }

    @Test
    void getPathList() {
        List<String> pathList = new ArrayList<>();
        pathList.add("/content/dam/mypath");
        pathList.add("/content/experience-fragments/mypath");
        wrapper = new GroupWrapper(group, GroupType.GLOBAL_PUBLISHER, pathList);
        GroupWrapper wrapper2 = new GroupWrapper(group, GroupType.GLOBAL_SUPPORT, pathList);
        GroupWrapper wrapper3 = new GroupWrapper(group, GroupType.NO_ACCESS, pathList);
        assertTrue(wrapper.getPathList().size() == 2);
    }

    @Test
    void getGroupId() throws RepositoryException {
        List<String> pathList = new ArrayList<>();
        pathList.add("/content/dam/mypath");
        pathList.add("/content/experience-fragments/mypath");
        Group group2 = mock(Group.class);
        when(group2.getID()).thenReturn("groupid");
        GroupWrapper wrapper2 = new GroupWrapper(group, GroupType.EDITOR, pathList);
        assertNotNull(wrapper2.getGroupId());
    }
}