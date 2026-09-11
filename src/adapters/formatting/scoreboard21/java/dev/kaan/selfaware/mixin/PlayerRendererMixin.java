package dev.kaan.selfaware.mixin;

import dev.kaan.selfaware.SelfawareConfig;
import dev.kaan.selfaware.ServerFormattingScore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
abstract class PlayerRendererMixin {
    @Redirect(method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/Scoreboard;getPlayerScoreInfo(Lnet/minecraft/world/scores/ScoreHolder;Lnet/minecraft/world/scores/Objective;)Lnet/minecraft/world/scores/ReadOnlyScoreInfo;"))
    private ReadOnlyScoreInfo selfaware$ownBelowNameScore(Scoreboard scoreboard, ScoreHolder holder,
            Objective objective) {
        ReadOnlyScoreInfo original = scoreboard.getPlayerScoreInfo(holder, objective);
        if (!SelfawareConfig.serverFormattingEnabled()
                || !(holder instanceof Player player)
                || player != Minecraft.getInstance().player
                || original != null) {
            return original;
        }
        return ServerFormattingScore.fallback(scoreboard, objective);
    }
}
