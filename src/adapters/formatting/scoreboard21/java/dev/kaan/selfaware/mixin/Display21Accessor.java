package dev.kaan.selfaware.mixin;

import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface Display21Accessor {
    @Invoker("setBillboardConstraints")
    void selfaware$setBillboardConstraints(Display.BillboardConstraints constraints);
}
