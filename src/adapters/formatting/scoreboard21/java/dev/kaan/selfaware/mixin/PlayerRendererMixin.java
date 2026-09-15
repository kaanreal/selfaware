package dev.kaan.selfaware.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.kaan.selfaware.DonutTextDisplayRenderer;
import dev.kaan.selfaware.DonutMoneySupport;
import dev.kaan.selfaware.SelfawareConfig;
import dev.kaan.selfaware.ServerFormatting;
import dev.kaan.selfaware.ServerFormattingScore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    protected PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model,
            float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
                    ordinal = 0))
    private void selfaware$skipDonutBelowNameScore(LivingEntityRenderer<?, ?> renderer, Entity entity, Component score,
            PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {
        if (!selfaware$donutMoneyVisible(entity)) {
            selfaware$renderVanillaNameTag((AbstractClientPlayer) entity, score, poseStack, buffers, light, partialTick);
        }
    }

    @Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
                    ordinal = 1))
    private void selfaware$renderDonutMoney(LivingEntityRenderer<?, ?> renderer, Entity entity, Component name,
            PoseStack poseStack, MultiBufferSource buffers, int light, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        if (selfaware$donutMoneyFor(entity)) {
            Component money = ServerFormattingScore.fromTab(client);
            if (money != null) {
                if (DonutMoneySupport.needsNameOffset((AbstractClientPlayer) entity)) {
                    poseStack.translate(0.0F, 0.25875F, 0.0F);
                }
                DonutTextDisplayRenderer.render((AbstractClientPlayer) entity, name, money, poseStack, buffers, light,
                        partialTick);
                return;
            }
        }
        selfaware$renderVanillaNameTag((AbstractClientPlayer) entity, name, poseStack, buffers, light, partialTick);
    }

    @Unique
    private boolean selfaware$donutMoneyFor(Entity entity) {
        Minecraft client = Minecraft.getInstance();
        return entity == client.player && SelfawareConfig.donutMoneyEnabled() && ServerFormatting.isDonutServer();
    }

    @Unique
    private boolean selfaware$donutMoneyVisible(Entity entity) {
        return selfaware$donutMoneyFor(entity) && ServerFormattingScore.fromTab(Minecraft.getInstance()) != null;
    }

    @Unique
    private void selfaware$renderVanillaNameTag(AbstractClientPlayer player, Component name, PoseStack poseStack,
            MultiBufferSource buffers, int light, float partialTick) {
        super.renderNameTag(player, name, poseStack, buffers, light, partialTick);
    }
}
