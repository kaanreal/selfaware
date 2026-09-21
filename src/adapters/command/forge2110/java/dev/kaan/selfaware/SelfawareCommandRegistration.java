package dev.kaan.selfaware;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;

public final class SelfawareCommandRegistration {
    private SelfawareCommandRegistration() {}

    public static void register() {
        RegisterClientCommandsEvent.BUS.addListener(SelfawareCommandRegistration::registerCommand);
    }

    private static void registerCommand(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("selfaware")
                .executes(context -> SelfawareCommand.execute());
        event.getDispatcher().register(command);
    }
}
