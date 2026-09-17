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
        int x = width / 2 - 125;
        int y = SelfawareMenu.top(height);
        int row = 0;
        nametagButton = addRenderableWidget(new Button(x, y + row++ * 26, 250, 20, SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }));
        if (SelfawareMenu.svcIconsAvailable()) {
            svcIconsButton = addRenderableWidget(new Button(x, y + row++ * 26, 250, 20,
                    SelfawareMenu.svcIconsLabel(), button -> {
                        SelfawareMenu.toggleSvcIcons();
                        svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
                    }));
        }
        serverFormattingButton = addRenderableWidget(new Button(x, y + row++ * 26, 250, 20,
                SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }));
        if (SelfawareMenu.donutRankAvailable()) {
            addRenderableWidget(new Button(x, y + row++ * 26, 250, 20, SelfawareMenu.donutRankLabel(), button -> {
                SelfawareMenu.toggleDonutRank();
                button.setMessage(SelfawareMenu.donutRankLabel());
            }));
        }
        if (SelfawareMenu.donutMoneyAvailable()) {
            donutMoneyButton = addRenderableWidget(new Button(x, y + row++ * 26, 250, 20,
                    SelfawareMenu.donutMoneyLabel(), button -> {
                SelfawareMenu.toggleDonutMoney();
                donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
            }));
        }
        addRenderableWidget(new Button(x, y + row * 26 + 8, 250, 20, SelfawareMenu.doneLabel(), button -> onClose()));
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        GuiComponent.drawCenteredString(poseStack, font, SelfawareMenu.title(), width / 2,
                SelfawareMenu.top(height) - 24, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
