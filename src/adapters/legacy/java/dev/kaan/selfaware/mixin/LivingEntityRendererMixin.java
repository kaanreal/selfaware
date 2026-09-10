package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfNameTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void selfaware$showOwnName(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (SelfNameTag.isLocalPlayer(entity)) {
            cir.setReturnValue(SelfNameTag.shouldShow(entity,
                    Minecraft.getInstance().getEntityRenderDispatcher().distanceToSqr(entity)));
        }
    }
}
