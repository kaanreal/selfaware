package dev.kaan.selfaware;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

final class SelfawareKeyBindingImpl {
    private SelfawareKeyBindingImpl() {}

    static KeyMapping create(String name) {
        return new KeyMapping(name, GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");
    }
}
