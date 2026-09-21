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
        return Component.nullToEmpty("Simple Voice Chat icons: "
                + (svcIconsAvailable() ? onOff(SelfawareConfig.svcIconsEnabled()) : "UNAVAILABLE"));
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
        return Component.nullToEmpty("DonutSMP rank: "
                + (donutRankAvailable() ? onOff(SelfawareConfig.donutRankEnabled()) : "UNAVAILABLE"));
    }

    public static boolean donutRankAvailable() {
        return ServerFormatting.isDonutServer();
    }

    public static void toggleDonutRank() {
        SelfawareConfig.setDonutRankEnabled(!SelfawareConfig.donutRankEnabled());
    }

    public static Component donutMoneyLabel() {
        return Component.nullToEmpty("DonutSMP money: "
                + (donutMoneyAvailable() ? onOff(SelfawareConfig.donutMoneyEnabled()) : "UNAVAILABLE"));
    }

    public static boolean donutMoneyAvailable() {
        return DonutMoneySupport.available() && ServerFormatting.isDonutServer();
    }

    public static void toggleDonutMoney() {
        SelfawareConfig.setDonutMoneyEnabled(!SelfawareConfig.donutMoneyEnabled());
    }

    public static int visibleRows() {
        return 6;
    }

    public static int top(int height) {
        int controlsHeight = rowStep(height) * 5 + 8 + rowHeight(height);
        int availableTop = previewTop() + 34;
        int availableHeight = Math.max(controlsHeight, previewBottom(height) - availableTop);
        return availableTop + Math.max(0, (availableHeight - controlsHeight) / 2);
    }

    public static int contentLeft(int width) {
        return (width - contentWidth(width)) / 2;
    }

    public static int contentWidth(int width) {
        return Math.min(1100, Math.max(0, width - 24));
    }

    public static int optionsLeft(int width) {
        return contentLeft(width) + contentWidth(width) / 2 + columnGap(width) / 2;
    }

    public static int optionsWidth(int width) {
        return Math.max(0, contentLeft(width) + contentWidth(width) - optionsLeft(width));
    }

    public static int buttonLeft(int width) {
        return optionsLeft(width) + Math.max(8, (optionsWidth(width) - buttonWidth(width)) / 2);
    }

    public static int buttonWidth(int width) {
        return Math.min(360, Math.max(0, optionsWidth(width) - 16));
    }

    public static int rowY(int height, int row) {
        return top(height) + row * rowStep(height);
    }

    public static int rowHeight(int height) {
        return height < 340 ? 20 : 30;
    }

    public static int rowStep(int height) {
        return height < 340 ? 24 : 38;
    }

    public static int doneY(int height) {
        return rowY(height, 5) + 8;
    }

    public static int doneWidth(int width) {
        return Math.min(160, buttonWidth(width));
    }

    public static int doneLeft(int width) {
        return optionsLeft(width) + (optionsWidth(width) - doneWidth(width)) / 2;
    }

    public static float openingSpin(long openedAt) {
        float progress = Math.min(1.0F, (System.currentTimeMillis() - openedAt) / 900.0F);
        float remaining = 1.0F - progress;
        return 360.0F * remaining * remaining * remaining;
    }

    public static int previewLeft(int width) {
        return contentLeft(width);
    }

    public static int previewRight(int width) {
        return contentLeft(width) + contentWidth(width) / 2 - columnGap(width) / 2;
    }

    public static int previewTop() {
        return 38;
    }

    public static int previewBottom(int height) {
        return height - 12;
    }

    public static int playerScale(int height) {
        return Math.min(105, Math.max(48, (previewBottom(height) - previewTop() - 46) / 2));
    }

    private static int columnGap(int width) {
        return width < 600 ? 8 : 24;
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
