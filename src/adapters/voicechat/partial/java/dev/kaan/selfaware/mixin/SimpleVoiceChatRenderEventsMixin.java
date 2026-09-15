package dev.kaan.selfaware.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kaan.selfaware.DonutMoneySupport;
import dev.kaan.selfaware.SelfNameTag;
import dev.kaan.selfaware.SimpleVoiceChatIcon;
import dev.kaan.selfaware.SimpleVoiceChatPlugin;
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
            MultiBufferSource buffer, int light, float partialTicks, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (!(entity instanceof Player player) || component == null
                || !SelfNameTag.isLocalPlayer(player)
                || client.options.hideGui || !selfaware$shouldShowIcons()) {
            return;
        }
        ResourceLocation texture = selfaware$texture(SimpleVoiceChatPlugin.localIcon(player));
        if (texture != null) {
            if (DonutMoneySupport.needsNameOffset((net.minecraft.client.player.AbstractClientPlayer) player)) {
                poseStack.pushPose();
                poseStack.translate(0.0F, 0.25875F, 0.0F);
                selfaware$renderPlayerIcon(player, component, texture, poseStack, buffer, light, partialTicks);
                poseStack.popPose();
                return;
            }
            selfaware$renderPlayerIcon(player, component, texture, poseStack, buffer, light, partialTicks);
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
    abstract void selfaware$renderPlayerIcon(Player player, Component component, ResourceLocation texture,
            PoseStack poseStack, MultiBufferSource buffer, int light, float partialTicks);

    @Invoker("shouldShowIcons")
    abstract boolean selfaware$shouldShowIcons();
}
