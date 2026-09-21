package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.TextDisplayEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
abstract class TextDisplayRendererMixin {
    @Shadow @Final private Font font;

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/client/renderer/entity/state/TextDisplayEntityRenderState;F)V", at = @At("TAIL"))
    private void selfaware$overrideOwnDisplay(Display.TextDisplay entity, TextDisplayEntityRenderState state,
            float partialTick, CallbackInfo callback) {
        ServerFormatting.TextDisplayOverride override = ServerFormatting.textDisplayOverride(entity);
        if (override == null) {
            return;
        }
        if (override.hidden) {
            state.textRenderState = null;
            state.cachedInfo = null;
            return;
        }
        if (override.text == null || state.textRenderState == null) {
            return;
        }

        Display.TextDisplay.TextRenderState original = state.textRenderState;
        byte flags = original.flags();
        Display.IntInterpolator opacity = original.textOpacity();
        Display.IntInterpolator background = original.backgroundColor();
        if (!override.serverAppearance) {
            flags = defaultFlags(flags);
            opacity = Display.IntInterpolator.constant(-1);
            background = Display.IntInterpolator.constant(Display.TextDisplay.INITIAL_BACKGROUND);
        }
        state.textRenderState = new Display.TextDisplay.TextRenderState(override.text, original.lineWidth(),
                opacity, background, flags);
        state.cachedInfo = splitLines(override.text, original.lineWidth());
    }

    private byte defaultFlags(byte original) {
        return (byte) ((original & Display.TextDisplay.FLAG_SEE_THROUGH)
                | Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND);
    }

    private Display.TextDisplay.CachedInfo splitLines(Component text, int lineWidth) {
        List<FormattedCharSequence> split = font.split(text, lineWidth);
        List<Display.TextDisplay.CachedLine> lines = new ArrayList<>(split.size());
        int width = 0;
        for (FormattedCharSequence line : split) {
            int lineWidthPixels = font.width(line);
            width = Math.max(width, lineWidthPixels);
            lines.add(new Display.TextDisplay.CachedLine(line, lineWidthPixels));
        }
        return new Display.TextDisplay.CachedInfo(lines, width);
    }
}
