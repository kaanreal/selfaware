package dev.kaan.selfaware;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
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
        int buttonHeight = SelfawareMenu.rowHeight(height);
        nametagButton = transparent(new Button(x, SelfawareMenu.rowY(height, 0), buttonWidth, buttonHeight,
                SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }));
        svcIconsButton = transparent(new Button(x, SelfawareMenu.rowY(height, 1), buttonWidth, buttonHeight,
                SelfawareMenu.svcIconsLabel(), button -> {
                    SelfawareMenu.toggleSvcIcons();
                    svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
                }));
        svcIconsButton.active = SelfawareMenu.svcIconsAvailable();
        serverFormattingButton = transparent(new Button(x, SelfawareMenu.rowY(height, 2), buttonWidth, buttonHeight,
                SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }));
        Button donutRankButton = transparent(new Button(x, SelfawareMenu.rowY(height, 3), buttonWidth, buttonHeight,
                SelfawareMenu.donutRankLabel(), button -> {
                    SelfawareMenu.toggleDonutRank();
                    button.setMessage(SelfawareMenu.donutRankLabel());
                }));
        donutRankButton.active = SelfawareMenu.donutRankAvailable();
        donutMoneyButton = transparent(new Button(x, SelfawareMenu.rowY(height, 4), buttonWidth, buttonHeight,
                SelfawareMenu.donutMoneyLabel(), button -> {
                    SelfawareMenu.toggleDonutMoney();
                    donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
                }));
        donutMoneyButton.active = SelfawareMenu.donutMoneyAvailable();
        transparent(new Button(SelfawareMenu.doneLeft(width), SelfawareMenu.doneY(height),
                SelfawareMenu.doneWidth(width), buttonHeight, SelfawareMenu.doneLabel(), button -> onClose()));
    }

    private Button transparent(Button button) {
        button.setAlpha(0.0F);
        return addButton(button);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        SelfawareMenuPreviewRenderer.render(poseStack, font, width, height, mouseX, mouseY,
                SelfawareMenu.openingSpin(openedAt));
        GuiComponent.drawCenteredString(poseStack, font, SelfawareMenu.title(), width / 2, 14, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
