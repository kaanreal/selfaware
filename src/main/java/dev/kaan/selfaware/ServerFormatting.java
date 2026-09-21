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
    private static Component sampledText;
    private static Entity sampledDisplay;
    private static Entity ownDisplayEntity;
    private static boolean ownDisplay;
    private static Object world;
    private static String server;
    private static int checkedTick = -1;
    private static Appearance cached;

    private ServerFormatting() {}

    public static Component name(Entity entity) {
        Component vanilla = entity.getDisplayName();
        Minecraft client = Minecraft.getInstance();
        if (entity != client.player) {
            return vanilla;
        }
        boolean donutServer = isDonutServer(client);
        if (!donutServer && !SelfawareConfig.serverFormattingEnabled()) {
            return vanilla;
        }
        appearance = findAppearance(client);
        PlayerInfo info = client.getConnection() == null ? null
                : client.getConnection().getPlayerInfo(entity.getUUID());
        // A tab-list component belongs to this player, but can differ from the overhead name.
        Component tab = info == null ? null : info.getTabListDisplayName();
        ownName = donutServer
                ? selectDonutName(entity.getName(), tab, SelfawareConfig.donutRankEnabled())
                : selectName(vanilla, tab);
        return ownName;
    }

    public static boolean isDonutServer() {
        return isDonutServer(Minecraft.getInstance());
    }

    private static boolean isDonutServer(Minecraft client) {
        return isDonutAddress(serverKey(client));
    }

    static boolean isDonutAddress(String address) {
        if (address == null || address.equals(SINGLEPLAYER_SERVER)) {
            return false;
        }
        String host = address.trim().toLowerCase(Locale.ROOT);
        int port = host.lastIndexOf(':');
        if (port > 0 && host.indexOf(':') == port) {
            host = host.substring(0, port);
        }
        return host.equals("donutsmp.net") || host.endsWith(".donutsmp.net");
    }

    static Component selectName(Component vanilla, Component tab) {
        return (tab == null || tab.getString().trim().isEmpty() ? vanilla : tab).copy();
    }

    static Component selectDonutName(Component plain, Component tab, boolean rankEnabled) {
        return rankEnabled ? selectName(plain, tab) : plain.copy();
    }

    private static Appearance findAppearance(Minecraft client) {
        if (client.level == null || client.player == null) {
            world = null;
            server = null;
            checkedTick = -1;
            cached = null;
            sampledText = null;
            sampledDisplay = null;
            ownDisplayEntity = null;
            ownDisplay = false;
            return null;
        }

        String currentServer = serverKey(client);
        if (world != client.level || !Objects.equals(server, currentServer)) {
            world = client.level;
            server = currentServer;
            checkedTick = -1;
            cached = loadCachedAppearance(currentServer);
            sampledText = null;
            sampledDisplay = null;
            ownDisplayEntity = null;
            ownDisplay = false;
        }
        if (checkedTick == client.player.tickCount) {
            return cached;
        }
        checkedTick = client.player.tickCount;
        if (!ServerTextDisplays.available()) {
            ownDisplay = false;
            ownDisplayEntity = null;
            return cached;
        }

        ownDisplay = false;
        ownDisplayEntity = null;
        double closest = 32 * 32;
        Appearance observed = null;
        for (Entity entity : client.level.entitiesForRendering()) {
            double distance = entity.distanceToSqr(client.player);
            if (distance >= 32 * 32 || entity.isInvisible()) {
                continue;
            }
            DisplayStyle display = ServerTextDisplays.read(entity);
            if (display == null) {
                continue;
            }
            for (Player player : client.level.players()) {
                if (player.isInvisible() || player.isSpectator()
                        || !ServerNameMatch.containsName(display.text.getString(), player.getName().getString())) {
                    continue;
                }
                // Plugins either mount their display or teleport it above the player's head.
                if (entity.getRootVehicle() != player
                        && !ServerNameMatch.nearHead(entity.getX() - player.getX(),
                                entity.getY() - player.getY() - player.getBbHeight(),
                                entity.getZ() - player.getZ())) {
                    continue;
                }
                if (player == client.player) {
                    ownDisplay = true;
                    ownDisplayEntity = entity;
                    observed = display.appearance;
                    sampledText = display.text.copy();
                    sampledDisplay = entity;
                    closest = -1;
                    break;
                }
                if (distance >= closest) {
                    continue;
                }
                observed = display.appearance;
                sampledText = display.text.copy();
                sampledDisplay = entity;
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
        public final Component text;
        public final Appearance appearance;

        public DisplayStyle(Component text, Appearance appearance) {
            this.text = text;
            this.appearance = appearance;
        }
    }

    public static Component sampledText() {
        return sampledText;
    }

    public static Entity sampledDisplay() {
        return sampledDisplay;
    }

    public static boolean hasOwnTextDisplay() {
        findAppearance(Minecraft.getInstance());
        return ownDisplay;
    }

    public static TextDisplayOverride textDisplayOverride(Entity entity) {
        Minecraft client = Minecraft.getInstance();
        appearance = findAppearance(client);
        if (client.player == null || entity != ownDisplayEntity) {
            return null;
        }
        if (!SelfawareConfig.nametagEnabled()) {
            return TextDisplayOverride.hidden();
        }

        boolean serverAppearance = SelfawareConfig.serverFormattingEnabled();
        if (!isDonutServer(client)) {
            return serverAppearance ? TextDisplayOverride.unchanged()
                    : TextDisplayOverride.text(client.player.getName().copy(), false);
        }

        PlayerInfo info = client.getConnection() == null ? null
                : client.getConnection().getPlayerInfo(client.player.getUUID());
        Component tab = info == null ? null : info.getTabListDisplayName();
        Component text = selectDonutName(client.player.getName(), tab, SelfawareConfig.donutRankEnabled());
        if (SelfawareConfig.donutMoneyEnabled()) {
            Component money = DonutMoneySupport.previewMoney();
            if (money != null) {
                text = nameWithMoney(text, money);
            }
        }
        return TextDisplayOverride.text(text, serverAppearance);
    }

    public static Component nameWithMoney(Component name, Component money) {
        return name.copy().append(Component.nullToEmpty("\n")).append(money.copy());
    }

    public static Component donutMoney(Component name) {
        if (name != ownName || ownDisplay || !SelfawareConfig.donutMoneyEnabled() || !isDonutServer()) {
            return null;
        }
        return DonutMoneySupport.previewMoney();
    }

    public static Appearance appearance(Component text) {
        return SelfawareConfig.serverFormattingEnabled() && text == ownName ? appearance : null;
    }

    public static final class TextDisplayOverride {
        public final Component text;
        public final boolean hidden;
        public final boolean serverAppearance;

        private TextDisplayOverride(Component text, boolean hidden, boolean serverAppearance) {
            this.text = text;
            this.hidden = hidden;
            this.serverAppearance = serverAppearance;
        }

        private static TextDisplayOverride hidden() {
            return new TextDisplayOverride(null, true, false);
        }

        private static TextDisplayOverride unchanged() {
            return new TextDisplayOverride(null, false, true);
        }

        private static TextDisplayOverride text(Component text, boolean serverAppearance) {
            return new TextDisplayOverride(text, false, serverAppearance);
        }
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
