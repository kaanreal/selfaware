package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;

final class HudVisibility {
    private HudVisibility() {}

    static boolean isHidden(Minecraft client) {
        return client.options.hideGui;
    }
}
