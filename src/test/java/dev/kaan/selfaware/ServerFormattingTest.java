package dev.kaan.selfaware;

import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void limitsDonutMoneyToTheDonutServerAddress() {
        assertTrue(ServerFormatting.isDonutAddress("donutsmp.net:25565"));
        assertTrue(ServerFormatting.isDonutAddress("play.donutsmp.net"));
        assertFalse(ServerFormatting.isDonutAddress("example.donutsmp.com"));
        assertFalse(ServerFormatting.isDonutAddress("singleplayer"));
    }
}
