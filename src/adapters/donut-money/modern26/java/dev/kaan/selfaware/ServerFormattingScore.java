package dev.kaan.selfaware;

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
    private static Component tabFooter;

    private ServerFormattingScore() {}

    public static Component fromTab(Minecraft client) {
        return tabFooter == null ? null : moneyText(tabFooter.getString(), ServerFormatting.sampledText());
    }

    public static void rememberTabFooter(Component footer) {
        tabFooter = footer == null ? null : footer.copy();
    }

    private static Component moneyText(String text, Component sample) {
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
}
