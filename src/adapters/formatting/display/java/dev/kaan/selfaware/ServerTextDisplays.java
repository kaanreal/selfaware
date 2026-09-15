package dev.kaan.selfaware;

import dev.kaan.selfaware.mixin.TextDisplayAccessor;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;

public final class ServerTextDisplays {
    private ServerTextDisplays() {}

    public static boolean available() {
        return true;
    }

    public static ServerFormatting.DisplayStyle read(Entity entity) {
        if (!(entity instanceof Display.TextDisplay)) {
            return null;
        }
        TextDisplayAccessor display = (TextDisplayAccessor) entity;
        byte flags = display.selfaware$getFlags();
        return new ServerFormatting.DisplayStyle(display.selfaware$getText().copy(),
                new ServerFormatting.Appearance((flags & Display.TextDisplay.FLAG_SHADOW) != 0,
                        (flags & Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND) != 0,
                        display.selfaware$getBackgroundColor()));
    }
}
