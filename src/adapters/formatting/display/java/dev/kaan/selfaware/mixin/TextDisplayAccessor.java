package dev.kaan.selfaware.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplayAccessor {
    @Invoker("getText")
    Component selfaware$getText();

    @Invoker("getFlags")
    byte selfaware$getFlags();

    @Invoker("getBackgroundColor")
    int selfaware$getBackgroundColor();
}
