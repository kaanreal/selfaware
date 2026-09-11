package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfNameTag;
import dev.kaan.selfaware.SimpleVoiceChatIcon;
import dev.kaan.selfaware.SimpleVoiceChatPlugin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
    private void selfaware$renderOwnIcon(Entity entity, Component component, PoseStack poseStack,
            MultiBufferSource buffer, int light, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (!(entity instanceof Player) || component == null
                || !SelfNameTag.isLocalPlayer((Player) entity)
                || client.options.hideGui || !selfaware$shouldShowIcons()) {
            return;
        }
        Player player = (Player) entity;
        ResourceLocation texture = selfaware$texture(SimpleVoiceChatPlugin.localIcon(player));
        if (texture != null) {
            selfaware$renderPlayerIcon(player, component, texture, poseStack, buffer, light);
        }
    }

    private static ResourceLocation selfaware$texture(SimpleVoiceChatIcon icon) {
        if (icon == null) {
            return null;
        }
        String name;
        switch (icon) {
            case WHISPER_SPEAKER:
                name = "speaker_whisper";
                break;
            case SPEAKER:
                name = "speaker";
                break;
            case DISCONNECTED:
                name = "disconnected";
                break;
            case SPEAKER_OFF:
                name = "speaker_off";
                break;
            default:
                return null;
        }
        return new ResourceLocation("voicechat", "textures/icons/" + name + ".png");
    }

    @Invoker("renderPlayerIcon")
    abstract void selfaware$renderPlayerIcon(Player player, Component component, ResourceLocation texture,
            PoseStack poseStack, MultiBufferSource buffer, int light);

    @Invoker("shouldShowIcons")
    abstract boolean selfaware$shouldShowIcons();
}
