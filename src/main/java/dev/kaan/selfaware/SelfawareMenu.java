package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public final class SelfawareMenu {
    private SelfawareMenu() {}

    public static Component title() {
        return Component.nullToEmpty("Selfaware");
    }

    public static Component doneLabel() {
        return Component.nullToEmpty("Done");
    }

    public static void open() {
        SelfawareMenuScreen.open();
    }

    public static Component nametagLabel() {
        return Component.nullToEmpty("Nametag: " + onOff(SelfawareConfig.nametagEnabled()));
    }

    public static Component svcIconsLabel() {
        return Component.nullToEmpty("Simple Voice Chat icons: " + onOff(SelfawareConfig.svcIconsEnabled()));
    }

    public static boolean svcIconsAvailable() {
        return SimpleVoiceChatAvailability.available();
    }

    public static void toggleNametag() {
        SelfawareConfig.setNametagEnabled(!SelfawareConfig.nametagEnabled());
    }

    public static void toggleSvcIcons() {
        SelfawareConfig.setSvcIconsEnabled(!SelfawareConfig.svcIconsEnabled());
    }

    public static Component serverFormattingLabel() {
        return Component.nullToEmpty("Server formatting: " + onOff(SelfawareConfig.serverFormattingEnabled()));
    }

    public static void toggleServerFormatting() {
        SelfawareConfig.setServerFormattingEnabled(!SelfawareConfig.serverFormattingEnabled());
    }

    public static Component donutRankLabel() {
        return Component.nullToEmpty("DonutSMP rank: " + onOff(SelfawareConfig.donutRankEnabled()));
    }

    public static boolean donutRankAvailable() {
        return ServerFormatting.isDonutServer();
    }

    public static void toggleDonutRank() {
        SelfawareConfig.setDonutRankEnabled(!SelfawareConfig.donutRankEnabled());
    }

    public static Component donutMoneyLabel() {
        return Component.nullToEmpty("DonutSMP money: " + onOff(SelfawareConfig.donutMoneyEnabled()));
    }

    public static boolean donutMoneyAvailable() {
        return DonutMoneySupport.available() && ServerFormatting.isDonutServer();
    }

    public static void toggleDonutMoney() {
        SelfawareConfig.setDonutMoneyEnabled(!SelfawareConfig.donutMoneyEnabled());
    }

    public static int visibleRows() {
        int rows = 3;
        if (svcIconsAvailable()) {
            rows++;
        }
        if (donutRankAvailable()) {
            rows++;
        }
        if (donutMoneyAvailable()) {
            rows++;
        }
        return rows;
    }

    public static int top(int height) {
        return height / 2 - (visibleRows() * 26 + 8) / 2;
    }

    public static int optionsLeft(int width) {
        return width / 2 + 12;
    }

    public static int optionsWidth(int width) {
        return Math.max(180, Math.min(280, width - optionsLeft(width) - 24));
    }

    public static int previewLeft() {
        return 24;
    }

    public static int previewRight(int width) {
        return width / 2 - 12;
    }

    public static int previewTop() {
        return 48;
    }

    public static int previewBottom(int height) {
        return height - 28;
    }

    public static Component previewName() {
        Minecraft client = Minecraft.getInstance();
        Component plain = client.player == null ? Component.nullToEmpty("Kaanreal") : client.player.getName().copy();
        boolean donut = ServerFormatting.isDonutServer();
        PlayerInfo info = client.getConnection() == null || client.player == null
                ? null : client.getConnection().getPlayerInfo(client.player.getUUID());
        Component tab = info == null ? null : info.getTabListDisplayName();
        if (donut) {
            return ServerFormatting.selectDonutName(plain, tab, SelfawareConfig.donutRankEnabled());
        }
        return SelfawareConfig.serverFormattingEnabled() ? ServerFormatting.selectName(plain, tab) : plain;
    }

    public static Component previewMoney() {
        if (!donutMoneyAvailable() || !SelfawareConfig.donutMoneyEnabled()) {
            return null;
        }
        Component money = DonutMoneySupport.previewMoney();
        return money == null ? Component.nullToEmpty("$ 196M") : money;
    }

    public static boolean previewBackground() {
        return SelfawareConfig.serverFormattingEnabled();
    }

    public static boolean previewNametag() {
        return SelfawareConfig.nametagEnabled();
    }

    public static boolean previewSvcIcon() {
        return svcIconsAvailable() && SelfawareConfig.svcIconsEnabled();
    }

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }
}
