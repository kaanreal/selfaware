package dev.kaan.selfaware;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

final class SelfawareMenuPreviewRenderer {
    private SelfawareMenuPreviewRenderer() {}

    static void render(PoseStack poseStack, Font font, int width, int height, int mouseX, int mouseY) {
        int left = SelfawareMenu.previewLeft();
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        GuiComponent.fill(poseStack, left, top, right, bottom, 0x88000000);
        GuiComponent.drawString(poseStack, font, Component.nullToEmpty("Preview"), left + 10, top + 10, 0xFFFFFFFF);

        Minecraft client = Minecraft.getInstance();
        LivingEntity player = client.player;
        if (player != null) {
            InventoryScreen.renderEntityInInventory((left + right) / 2, bottom - 18, 105, mouseX, mouseY, player);
        } else {
            GuiComponent.drawCenteredString(poseStack, font, Component.nullToEmpty("Join a world to preview"),
                    (left + right) / 2, bottom / 2, 0xFFBBBBBB);
        }

        if (!SelfawareMenu.previewNametag()) {
            return;
        }
        Component name = SelfawareMenu.previewName();
        Component money = SelfawareMenu.previewMoney();
        Component svc = SelfawareMenu.previewSvcIcon() ? Component.nullToEmpty("SVC") : null;
        int nameWidth = font.width(name);
        int moneyWidth = money == null ? 0 : font.width(money);
        int svcWidth = svc == null ? 0 : font.width(svc) + 6;
        int boxWidth = Math.max(nameWidth + svcWidth, moneyWidth) + 12;
        int boxLeft = (left + right - boxWidth) / 2;
        int nameY = top + 28;
        int boxHeight = money == null ? 15 : 31;
        if (SelfawareMenu.previewBackground()) {
            GuiComponent.fill(poseStack, boxLeft, nameY - 3, boxLeft + boxWidth, nameY + boxHeight, 0x70000000);
        }
        GuiComponent.drawString(poseStack, font, name, (left + right - nameWidth - svcWidth) / 2, nameY, 0xFFFFFFFF);
        if (svc != null) {
            GuiComponent.drawString(poseStack, font, svc,
                    (left + right + nameWidth - svcWidth) / 2 + 3, nameY, 0xFFAAAAAA);
        }
        if (money != null) {
            GuiComponent.drawCenteredString(poseStack, font, money, (left + right) / 2, nameY + 15, 0xFFFFFFFF);
        }
    }
}
