package dev.kaan.selfaware.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kaan.selfaware.SimpleVoiceChatIcon;
import dev.kaan.selfaware.SimpleVoiceChatPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    private void selfaware$renderOwnIcon(EntityRenderState renderState,
            CameraRenderState cameraRenderState, PoseStack poseStack,
            SubmitNodeCollector collector, CallbackInfo ci) {
        if (!(renderState instanceof AvatarRenderState avatarState)
                || renderState.nameTag == null || renderState.nameTagAttachment == null) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null
                || client.level.getEntity(avatarState.id) != client.player
                || client.options.hideGui || !selfaware$shouldShowIcons()) {
            return;
        }
        Identifier texture = selfaware$texture(SimpleVoiceChatPlugin.localIcon(client.player));
        if (texture != null) {
            poseStack.pushPose();
            poseStack.translate(avatarState.nameTagAttachment);
            poseStack.translate(0.0D, 0.5D, 0.0D);
            poseStack.mulPose(cameraRenderState.orientation);
            poseStack.scale(0.025F, -0.025F, 0.025F);
            try {
                selfaware$renderPlayerIcon(client.player.getUUID(), avatarState.isDiscrete,
                        renderState.nameTag, texture, poseStack, collector, renderState.lightCoords);
            } finally {
                poseStack.popPose();
            }
        }
    }

    private static Identifier selfaware$texture(SimpleVoiceChatIcon icon) {
        if (icon == null) {
            return null;
        }
        String name = switch (icon) {
            case WHISPER_SPEAKER -> "speaker_whisper";
            case SPEAKER -> "speaker";
            case DISCONNECTED -> "disconnected";
            case SPEAKER_OFF -> "speaker_off";
        };
        return Identifier.fromNamespaceAndPath("voicechat", "icons/" + name);
    }

    @Invoker("renderPlayerIcon")
    abstract void selfaware$renderPlayerIcon(java.util.UUID entityId, boolean discrete,
            Component component, Identifier texture, PoseStack poseStack,
            SubmitNodeCollector collector, int light);

    @Invoker("shouldShowIcons")
    abstract boolean selfaware$shouldShowIcons();
}
