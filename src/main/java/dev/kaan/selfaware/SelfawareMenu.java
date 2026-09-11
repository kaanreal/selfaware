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

    public static Component nametagLabel() {
        return Component.nullToEmpty("Nametag: " + onOff(SelfawareConfig.nametagEnabled()));
    }

    public static Component svcIconsLabel() {
        return Component.nullToEmpty("Simple Voice Chat icons: " + onOff(SelfawareConfig.svcIconsEnabled()));
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

    private static String onOff(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }
}
