package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

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
        // Some servers send a separate text-display nameplate for the local player.
        // Leave that native plate alone instead of drawing a second vanilla label.
        if (ServerFormatting.hasOwnTextDisplay()) {
            return false;
        }
        // EntityRenderer skips team visibility checks for the camera entity. Keep that
        // vanilla behavior so servers that hide names from teammates do not hide our
        // own third-person tag as well.
        boolean hiddenByTeam = false;
        return NameTagVisibility.visible(!client.options.getCameraType().isFirstPerson(),
                HudVisibility.isHidden(client), entity.isInvisible(), entity.isSpectator(),
                hiddenByTeam, entity.isDiscrete(), distanceSquared);
    }
}
