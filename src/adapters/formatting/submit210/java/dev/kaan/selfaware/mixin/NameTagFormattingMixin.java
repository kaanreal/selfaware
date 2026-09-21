package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NameTagFeatureRenderer.class)
abstract class NameTagFormattingMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"))
    private void selfaware$serverAppearance(Font font, Component text, float x, float y, int color,
            boolean shadow, Matrix4f pose, MultiBufferSource buffers, Font.DisplayMode mode,
            int background, int light) {
        ServerFormatting.Appearance style = ServerFormatting.appearance(text);
        if (style != null) {
            shadow = style.shadow;
            if (!style.defaultBackground && background != 0) {
                background = style.background;
            }
        }

        Component money = ServerFormatting.donutMoney(text);
        if (money == null) {
            font.drawInBatch(text, x, y, color, shadow, pose, buffers, mode, background, light);
            return;
        }

        int nameWidth = font.width(text);
        int moneyWidth = font.width(money);
        float moneyX = x + (nameWidth - moneyWidth) / 2.0F;
        float nameY = y - 9.0F;
        float moneyY = y;
        if (background == 0) {
            font.drawInBatch(text, x, nameY, color, shadow, pose, buffers, mode, 0, light);
            font.drawInBatch(money, moneyX, moneyY, color, shadow, pose, buffers, mode, 0, light);
            return;
        }

        float center = x + nameWidth / 2.0F;
        float left = center - Math.max(nameWidth, moneyWidth) / 2.0F - 1.0F;
        float right = center + Math.max(nameWidth, moneyWidth) / 2.0F + 1.0F;
        drawBackground(buffers, mode, pose, left, nameY - 1.0F, right, moneyY + 9.0F, background, light);
        font.drawInBatch(text, x, nameY, color, shadow, pose, buffers, mode, 0, light);
        font.drawInBatch(money, moneyX, moneyY, color, shadow, pose, buffers, mode, 0, light);
    }

    private static void drawBackground(MultiBufferSource buffers, Font.DisplayMode mode, Matrix4f pose,
            float left, float top, float right, float bottom, int color, int light) {
        RenderType type = mode == Font.DisplayMode.SEE_THROUGH
                ? RenderType.textBackgroundSeeThrough() : RenderType.textBackground();
        VertexConsumer vertices = buffers.getBuffer(type);
        vertices.addVertex(pose, left, bottom, -0.01F).setColor(color).setLight(light);
        vertices.addVertex(pose, right, bottom, -0.01F).setColor(color).setLight(light);
        vertices.addVertex(pose, right, top, -0.01F).setColor(color).setLight(light);
        vertices.addVertex(pose, left, top, -0.01F).setColor(color).setLight(light);
    }
}
