package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;

public final class DonutMoneySupport {
    private DonutMoneySupport() {}

    public static boolean available() {
        return true;
    }

    public static Component previewMoney() {
        Minecraft client = Minecraft.getInstance();
        return ServerFormatting.isDonutServer() ? ServerFormattingScore.fromTab(client) : null;
    }

    public static boolean needsNameOffset(AbstractClientPlayer player) {
        Minecraft client = Minecraft.getInstance();
        return player == client.player
                && SelfawareConfig.donutMoneyEnabled()
                && ServerFormatting.isDonutServer()
                && ServerFormattingScore.fromTab(client) != null
                && player.getScoreboard().getDisplayObjective(DisplaySlot.BELOW_NAME) == null;
    }
}
