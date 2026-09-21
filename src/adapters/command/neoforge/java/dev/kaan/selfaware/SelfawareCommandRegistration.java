package dev.kaan.selfaware;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class SelfawareCommandRegistration {
    private SelfawareCommandRegistration() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(SelfawareCommandRegistration::registerCommand);
    }

    private static void registerCommand(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("selfaware")
                .executes(context -> SelfawareCommand.execute());
        event.getDispatcher().register(command);
    }
}
