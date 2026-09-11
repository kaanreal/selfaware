package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Team;

public final class SelfNameTag {
    private SelfNameTag() {}

    public static boolean isLocalPlayer(LivingEntity entity) {
        return entity == Minecraft.getInstance().player;
    }

    public static boolean shouldShow(LivingEntity entity, double distanceSquared) {
        if (!SelfawareConfig.nametagEnabled()) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        Team team = entity.getTeam();
        boolean hiddenByTeam = team != null && (team.getNameTagVisibility() == Team.Visibility.NEVER
                || team.getNameTagVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM);
        return NameTagVisibility.visible(!client.options.getCameraType().isFirstPerson(),
                HudVisibility.isHidden(client), entity.isInvisible(), entity.isSpectator(),
                hiddenByTeam, entity.isDiscrete(), distanceSquared);
    }
}
