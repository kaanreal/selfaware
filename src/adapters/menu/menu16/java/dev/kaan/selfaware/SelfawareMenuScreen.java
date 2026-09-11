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
        nametagButton = addButton(new Button(x, y, 250, 20, SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }));
        svcIconsButton = addButton(new Button(x, y + 26, 250, 20, SelfawareMenu.svcIconsLabel(), button -> {
            SelfawareMenu.toggleSvcIcons();
            svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
        }));
        serverFormattingButton = addButton(new Button(x, y + 52, 250, 20, SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }));
        int doneOffset = 86;
        if (SelfawareMenu.donutMoneyAvailable()) {
            donutMoneyButton = addButton(new Button(x, y + 78, 250, 20, SelfawareMenu.donutMoneyLabel(), button -> {
                SelfawareMenu.toggleDonutMoney();
                donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
            }));
            doneOffset = 112;
        }
        addButton(new Button(x, y + doneOffset, 250, 20, SelfawareMenu.doneLabel(), button -> onClose()));
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
