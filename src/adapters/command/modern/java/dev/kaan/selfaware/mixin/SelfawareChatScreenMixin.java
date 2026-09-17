package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfawareCommand;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
abstract class SelfawareChatScreenMixin {
    @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
    private void selfaware$openMenu(String message, boolean addToRecent, CallbackInfo ci) {
        if (SelfawareCommand.matches(message)) {
            SelfawareCommand.open();
            ci.cancel();
        }
    }
}
