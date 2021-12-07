package de.rockware.aem.rat.core.api.config;

import de.rockware.aem.rat.core.api.caconfig.GlobalRATConfigImpl;
import de.rockware.aem.rat.core.api.caconfig.TenantRATConfigImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RichConfigurationTest {

    private RichConfiguration richConfig;

    @BeforeEach
    void setUp() {
        richConfig = new RichConfiguration(new TenantRATConfigImpl(), new GlobalRATConfigImpl(), "");
    }

    @Test
    void needsCreation() {
        assertTrue(richConfig.needsCreation(ResourcePathType.COMMUNITIES));
        assertFalse(richConfig.needsCreation(null));
    }

    @Test
    void isValidLevel() {
        assertTrue(richConfig.isValidLevel(4));
        assertFalse(richConfig.isValidLevel(1));
        assertFalse(richConfig.isValidLevel(0));
        assertFalse(richConfig.isValidLevel(110));
    }

    @Test
    void isReadAccessLevel() {
        assertTrue(richConfig.isReadAccessLevel(1));
        assertFalse(richConfig.isReadAccessLevel(11));
        assertFalse(richConfig.isReadAccessLevel(0));
        assertFalse(richConfig.isReadAccessLevel(-10));
    }

    @Test
    void getPath() {
        assertNotNull(richConfig.getPath());
        assertNotNull(ResourcePathType.DAM.getPath());
    }

}