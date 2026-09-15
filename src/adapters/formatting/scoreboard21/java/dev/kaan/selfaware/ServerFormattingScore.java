package dev.kaan.selfaware;

import dev.kaan.selfaware.mixin.TabOverlayAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerFormattingScore {
    private static final Pattern MONEY = Pattern.compile("\\$\\s*([0-9]+(?:[.,][0-9]+)?)\\s*([KMBT]?)",
            Pattern.CASE_INSENSITIVE);

    private ServerFormattingScore() {}

    public static Component fromTab(Minecraft client) {
        if (client.gui == null) {
            return null;
        }
        Component footer = ((TabOverlayAccessor) client.gui.getTabList()).selfaware$getFooter();
        return footer == null ? null : moneyText(footer.getString(), ServerFormatting.sampledText());
    }

    static Component moneyText(String text) {
        return moneyText(text, null);
    }

    static Component moneyText(String text, Component sample) {
        Matcher matcher = MONEY.matcher(text);
        if (!matcher.find()) {
            return null;
        }

        Style dollarStyle = Style.EMPTY.withColor(ChatFormatting.GREEN);
        Style valueStyle = Style.EMPTY.withColor(ChatFormatting.WHITE);
        if (sample != null) {
            List<Component> parts = sample.toFlatList();
            boolean foundDollar = false;
            for (Component part : parts) {
                String partText = part.getString();
                if (!foundDollar && partText.indexOf('$') >= 0) {
                    dollarStyle = part.getStyle();
                    foundDollar = true;
                    continue;
                }
                if (foundDollar && partText.matches(".*\\d.*")) {
                    valueStyle = part.getStyle();
                    break;
                }
            }
        }

        String value = matcher.group(1).replace(',', '.') + matcher.group(2).toUpperCase(Locale.ROOT);
        return Component.literal("$").setStyle(dollarStyle)
                .append(Component.literal(" " + value).setStyle(valueStyle));
    }

    static Component textDisplayValue(Component name, Component money) {
        return name.copy().append(Component.literal("\n")).append(money.copy());
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
}
