package dev.kaan.selfaware;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

final class SelfawareMenuPreviewRenderer {
    private SelfawareMenuPreviewRenderer() {}

    static void render(PoseStack poseStack, Font font, int width, int height, int mouseX, int mouseY,
                       float openingSpin) {
        int left = SelfawareMenu.previewLeft(width);
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        SelfawareMenuStyle.render(canvas(poseStack, font), width, height, mouseX, mouseY);

        Minecraft client = Minecraft.getInstance();
        LivingEntity player = client.player;
        if (player != null) {
            int centerX = (left + right) / 2;
            float centerY = (top + bottom) / 2.0F;
            float pointerX = Math.max(left, Math.min(right, mouseX));
            float pointerY = Math.max(top, Math.min(bottom, mouseY));
            float idleX = centerX + (float) Math.sin(System.currentTimeMillis() * 0.0008D)
                    * Math.min(90.0F, (right - left) * 0.22F);
            float idleY = centerY + (float) Math.sin(System.currentTimeMillis() * 0.00045D) * 8.0F;
            float renderMouseX = idleX + (pointerX - centerX) * 0.35F;
            float renderMouseY = idleY + (pointerY - centerY) * 0.15F;
            renderPlayer(centerX, bottom - 18, SelfawareMenu.playerScale(height), centerX - renderMouseX,
                    centerY - renderMouseY, openingSpin, player);
        } else {
            GuiComponent.drawCenteredString(poseStack, font, Component.nullToEmpty("Join a world to preview"),
                    (left + right) / 2, bottom / 2, 0xFFBBBBBB);
        }
    }

    private static void renderPlayer(int x, int y, int scale, float mouseX, float mouseY, float openingSpin,
                                     LivingEntity player) {
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan(mouseY / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0D, 0.0D, 1000.0D);
        poseStack.scale(scale, scale, scale);
        Quaternion pose = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion camera = Vector3f.XP.rotationDegrees(pitch * 20.0F);
        pose.mul(camera);
        poseStack.mulPose(pose);

        float bodyRot = player.yBodyRot;
        float yRot = player.yRot;
        float xRot = player.xRot;
        float headRotOld = player.yHeadRotO;
        float headRot = player.yHeadRot;
        player.yBodyRot = 180.0F + yaw * 20.0F + openingSpin;
        player.yRot = 180.0F + yaw * 40.0F + openingSpin;
        player.xRot = -pitch * 20.0F;
        player.yHeadRot = player.yRot;
        player.yHeadRotO = player.yRot;

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        camera.conj();
        dispatcher.overrideCameraOrientation(camera);
        dispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> dispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F,
                poseStack, buffers, 15728880));
        buffers.endBatch();
        dispatcher.setRenderShadow(true);
        player.yBodyRot = bodyRot;
        player.yRot = yRot;
        player.xRot = xRot;
        player.yHeadRotO = headRotOld;
        player.yHeadRot = headRot;
        RenderSystem.popMatrix();
    }

    private static SelfawareMenuStyle.Canvas canvas(PoseStack poseStack, Font font) {
        return new SelfawareMenuStyle.Canvas() {
            @Override
            public void fill(int left, int top, int right, int bottom, int color) {
                GuiComponent.fill(poseStack, left, top, right, bottom, color);
            }

            @Override
            public void text(Component text, int x, int y, int color) {
                GuiComponent.drawString(poseStack, font, text, x, y, color);
            }

            @Override
            public void centeredText(Component text, int x, int y, int color) {
                GuiComponent.drawCenteredString(poseStack, font, text, x, y, color);
            }
        };
    }
}
