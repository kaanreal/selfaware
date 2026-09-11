package dev.kaan.selfaware;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SimpleVoiceChatIconTest {
    @Test
    void followsSimpleVoiceChatPriority() {
        assertEquals(SimpleVoiceChatIcon.WHISPER_SPEAKER,
                SimpleVoiceChatIcon.select(true, true, true, true));
        assertEquals(SimpleVoiceChatIcon.SPEAKER,
                SimpleVoiceChatIcon.select(false, true, true, true));
        assertEquals(SimpleVoiceChatIcon.DISCONNECTED,
                SimpleVoiceChatIcon.select(false, false, true, true));
        assertEquals(SimpleVoiceChatIcon.SPEAKER_OFF,
                SimpleVoiceChatIcon.select(false, false, false, true));
    }

    @Test
    void hasNoIconWhenVoiceChatHasNoLocalStatus() {
        assertNull(SimpleVoiceChatIcon.select(false, false, false, false));
    }

    @Test
    void staysDisabledWithoutSimpleVoiceChat() {
        assertNull(SimpleVoiceChatPlugin.localIcon());
    }
}
