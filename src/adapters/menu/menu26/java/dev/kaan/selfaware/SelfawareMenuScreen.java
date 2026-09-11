package dev.kaan.selfaware;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public final class SelfawareMenuScreen extends Screen {
    private final Screen parent;
    private Button nametagButton;
    private Button svcIconsButton;

    public SelfawareMenuScreen(Screen parent) {
        super(SelfawareMenu.title());
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 125;
        int y = height / 2 - 44;
        nametagButton = addRenderableWidget(Button.builder(SelfawareMenu.nametagLabel(), button -> {
            SelfawareMenu.toggleNametag();
            nametagButton.setMessage(SelfawareMenu.nametagLabel());
        }).bounds(x, y, 250, 20).build());
        svcIconsButton = addRenderableWidget(Button.builder(SelfawareMenu.svcIconsLabel(), button -> {
            SelfawareMenu.toggleSvcIcons();
            svcIconsButton.setMessage(SelfawareMenu.svcIconsLabel());
        }).bounds(x, y + 26, 250, 20).build());
        addRenderableWidget(Button.builder(SelfawareMenu.doneLabel(), button -> onClose())
                .bounds(x, y + 60, 250, 20)
                .build());
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        graphics.centeredText(font, SelfawareMenu.title(), width / 2, height / 2 - 78, 0xFFFFFF);
    }
}
