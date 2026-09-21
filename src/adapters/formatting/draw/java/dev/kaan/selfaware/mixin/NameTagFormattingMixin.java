package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
abstract class NameTagFormattingMixin {
    @Redirect(method = "renderNameTag", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"))
    private int selfaware$serverAppearance(Font font, Component text, float x, float y, int color, boolean shadow,
                                           Matrix4f matrix, MultiBufferSource buffers, Font.DisplayMode mode,
                                           int background, int light) {
        ServerFormatting.Appearance style = ServerFormatting.appearance(text);
        if (style != null) {
            shadow = style.shadow;
            // Only the first vanilla pass draws a background.
            if (!style.defaultBackground && background != 0) {
                background = style.background;
            }
        }
        return font.drawInBatch(text, x, y, color, shadow, matrix, buffers, mode, background, light);
    }
}
