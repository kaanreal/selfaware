package dev.kaan.selfaware;

import java.lang.reflect.Method;

final class SimpleVoiceChatAvailability {
    private SimpleVoiceChatAvailability() {}

    static boolean available() {
        try {
            Class<?> plugin = Class.forName("dev.kaan.selfaware.SimpleVoiceChatPlugin", false,
                    SimpleVoiceChatAvailability.class.getClassLoader());
            Method method = plugin.getMethod("available");
            return Boolean.TRUE.equals(method.invoke(null));
        } catch (Throwable ignored) {
            return false;
        }
    }
}
