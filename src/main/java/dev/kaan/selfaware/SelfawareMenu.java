package dev.kaan.selfaware;

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
        int rows = 2;
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

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }
}
