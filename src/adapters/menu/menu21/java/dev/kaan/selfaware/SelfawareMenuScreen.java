package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
        nametagButton = addRenderableWidget(Button.builder(SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }).bounds(x, y + row++ * 26, 250, 20).build());
        if (SelfawareMenu.svcIconsAvailable()) {
            svcIconsButton = addRenderableWidget(Button.builder(SelfawareMenu.svcIconsLabel(), button -> {
                SelfawareMenu.toggleSvcIcons();
                svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
            }).bounds(x, y + row++ * 26, 250, 20).build());
        }
        serverFormattingButton = addRenderableWidget(Button.builder(SelfawareMenu.serverFormattingLabel(), button -> {
            SelfawareMenu.toggleServerFormatting();
            serverFormattingButton.setMessage(SelfawareMenu.serverFormattingLabel());
        }).bounds(x, y + row++ * 26, 250, 20).build());
        if (SelfawareMenu.donutRankAvailable()) {
            addRenderableWidget(Button.builder(SelfawareMenu.donutRankLabel(), button -> {
                SelfawareMenu.toggleDonutRank();
                button.setMessage(SelfawareMenu.donutRankLabel());
            }).bounds(x, y + row++ * 26, 250, 20).build());
        }
        if (SelfawareMenu.donutMoneyAvailable()) {
            donutMoneyButton = addRenderableWidget(Button.builder(SelfawareMenu.donutMoneyLabel(), button -> {
                SelfawareMenu.toggleDonutMoney();
                donutMoneyButton.setMessage(SelfawareMenu.donutMoneyLabel());
            }).bounds(x, y + row++ * 26, 250, 20).build());
        }
        addRenderableWidget(Button.builder(SelfawareMenu.doneLabel(), button -> onClose())
                .bounds(x, y + row * 26 + 8, 250, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, SelfawareMenu.title(), width / 2, SelfawareMenu.top(height) - 24, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }
}
