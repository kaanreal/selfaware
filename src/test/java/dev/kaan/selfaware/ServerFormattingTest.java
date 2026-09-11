package dev.kaan.selfaware;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class ServerFormattingTest {
    @Test
    void keepsThePlayersTabFormattingWithoutAddingTheTeamTwice() {
        Component vanilla = Component.nullToEmpty("[VIP] kaanreal");
        Component tab = Component.nullToEmpty("[VIP] kaanreal").copy();
        Component selected = ServerFormatting.selectName(vanilla, tab);
        assertEquals(tab, selected);
        assertNotSame(tab, selected);
    }

    @Test
    void fallsBackToVanillaWhenTabNameIsMissingOrBlank() {
        Component vanilla = Component.nullToEmpty("kaanreal");
        assertEquals(vanilla, ServerFormatting.selectName(vanilla, null));
        assertEquals(vanilla, ServerFormatting.selectName(vanilla, Component.nullToEmpty("  ")));
    }
}
