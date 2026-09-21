package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

final class SelfawareMenuPreviewRenderer {
    private SelfawareMenuPreviewRenderer() {}

    static void render(GuiGraphics graphics, Font font, int width, int height, int mouseX, int mouseY,
                       float openingSpin) {
        int left = SelfawareMenu.previewLeft(width);
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        SelfawareMenuStyle.render(canvas(graphics, font), width, height, mouseX, mouseY);

        Minecraft client = Minecraft.getInstance();
        LivingEntity player = client.player;
        if (player != null) {
            renderPlayer(graphics, player, left, top, right, bottom, height, mouseX, mouseY, openingSpin);
        } else {
            graphics.drawCenteredString(font, Component.nullToEmpty("Join a world to preview"),
                    (left + right) / 2, bottom / 2, 0xFFBBBBBB);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void renderPlayer(GuiGraphics graphics, LivingEntity player, int left, int top, int right,
                                     int bottom, int height, int mouseX, int mouseY, float openingSpin) {
        Minecraft client = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        EntityRenderer renderer = dispatcher.getRenderer(player);
        EntityRenderState state = (EntityRenderState) renderer.createRenderState(player, 1.0F);
        state.lightCoords = 15728880;
        state.shadowPieces.clear();
        state.outlineColor = 0;

        float centerX = (left + right) / 2.0F;
        float centerY = (top + bottom) / 2.0F;
        float yaw = (float) Math.toDegrees(Math.atan((centerX - mouseX) / 90.0F)) * 0.38F;
        float pitch = (float) Math.toDegrees(Math.atan((centerY - mouseY) / 90.0F)) * 0.22F;
        if (state instanceof LivingEntityRenderState living) {
            living.bodyRot = 180.0F + yaw + openingSpin;
            living.yRot = yaw + openingSpin;
            living.xRot = living.pose == Pose.FALL_FLYING ? 0.0F : -pitch;
            living.boundingBoxWidth /= living.scale;
            living.boundingBoxHeight /= living.scale;
            living.scale = 1.0F;
        }

        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX((float) Math.toRadians(pitch));
        pose.mul(camera);
        Vector3f offset = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
        graphics.submitEntityRenderState(state, SelfawareMenu.playerScale(height), offset, pose, camera,
                left + 12, top + 48, right - 12, bottom - 8);
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
