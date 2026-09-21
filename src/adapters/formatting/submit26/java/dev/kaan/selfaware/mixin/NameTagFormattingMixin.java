package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.PreparedTextRows;
import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NameTagFeatureRenderer.class)
abstract class NameTagFormattingMixin {
    @Redirect(method = "prepareText", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;"))
    private static Font.PreparedText selfaware$prepareText(Font font, FormattedCharSequence text,
            float x, float y, int color, boolean shadow, boolean includeEmpty, int background,
            Font ignored, NameTagFeatureRenderer.Submit submit) {
        ServerFormatting.Appearance style = ServerFormatting.appearance(submit.text());
        if (style != null) {
            shadow = style.shadow;
            if (!style.defaultBackground && background != 0) {
                background = style.background;
            }
        }

        Component money = ServerFormatting.donutMoney(submit.text());
        if (money == null) {
            return font.prepareText(text, x, y, color, shadow, includeEmpty, background);
        }

        int spaceWidth = Math.max(1, font.width(" "));
        int targetWidth = font.width(submit.text());
        Component nameRow = submit.text().copy();
        Component moneyRow = pad(font, money, targetWidth, spaceWidth);
        Font.PreparedText name = font.prepareText(nameRow.getVisualOrderText(), -font.width(nameRow) / 2.0F,
                y - 10.0F, color, shadow, includeEmpty, background);
        Font.PreparedText balance = font.prepareText(moneyRow.getVisualOrderText(), -font.width(moneyRow) / 2.0F,
                y, color, shadow, includeEmpty, 0);
        return new PreparedTextRows(name, balance, background != 0);
    }

    private static Component pad(Font font, Component text, int targetWidth, int spaceWidth) {
        int spaces = Math.max(0, (targetWidth - font.width(text) + spaceWidth - 1) / spaceWidth);
        int left = spaces / 2;
        int right = spaces - left;
        String padding = "\u00A0";
        return Component.literal(padding.repeat(left)).append(text.copy()).append(padding.repeat(right));
    }
}
