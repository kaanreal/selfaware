package dev.kaan.selfaware;

public final class SelfawareCommand {
    private SelfawareCommand() {}

    public static boolean matches(String message) {
        return message != null && "/selfaware".equalsIgnoreCase(message.trim());
    }

    public static void open() {
        SelfawareMenu.open();
    }

    public static int execute() {
        open();
        return 1;
    }
}
