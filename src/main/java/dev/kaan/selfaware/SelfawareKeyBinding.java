package dev.kaan.selfaware;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public final class SelfawareKeyBinding {
    public static final String NAME = "key.selfaware.open_menu";
    public static final KeyMapping OPEN_MENU = SelfawareKeyBindingImpl.create(NAME);

    private SelfawareKeyBinding() {}

    public static void load(File optionsFile) {
        if (optionsFile == null || !optionsFile.isFile()) {
            return;
        }
        String prefix = "key_" + NAME + ":";
        try {
            List<String> lines = Files.readAllLines(optionsFile.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith(prefix)) {
                    OPEN_MENU.setKey(InputConstants.getKey(line.substring(prefix.length())));
                    return;
                }
            }
        } catch (Exception ignored) {
            // A missing or unreadable options file keeps the unbound default.
        }
    }
}
