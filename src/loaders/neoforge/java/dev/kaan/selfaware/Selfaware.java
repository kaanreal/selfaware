package dev.kaan.selfaware;

import net.neoforged.fml.common.Mod;

@Mod("selfaware")
public final class Selfaware {
    public Selfaware() {
        SelfawareCommandRegistration.register();
    }
}
