package dev.kaan.selfaware.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kaan.selfaware.SimpleVoiceChatIcon;
import dev.kaan.selfaware.SimpleVoiceChatPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "de.maxhenkel.voicechat.voice.client.RenderEvents")
abstract class SimpleVoiceChatRenderEventsMixin {
    @Inject(method = "onRenderName", at = @At("RETURN"))
    private void selfaware$renderOwnIcon(EntityRenderState renderState, Component component,
            PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {
        if (!(renderState instanceof PlayerRenderState playerState) || component == null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null
                || client.level.getEntity(playerState.id) != client.player
                || client.options.hideGui || !selfaware$shouldShowIcons()) {
            return;
        }
        ResourceLocation texture = selfaware$texture(SimpleVoiceChatPlugin.localIcon(client.player));
        if (texture != null) {
            selfaware$renderPlayerIcon(client.player.getUUID(), renderState, component, texture,
                    poseStack, buffer, light);
        }
    }

    private static ResourceLocation selfaware$texture(SimpleVoiceChatIcon icon) {
        if (icon == null) {
            return null;
        }
        String name = switch (icon) {
            case WHISPER_SPEAKER -> "speaker_whisper";
            case SPEAKER -> "speaker";
            case DISCONNECTED -> "disconnected";
            case SPEAKER_OFF -> "speaker_off";
        };
        return ResourceLocation.fromNamespaceAndPath("voicechat", "icons/" + name);
    }

    @Invoker("renderPlayerIcon")
    abstract void selfaware$renderPlayerIcon(java.util.UUID entityId, EntityRenderState renderState,
            Component component, ResourceLocation texture, PoseStack poseStack,
            MultiBufferSource buffer, int light);

    @Invoker("shouldShowIcons")
    abstract boolean selfaware$shouldShowIcons();
}
