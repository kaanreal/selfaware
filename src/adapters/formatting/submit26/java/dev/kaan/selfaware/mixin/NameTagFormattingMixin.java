package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(NameTagFeatureRenderer.class)
abstract class NameTagFormattingMixin {
    @ModifyArgs(method = "prepareText", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"))
    private static void selfaware$serverAppearance(Args args, Font font, NameTagFeatureRenderer.Submit submit) {
        ServerFormatting.Appearance style = ServerFormatting.appearance(submit.text());
        if (style != null) {
            args.set(4, style.shadow);
            if (!style.defaultBackground && (Integer) args.get(6) != 0) {
                args.set(6, style.background);
            }
        }
    }
}
