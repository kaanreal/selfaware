package dev.kaan.selfaware;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public final class SelfawareMenuScreen extends Screen {
    private final Screen parent;
    private Button nametagButton;
    private Button svcIconsButton;
    private Button serverFormattingButton;
    private Button donutMoneyButton;

    public SelfawareMenuScreen(Screen parent) {
        super(SelfawareMenu.title());
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 125;
        int y = height / 2 - 57;
        nametagButton = addRenderableWidget(Button.builder(SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }).bounds(x, y, 250, 20).build());
        svcIconsButton = addRenderableWidget(Button.builder(SelfawareMenu.svcIconsLabel(), button -> {
            SelfawareMenu.toggleSvcIcons();
            svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
        }).bounds(x, y + 26, 250, 20).build());
        serverFormattingButton = addRenderableWidget(Button.builder(SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }).bounds(x, y + 52, 250, 20).build());
        int doneOffset = 86;
        if (SelfawareMenu.donutMoneyAvailable()) {
            donutMoneyButton = addRenderableWidget(Button.builder(SelfawareMenu.donutMoneyLabel(), button -> {
                SelfawareMenu.toggleDonutMoney();
                donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
            }).bounds(x, y + 78, 250, 20).build());
            doneOffset = 112;
        }
        addRenderableWidget(Button.builder(SelfawareMenu.doneLabel(), button -> onClose())
                .bounds(x, y + doneOffset, 250, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        GuiComponent.drawCenteredString(poseStack, font, SelfawareMenu.title(), width / 2, height / 2 - 91, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
