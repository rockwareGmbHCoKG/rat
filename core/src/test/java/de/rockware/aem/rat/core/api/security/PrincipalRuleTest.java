package de.rockware.aem.rat.core.api.security;

import org.junit.jupiter.api.Test;

import javax.jcr.security.Privilege;

import static org.junit.jupiter.api.Assertions.*;

class PrincipalRuleTest {

    @Test
    void getPermissions() {
        PrincipalRule rule = new PrincipalRule(true, Permission.ALL);
        Permission permission = rule.getPermissions()[0];
        assertTrue(permission.isAllowedTo(Privilege.JCR_ALL));
        assertNotNull(permission.getPrivileges());
        assertNotNull(rule.getPermissions());
    }

    @Test
    void isAllowRule() {
        PrincipalRule rule = new PrincipalRule(true, Permission.ALL);
        assertFalse(rule.isDenyRule());
        assertTrue(rule.isAllowRule());
    }

    @Test
    void isDenyRule() {
        PrincipalRule rule = new PrincipalRule(false, Permission.ALL);
        assertFalse(rule.isAllowRule());
        assertTrue(rule.isDenyRule());
    }

    @Test
    void canRead() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertTrue(rule.canRead());
    }

    @Test
    void canModify() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canModify());
    }

    @Test
    void canCreate() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canCreate());
    }

    @Test
    void canDelete() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canDelete());
    }

    @Test
    void canReadAcl() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canReadAcl());
    }

    @Test
    void canEditAcl() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canEditAcl());
    }

    @Test
    void canReplicate() {
        PrincipalRule rule = new PrincipalRule(false, Permission.READ);
        assertFalse(rule.canReplicate());
    }

}