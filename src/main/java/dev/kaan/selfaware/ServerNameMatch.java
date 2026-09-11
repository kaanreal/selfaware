package dev.kaan.selfaware;

public final class ServerNameMatch {
    private ServerNameMatch() {}

    public static boolean containsName(String text, String name) {
        if (name.isEmpty()) {
            return false;
        }
        int start = text.indexOf(name);
        while (start >= 0) {
            int end = start + name.length();
            if ((start == 0 || !nameCharacter(text.charAt(start - 1)))
                    && (end == text.length() || !nameCharacter(text.charAt(end)))) {
                return true;
            }
            start = text.indexOf(name, start + 1);
        }
        return false;
    }

    public static boolean nearHead(double x, double y, double z) {
        return x * x + z * z <= 1 && y >= -0.5 && y <= 3;
    }

    private static boolean nameCharacter(char value) {
        return value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z'
                || value >= '0' && value <= '9' || value == '_';
    }
}
