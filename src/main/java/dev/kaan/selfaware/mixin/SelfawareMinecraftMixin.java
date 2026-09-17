package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfawareKeyBinding;
import dev.kaan.selfaware.SelfawareMenu;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class SelfawareMinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void selfaware$openMenu(CallbackInfo ci) {
        if (SelfawareKeyBinding.OPEN_MENU.consumeClick()) {
            SelfawareMenu.open();
        }
    }
}
