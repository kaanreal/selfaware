package dev.kaan.selfaware;

final class NameTagVisibility {
    private NameTagVisibility() {}

    static boolean visible(boolean thirdPerson, boolean hiddenHud, boolean invisible,
            boolean spectator, boolean hiddenByTeam, boolean sneaking, double distanceSquared) {
        double range = sneaking ? 32.0 : 64.0;
        return thirdPerson && !hiddenHud && !invisible && !spectator && !hiddenByTeam
                && distanceSquared < range * range;
    }
}
