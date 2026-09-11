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
    private static boolean serverFormattingEnabled = false;
    private static boolean donutMoneyEnabled = false;
    private static String cachedFormattingServer;
    private static boolean cachedFormattingShadow;
    private static boolean cachedFormattingDefaultBackground;
    private static int cachedFormattingBackground;

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

    public static synchronized boolean serverFormattingEnabled() {
        return serverFormattingEnabled;
    }

    public static synchronized void setServerFormattingEnabled(boolean enabled) {
        serverFormattingEnabled = enabled;
        save();
    }

    public static synchronized boolean donutMoneyEnabled() {
        return donutMoneyEnabled;
    }

    public static synchronized void setDonutMoneyEnabled(boolean enabled) {
        donutMoneyEnabled = enabled;
        save();
    }

    static synchronized String cachedFormattingServer() {
        return cachedFormattingServer;
    }

    static synchronized boolean cachedFormattingShadow() {
        return cachedFormattingShadow;
    }

    static synchronized boolean cachedFormattingDefaultBackground() {
        return cachedFormattingDefaultBackground;
    }

    static synchronized int cachedFormattingBackground() {
        return cachedFormattingBackground;
    }

    static synchronized void saveCachedFormatting(String server, boolean shadow,
            boolean defaultBackground, int background) {
        cachedFormattingServer = server;
        cachedFormattingShadow = shadow;
        cachedFormattingDefaultBackground = defaultBackground;
        cachedFormattingBackground = background;
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
            serverFormattingEnabled = parseBoolean(properties.getProperty("server_formatting_enabled"), false);
            donutMoneyEnabled = parseBoolean(properties.getProperty("donut_money_enabled"), false);
            svcIconsEnabled = parseBoolean(properties.getProperty("svc_icons_enabled"), true);
            cachedFormattingServer = properties.getProperty("server_formatting_cached_server");
            if (cachedFormattingServer != null && cachedFormattingServer.trim().isEmpty()) {
                cachedFormattingServer = null;
            }
            cachedFormattingShadow = parseBoolean(properties.getProperty("server_formatting_cached_shadow"), false);
            cachedFormattingDefaultBackground = parseBoolean(
                    properties.getProperty("server_formatting_cached_default_background"), false);
            try {
                cachedFormattingBackground = Integer.parseInt(
                        properties.getProperty("server_formatting_cached_background", "0"));
            } catch (NumberFormatException ignored) {
                cachedFormattingBackground = 0;
            }
        } catch (Exception ignored) {
            // Missing or unreadable settings keep the defaults.
        }
    }

    private static void save() {
        Properties properties = new Properties();
        properties.setProperty("nametag_enabled", Boolean.toString(nametagEnabled));
        properties.setProperty("server_formatting_enabled", Boolean.toString(serverFormattingEnabled));
        properties.setProperty("donut_money_enabled", Boolean.toString(donutMoneyEnabled));
        properties.setProperty("svc_icons_enabled", Boolean.toString(svcIconsEnabled));
        if (cachedFormattingServer != null) {
            properties.setProperty("server_formatting_cached_server", cachedFormattingServer);
            properties.setProperty("server_formatting_cached_shadow", Boolean.toString(cachedFormattingShadow));
            properties.setProperty("server_formatting_cached_default_background",
                    Boolean.toString(cachedFormattingDefaultBackground));
            properties.setProperty("server_formatting_cached_background",
                    Integer.toString(cachedFormattingBackground));
        }
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
