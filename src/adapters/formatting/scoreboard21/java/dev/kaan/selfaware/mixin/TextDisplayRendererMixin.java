package dev.kaan.selfaware.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kaan.selfaware.ServerFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisplayRenderer.TextDisplayRenderer.class)
abstract class TextDisplayRendererMixin {
    @Shadow @Final private Font font;

    @Inject(method = "getSubState(Lnet/minecraft/world/entity/Display$TextDisplay;)Lnet/minecraft/world/entity/Display$TextDisplay$TextRenderState;", at = @At("RETURN"), cancellable = true)
    private void selfaware$overrideOwnText(Display.TextDisplay entity,
            CallbackInfoReturnable<Display.TextDisplay.TextRenderState> callback) {
        ServerFormatting.TextDisplayOverride override = ServerFormatting.textDisplayOverride(entity);
        Display.TextDisplay.TextRenderState original = callback.getReturnValue();
        if (override == null || override.hidden || override.text == null || original == null) {
            return;
        }

        byte flags = original.flags();
        Display.IntInterpolator opacity = original.textOpacity();
        Display.IntInterpolator background = original.backgroundColor();
        if (!override.serverAppearance) {
            flags = defaultFlags(flags);
            opacity = Display.IntInterpolator.constant(-1);
            background = Display.IntInterpolator.constant(Display.TextDisplay.INITIAL_BACKGROUND);
        }
        callback.setReturnValue(new Display.TextDisplay.TextRenderState(override.text, original.lineWidth(),
                opacity, background, flags));
    }

    @Inject(method = "renderInner(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/world/entity/Display$TextDisplay$TextRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", at = @At("HEAD"), cancellable = true)
    private void selfaware$hideOwnDisplay(Display.TextDisplay entity, Display.TextDisplay.TextRenderState state,
            PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick, CallbackInfo callback) {
        ServerFormatting.TextDisplayOverride override = ServerFormatting.textDisplayOverride(entity);
        if (override != null && override.hidden) {
            callback.cancel();
        }
    }

    @Redirect(method = "renderInner(Lnet/minecraft/world/entity/Display$TextDisplay;Lnet/minecraft/world/entity/Display$TextDisplay$TextRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Display$TextDisplay;cacheDisplay(Lnet/minecraft/world/entity/Display$TextDisplay$LineSplitter;)Lnet/minecraft/world/entity/Display$TextDisplay$CachedInfo;"))
    private Display.TextDisplay.CachedInfo selfaware$overrideOwnLines(Display.TextDisplay entity,
            Display.TextDisplay.LineSplitter splitter) {
        ServerFormatting.TextDisplayOverride override = ServerFormatting.textDisplayOverride(entity);
        Display.TextDisplay.TextRenderState state = entity.textRenderState();
        if (override == null || override.text == null || state == null) {
            return entity.cacheDisplay(splitter);
        }
        return splitLines(override.text, state.lineWidth());
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
