package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.ServerFormatting;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getDisplayName()Lnet/minecraft/network/chat/Component;"))
    private Component selfaware$serverName(Entity entity) {
        return ServerFormatting.name(entity);
    }
}
