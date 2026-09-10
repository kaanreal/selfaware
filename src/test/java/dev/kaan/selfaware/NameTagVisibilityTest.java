package dev.kaan.selfaware;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NameTagVisibilityTest {
    @Test
    void showsInThirdPerson() {
        assertTrue(NameTagVisibility.visible(true, false, false, false, false, false, 0));
    }

    @Test
    void hidesInFirstPersonAndWithHiddenHud() {
        assertFalse(NameTagVisibility.visible(false, false, false, false, false, false, 0));
        assertFalse(NameTagVisibility.visible(true, true, false, false, false, false, 0));
    }

    @Test
    void hidesInvisibleSpectatorAndTeamRestrictedPlayers() {
        assertFalse(NameTagVisibility.visible(true, false, true, false, false, false, 0));
        assertFalse(NameTagVisibility.visible(true, false, false, true, false, false, 0));
        assertFalse(NameTagVisibility.visible(true, false, false, false, true, false, 0));
    }

    @Test
    void keepsVanillaDistanceLimits() {
        assertTrue(NameTagVisibility.visible(true, false, false, false, false, false, 4095));
        assertFalse(NameTagVisibility.visible(true, false, false, false, false, false, 4096));
        assertTrue(NameTagVisibility.visible(true, false, false, false, false, true, 1023));
        assertFalse(NameTagVisibility.visible(true, false, false, false, false, true, 1024));
        assertFalse(NameTagVisibility.visible(true, false, false, false, false, false, Double.NaN));
    }
}
