package dev.kaan.selfaware;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class SelfawareConfig {
    private static final Path FILE = Paths.get("config", "selfaware.properties");
    private static boolean nametagEnabled = true;
    private static boolean svcIconsEnabled = true;

    static {
        load();
    }

    private SelfawareConfig() {}

    public static synchronized boolean nametagEnabled() {
        return nametagEnabled;
    }

    public static synchronized boolean svcIconsEnabled() {
        return svcIconsEnabled;
    }

    public static synchronized void setNametagEnabled(boolean enabled) {
        nametagEnabled = enabled;
        save();
    }

    public static synchronized void setSvcIconsEnabled(boolean enabled) {
        svcIconsEnabled = enabled;
        save();
    }

    static boolean parseBoolean(String value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        return fallback;
    }

    private static void load() {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(FILE)) {
            properties.load(input);
            nametagEnabled = parseBoolean(properties.getProperty("nametag_enabled"), true);
            svcIconsEnabled = parseBoolean(properties.getProperty("svc_icons_enabled"), true);
        } catch (Exception ignored) {
            // Missing or unreadable settings keep the feature defaults enabled.
        }
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("nametag_enabled", Boolean.toString(nametagEnabled));
        properties.setProperty("svc_icons_enabled", Boolean.toString(svcIconsEnabled));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream output = Files.newOutputStream(FILE)) {
                properties.store(output, "Selfaware settings");
            }
        } catch (Exception ignored) {
            // A read-only instance keeps the current values for this session.
        }
    }
}
