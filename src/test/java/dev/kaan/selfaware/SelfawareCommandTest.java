package dev.kaan.selfaware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelfawareCommandTest {
    @Test
    void matchesOnlyTheSelfawareCommand() {
        assertTrue(SelfawareCommand.matches("/selfaware"));
        assertTrue(SelfawareCommand.matches(" /SELFaware "));
        assertFalse(SelfawareCommand.matches("/selfaware now"));
        assertFalse(SelfawareCommand.matches("selfaware"));
        assertFalse(SelfawareCommand.matches(null));
    }
}
