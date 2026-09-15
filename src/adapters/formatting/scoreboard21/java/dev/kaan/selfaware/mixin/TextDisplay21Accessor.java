package dev.kaan.selfaware.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface TextDisplay21Accessor {
    @Invoker("getLineWidth")
    int selfaware$getLineWidth();

    @Invoker("setLineWidth")
    void selfaware$setLineWidth(int width);

    @Invoker("getTextOpacity")
    byte selfaware$getTextOpacity();

    @Invoker("setTextOpacity")
    void selfaware$setTextOpacity(byte opacity);

    @Invoker("setText")
    void selfaware$setText(Component text);

    @Invoker("setBackgroundColor")
    void selfaware$setBackgroundColor(int color);

    @Invoker("setFlags")
    void selfaware$setFlags(byte flags);
}
