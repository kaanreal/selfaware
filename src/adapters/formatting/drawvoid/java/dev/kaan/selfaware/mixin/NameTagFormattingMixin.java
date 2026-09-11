package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
abstract class NameTagFormattingMixin {
    @ModifyArgs(method = "renderNameTag", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)V"))
    private void selfaware$serverAppearance(Args args) {
        ServerFormatting.Appearance style = ServerFormatting.appearance((Component) args.get(0));
        if (style != null) {
            args.set(4, style.shadow);
            // Only the first vanilla pass draws a background.
            if (!style.defaultBackground && (Integer) args.get(8) != 0) {
                args.set(8, style.background);
            }
        }
    }
}
