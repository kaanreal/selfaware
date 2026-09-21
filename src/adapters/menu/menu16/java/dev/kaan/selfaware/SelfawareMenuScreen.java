package dev.kaan.selfaware;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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

    public static void open() {
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof SelfawareMenuScreen)) {
            client.setScreen(new SelfawareMenuScreen(client.screen));
        }
    }

    @Override
    protected void init() {
        int x = SelfawareMenu.optionsLeft(width);
        int buttonWidth = SelfawareMenu.optionsWidth(width);
        int y = SelfawareMenu.top(height);
        int row = 0;
        nametagButton = addButton(new Button(x, y + row++ * 26, buttonWidth, 20, SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }));
        if (SelfawareMenu.svcIconsAvailable()) {
            svcIconsButton = addButton(new Button(x, y + row++ * 26, buttonWidth, 20,
                    SelfawareMenu.svcIconsLabel(), button -> {
                        SelfawareMenu.toggleSvcIcons();
                        svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
                    }));
        }
        serverFormattingButton = addButton(new Button(x, y + row++ * 26, buttonWidth, 20,
                SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }));
        if (SelfawareMenu.donutRankAvailable()) {
            addButton(new Button(x, y + row++ * 26, buttonWidth, 20, SelfawareMenu.donutRankLabel(), button -> {
                SelfawareMenu.toggleDonutRank();
                button.setMessage(SelfawareMenu.donutRankLabel());
            }));
        }
        if (SelfawareMenu.donutMoneyAvailable()) {
            donutMoneyButton = addButton(new Button(x, y + row++ * 26, buttonWidth, 20,
                    SelfawareMenu.donutMoneyLabel(), button -> {
                SelfawareMenu.toggleDonutMoney();
                donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
            }));
        }
        addButton(new Button(x, y + row * 26 + 8, buttonWidth, 20, SelfawareMenu.doneLabel(), button -> onClose()));
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        SelfawareMenuPreviewRenderer.render(poseStack, font, width, height, mouseX, mouseY);
        GuiComponent.drawCenteredString(poseStack, font, SelfawareMenu.title(), width / 2, 20, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
