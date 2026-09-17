package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfawareKeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Arrays;

@Mixin(Options.class)
abstract class SelfawareOptionsMixin {
    @Shadow @Final @Mutable public KeyMapping[] keyMappings;
    @Shadow @Final private File optionsFile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void selfaware$registerKeyBinding(CallbackInfo ci) {
        for (KeyMapping keyMapping : keyMappings) {
            if (keyMapping == SelfawareKeyBinding.OPEN_MENU) {
                SelfawareKeyBinding.load(optionsFile);
                return;
            }
        }
        keyMappings = Arrays.copyOf(keyMappings, keyMappings.length + 1);
        keyMappings[keyMappings.length - 1] = SelfawareKeyBinding.OPEN_MENU;
        SelfawareKeyBinding.load(optionsFile);
    }
}
