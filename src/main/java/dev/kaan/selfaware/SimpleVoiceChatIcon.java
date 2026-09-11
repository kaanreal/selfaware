package dev.kaan.selfaware;

public enum SimpleVoiceChatIcon {
    WHISPER_SPEAKER,
    SPEAKER,
    DISCONNECTED,
    SPEAKER_OFF;

    public static SimpleVoiceChatIcon select(boolean whispering, boolean talking,
            boolean disconnected, boolean disabled) {
        if (whispering) {
            return WHISPER_SPEAKER;
        }
        if (talking) {
            return SPEAKER;
        }
        if (disconnected) {
            return DISCONNECTED;
        }
        if (disabled) {
            return SPEAKER_OFF;
        }
        return null;
    }
}
