package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;

final class SelfawareMenuPreviewRenderer {
    private SelfawareMenuPreviewRenderer() {}

    static void render(GuiGraphics graphics, Font font, int width, int height, int mouseX, int mouseY,
                       float openingSpin) {
        SelfawareMenuStyle.render(canvas(graphics, font), width, height, mouseX, mouseY);

        int left = SelfawareMenu.previewLeft(width);
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        LivingEntity player = Minecraft.getInstance().player;
        if (player == null) {
            graphics.drawCenteredString(font, Component.nullToEmpty("Join a world to preview"),
                    (left + right) / 2, (top + bottom) / 2, 0xFFBBBBBB);
            return;
        }

        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;
        float yaw = (float) Math.toDegrees(Math.atan((centerX - mouseX) / 90.0F)) * 0.38F;
        float pitch = (float) Math.toDegrees(Math.atan((centerY - mouseY) / 90.0F)) * 0.22F;
        float oldBodyRot = player.yBodyRot;
        float oldYRot = player.getYRot();
        float oldXRot = player.getXRot();
        float oldHeadRot = player.yHeadRot;
        float oldHeadRotO = player.yHeadRotO;
        player.yBodyRot = 180.0F + yaw + openingSpin;
        player.setYRot(180.0F + yaw + openingSpin);
        player.setXRot(-pitch);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX((float) Math.toRadians(pitch));
        pose.mul(camera);
        InventoryScreen.renderEntityInInventory(graphics, (int) centerX, bottom - 10,
                SelfawareMenu.playerScale(height), pose, camera, player);

        player.yBodyRot = oldBodyRot;
        player.setYRot(oldYRot);
        player.setXRot(oldXRot);
        player.yHeadRot = oldHeadRot;
        player.yHeadRotO = oldHeadRotO;
    }

    private static SelfawareMenuStyle.Canvas canvas(GuiGraphics graphics, Font font) {
        return new SelfawareMenuStyle.Canvas() {
            @Override
            public void fill(int left, int top, int right, int bottom, int color) {
                graphics.fill(left, top, right, bottom, color);
            }

            @Override
            public void text(Component text, int x, int y, int color) {
                graphics.drawString(font, text, x, y, color);
            }

            @Override
            public void centeredText(Component text, int x, int y, int color) {
                graphics.drawCenteredString(font, text, x, y, color);
            }
        };
    }
}
