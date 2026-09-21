package dev.kaan.selfaware;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public final class SelfawareClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandManager.DISPATCHER.register(
                ClientCommandManager.literal("selfaware")
                        .executes(context -> SelfawareCommand.execute())
        );
    }
}
