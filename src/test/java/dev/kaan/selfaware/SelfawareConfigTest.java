package dev.kaan.selfaware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelfawareConfigTest {
    @Test
    void parsesOnlyExplicitBooleanValues() {
        assertTrue(SelfawareConfig.parseBoolean("true", false));
        assertFalse(SelfawareConfig.parseBoolean("false", true));
        assertTrue(SelfawareConfig.parseBoolean("unexpected", true));
        assertFalse(SelfawareConfig.parseBoolean(null, false));
    }
}
