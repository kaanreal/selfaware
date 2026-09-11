package dev.kaan.selfaware;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.ReadOnlyScoreInfo;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerFormattingScore {
    private static final Pattern MONEY = Pattern.compile("\\$\\s*([0-9]+(?:[.,][0-9]+)?)\\s*([KMBT]?)",
            Pattern.CASE_INSENSITIVE);

    private ServerFormattingScore() {}

    public static ReadOnlyScoreInfo fromTab(Minecraft client) {
        if (client.player == null || client.getConnection() == null) {
            return null;
        }
        PlayerInfo info = client.getConnection().getPlayerInfo(client.player.getUUID());
        Component tab = info == null ? null : info.getTabListDisplayName();
        if (tab == null || tab.getString().trim().isEmpty()) {
            return null;
        }
        Integer value = parseMoneyValue(tab.getString());
        return value == null ? null : new FallbackScore(value, null);
    }

    static Integer parseMoneyValue(String text) {
        Matcher matcher = MONEY.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        double value;
        try {
            value = Double.parseDouble(matcher.group(1).replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return null;
        }
        value *= switch (matcher.group(2).toUpperCase(Locale.ROOT)) {
            case "K" -> 1_000;
            case "M" -> 1_000_000;
            case "B" -> 1_000_000_000;
            case "T" -> 1_000_000_000_000d;
            default -> 1;
        };
        return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE ? (int) value : null;
    }

    private record FallbackScore(int value, NumberFormat numberFormat) implements ReadOnlyScoreInfo {
        @Override
        public boolean isLocked() {
            return false;
        }
    }
}
