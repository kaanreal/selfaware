package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public final class SelfawareMenuScreen extends Screen {
    private final Screen parent;
    private final long openedAt = System.currentTimeMillis();
    private Button nametagButton;
    private Button svcIconsButton;
    private Button serverFormattingButton;
    private Button donutMoneyButton;

    public SelfawareMenuScreen(Screen parent) {
        super(SelfawareMenu.title());
        this.parent = parent;
    }

    public static void open() {
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof SelfawareMenuScreen)) {
            client.setScreen(new SelfawareMenuScreen(client.screen));
        }
    }

    @Override
    protected void init() {
        int x = SelfawareMenu.buttonLeft(width);
        int buttonWidth = SelfawareMenu.buttonWidth(width);
        nametagButton = transparent(Button.builder(SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }).bounds(x, SelfawareMenu.rowY(height, 0), buttonWidth, SelfawareMenu.rowHeight(height)).build());
        svcIconsButton = transparent(Button.builder(SelfawareMenu.svcIconsLabel(), button -> {
            SelfawareMenu.toggleSvcIcons();
            svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
        }).bounds(x, SelfawareMenu.rowY(height, 1), buttonWidth, SelfawareMenu.rowHeight(height)).build());
        svcIconsButton.active = SelfawareMenu.svcIconsAvailable();
        serverFormattingButton = transparent(Button.builder(SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }).bounds(x, SelfawareMenu.rowY(height, 2), buttonWidth, SelfawareMenu.rowHeight(height)).build());
        Button donutRankButton = transparent(Button.builder(SelfawareMenu.donutRankLabel(), button -> {
            SelfawareMenu.toggleDonutRank();
            button.setMessage(SelfawareMenu.donutRankLabel());
        }).bounds(x, SelfawareMenu.rowY(height, 3), buttonWidth, SelfawareMenu.rowHeight(height)).build());
        donutRankButton.active = SelfawareMenu.donutRankAvailable();
        donutMoneyButton = transparent(Button.builder(SelfawareMenu.donutMoneyLabel(), button -> {
            SelfawareMenu.toggleDonutMoney();
            donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
        }).bounds(x, SelfawareMenu.rowY(height, 4), buttonWidth, SelfawareMenu.rowHeight(height)).build());
        donutMoneyButton.active = SelfawareMenu.donutMoneyAvailable();
        transparent(Button.builder(SelfawareMenu.doneLabel(), button -> onClose())
                .bounds(SelfawareMenu.doneLeft(width), SelfawareMenu.doneY(height),
                        SelfawareMenu.doneWidth(width), SelfawareMenu.rowHeight(height)).build());
    }

    private Button transparent(Button button) {
        button.setAlpha(0.0F);
        return addRenderableWidget(button);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        SelfawareMenuPreviewRenderer.render(graphics, font, width, height, mouseX, mouseY,
                SelfawareMenu.openingSpin(openedAt));
        graphics.drawCenteredString(font, SelfawareMenu.title(), width / 2, 14, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
