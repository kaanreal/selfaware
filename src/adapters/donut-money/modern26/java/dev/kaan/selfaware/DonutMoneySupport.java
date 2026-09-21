package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class DonutMoneySupport {
    private DonutMoneySupport() {}

    public static boolean available() {
        return true;
    }

    public static Component previewMoney() {
        Minecraft client = Minecraft.getInstance();
        return ServerFormatting.isDonutServer() ? ServerFormattingScore.fromTab(client) : null;
    }
}
