package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfawareCommand;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
abstract class SelfawareChatScreenMixin {
    @Shadow protected EditBox input;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void selfaware$openMenu(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((keyCode == 257 || keyCode == 335) && SelfawareCommand.matches(input.getValue())) {
            SelfawareCommand.open();
            cir.setReturnValue(true);
        }
    }
}
