package dev.kaan.selfaware;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerFormattingScore {
    private static final Pattern MONEY = Pattern.compile("\\$\\s*([0-9]+(?:[.,][0-9]+)?)\\s*([KMBT]?)",
            Pattern.CASE_INSENSITIVE);

    private ServerFormattingScore() {}

    public static ReadOnlyScoreInfo fallback(Scoreboard scoreboard, Objective belowName) {
        Objective sidebar = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        if (sidebar == null || sidebar == belowName) {
            return null;
        }
        for (PlayerScoreEntry entry : scoreboard.listPlayerScores(sidebar)) {
            Component display = entry.display();
            String text = display == null ? entry.owner() : display.getString();
            if (text.indexOf('$') >= 0 || text.toLowerCase().contains("money")
                    || text.toLowerCase().contains("balance")) {
                Integer value = parseMoneyValue(text);
                if (value == null) {
                    continue;
                }
                NumberFormat format = entry.numberFormatOverride();
                return new FallbackScore(value, format);
            }
        }
        return null;
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
