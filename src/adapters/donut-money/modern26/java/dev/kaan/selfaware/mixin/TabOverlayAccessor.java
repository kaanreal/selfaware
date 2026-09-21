package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormattingScore;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
abstract class TabOverlayAccessor {
    @Inject(method = "setFooter", at = @At("HEAD"))
    private void selfaware$rememberFooter(Component footer, CallbackInfo callback) {
        ServerFormattingScore.rememberTabFooter(footer);
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void selfaware$clearFooter(CallbackInfo callback) {
        ServerFormattingScore.rememberTabFooter(null);
    }
}
