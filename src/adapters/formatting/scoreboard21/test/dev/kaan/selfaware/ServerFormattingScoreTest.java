package dev.kaan.selfaware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerFormattingScoreTest {
    @Test
    void readsCompactMoneyFromTheSidebarLine() {
        assertEquals(2_000_000, ServerFormattingScore.parseMoneyValue("$ 2M"));
        assertEquals(1_250_000_000, ServerFormattingScore.parseMoneyValue("$1.25B"));
    }
}
