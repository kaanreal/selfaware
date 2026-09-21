package dev.kaan.selfaware;

import net.fabricmc.api.ClientModInitializer;

public final class SelfawareClient implements ClientModInitializer {
    @Override
    // The 26.x profiles do not ship with Fabric API's client command module.
    // SelfawareChatScreenMixin remains the command fallback for these targets.
    public void onInitializeClient() {}
}
