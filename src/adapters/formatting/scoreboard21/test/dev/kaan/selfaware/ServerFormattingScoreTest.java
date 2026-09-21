package dev.kaan.selfaware;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerFormattingScoreTest {
    @Test
    void readsCompactMoneyValues() {
        assertEquals(2_000_000, ServerFormattingScore.parseMoneyValue("$ 2M"));
        assertEquals(1_250_000_000, ServerFormattingScore.parseMoneyValue("$1.25B"));
    }

    @Test
    void keepsTheCompactFooterValueForTheNametag() {
        Component money = ServerFormattingScore.moneyText("$ 2.3M • 35 ms");

        assertEquals("$ 2.3M", money.getString());
        assertEquals(ChatFormatting.GREEN.getColor(), money.getStyle().getColor().getValue());
        assertEquals(ChatFormatting.WHITE.getColor(),
                money.getSiblings().get(0).getStyle().getColor().getValue());
        assertNull(ServerFormattingScore.moneyText("Donut SMP"));
    }

    @Test
    void remembersMoneySentToTheTabFooter() {
        ServerFormattingScore.rememberTabFooter(Component.literal("$ 196M / 105 ms"));

        assertEquals("$ 196M", ServerFormattingScore.fromTab(null).getString());

        ServerFormattingScore.rememberTabFooter(null);
        assertNull(ServerFormattingScore.fromTab(null));
    }

    @Test
    void combinesTheNameAndMoneyIntoOneTextDisplayValue() {
        Component name = Component.literal("+").withStyle(ChatFormatting.AQUA)
                .append(Component.literal("Kaanreal").withStyle(ChatFormatting.WHITE));
        Component money = ServerFormattingScore.moneyText("$ 2.3M • 35 ms");

        Component combined = ServerFormattingScore.textDisplayValue(name, money);

        assertEquals("+Kaanreal\n$ 2.3M", combined.getString());
        assertEquals(ChatFormatting.AQUA.getColor(), combined.getStyle().getColor().getValue());
        assertEquals(ChatFormatting.WHITE.getColor(),
                combined.getSiblings().get(0).getStyle().getColor().getValue());
    }

    @Test
    void copiesTheMoneyColorsFromAPlayerTextDisplay() {
        Component sample = Component.literal("+sheepex_").withStyle(ChatFormatting.WHITE)
                .append(Component.literal("\n$ ").withStyle(ChatFormatting.DARK_GREEN))
                .append(Component.literal("10").withStyle(ChatFormatting.YELLOW));

        Component money = ServerFormattingScore.moneyText("$ 2.3M • 35 ms", sample);

        assertEquals(ChatFormatting.DARK_GREEN.getColor(), money.getStyle().getColor().getValue());
        assertEquals(ChatFormatting.YELLOW.getColor(),
                money.getSiblings().get(0).getStyle().getColor().getValue());
    }
}
