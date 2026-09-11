package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.Objects;

public final class ServerFormatting {
    private static final String SINGLEPLAYER_SERVER = "singleplayer";
    private static Component ownName;
    private static Appearance appearance;
    private static Object world;
    private static String server;
    private static int checkedTick = -1;
    private static Appearance cached;

    private ServerFormatting() {}

    public static Component name(Entity entity) {
        Component vanilla = entity.getDisplayName();
        Minecraft client = Minecraft.getInstance();
        if (entity != client.player || !SelfawareConfig.serverFormattingEnabled()) {
            return vanilla;
        }
        PlayerInfo info = client.getConnection() == null ? null
                : client.getConnection().getPlayerInfo(entity.getUUID());
        // A tab-list component belongs to this player, but can differ from the overhead name.
        Component tab = info == null ? null : info.getTabListDisplayName();
        ownName = selectName(vanilla, tab);
        appearance = findAppearance(client);
        return ownName;
    }

    static Component selectName(Component vanilla, Component tab) {
        return (tab == null || tab.getString().trim().isEmpty() ? vanilla : tab).copy();
    }

    private static Appearance findAppearance(Minecraft client) {
        if (client.level == null || client.player == null) {
            world = null;
            server = null;
            checkedTick = -1;
            cached = null;
            return null;
        }

        String currentServer = serverKey(client);
        if (world != client.level || !Objects.equals(server, currentServer)) {
            world = client.level;
            server = currentServer;
            checkedTick = -1;
            cached = loadCachedAppearance(currentServer);
        }
        if (checkedTick == client.player.tickCount) {
            return cached;
        }
        checkedTick = client.player.tickCount;
        if (!ServerTextDisplays.available()) {
            return cached;
        }

        double closest = 32 * 32;
        Appearance observed = null;
        for (Entity entity : client.level.entitiesForRendering()) {
            double distance = entity.distanceToSqr(client.player);
            if (distance >= closest || entity.isInvisible()) {
                continue;
            }
            DisplayStyle display = ServerTextDisplays.read(entity);
            if (display == null) {
                continue;
            }
            for (Player player : client.level.players()) {
                if (player.isInvisible() || player.isSpectator()
                        || !ServerNameMatch.containsName(display.text, player.getName().getString())) {
                    continue;
                }
                // Plugins either mount their display or teleport it above the player's head.
                if (entity.getRootVehicle() != player
                        && !ServerNameMatch.nearHead(entity.getX() - player.getX(),
                                entity.getY() - player.getY() - player.getBbHeight(),
                                entity.getZ() - player.getZ())) {
                    continue;
                }
                observed = display.appearance;
                closest = distance;
                break;
            }
        }
        if (observed != null && (cached == null || !cached.matches(observed))) {
            cached = observed;
            if (currentServer != null) {
                SelfawareConfig.saveCachedFormatting(currentServer, observed.shadow,
                        observed.defaultBackground, observed.background);
            }
        }
        return cached;
    }

    private static Appearance loadCachedAppearance(String currentServer) {
        if (currentServer == null || !currentServer.equals(SelfawareConfig.cachedFormattingServer())) {
            return null;
        }
        return new Appearance(SelfawareConfig.cachedFormattingShadow(),
                SelfawareConfig.cachedFormattingDefaultBackground(), SelfawareConfig.cachedFormattingBackground());
    }

    private static String serverKey(Minecraft client) {
        if (client.isLocalServer()) {
            return SINGLEPLAYER_SERVER;
        }
        ServerData current = client.getCurrentServer();
        if (current == null || current.ip == null || current.ip.trim().isEmpty()) {
            return null;
        }
        return current.ip.trim().toLowerCase(Locale.ROOT);
    }

    public static final class DisplayStyle {
        public final String text;
        public final Appearance appearance;

        public DisplayStyle(String text, Appearance appearance) {
            this.text = text;
            this.appearance = appearance;
        }
    }

    public static Appearance appearance(Component text) {
        return SelfawareConfig.serverFormattingEnabled() && text == ownName ? appearance : null;
    }

    public static final class Appearance {
        public final boolean shadow;
        public final boolean defaultBackground;
        public final int background;

        public Appearance(boolean shadow, boolean defaultBackground, int background) {
            this.shadow = shadow;
            this.defaultBackground = defaultBackground;
            this.background = background;
        }

        private boolean matches(Appearance other) {
            return shadow == other.shadow && defaultBackground == other.defaultBackground
                    && background == other.background;
        }
    }
}
