package dev.kaan.selfaware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerNameMatchTest {
    @Test
    void acceptsRankPrefixesAndExtraLines() {
        assertTrue(ServerNameMatch.containsName("+sheepex_\n$ 1B", "sheepex_"));
        assertTrue(ServerNameMatch.containsName("[VIP] kaanreal", "kaanreal"));
    }

    @Test
    void rejectsPartialNamesAndEmptyNames() {
        assertFalse(ServerNameMatch.containsName("notkaanreal", "kaanreal"));
        assertFalse(ServerNameMatch.containsName("kaanreal_", "kaanreal"));
        assertFalse(ServerNameMatch.containsName("kaanreal2", "kaanreal"));
        assertFalse(ServerNameMatch.containsName("kaanreal", ""));
    }

    @Test
    void onlyAcceptsDisplaysNearThePlayersHead() {
        assertTrue(ServerNameMatch.nearHead(0.1, 0.4, 0.1));
        assertFalse(ServerNameMatch.nearHead(2, 0.4, 0));
        assertFalse(ServerNameMatch.nearHead(0, -2, 0));
        assertFalse(ServerNameMatch.nearHead(0, 5, 0));
    }
}
