package dev.kaan.selfaware;

import net.minecraft.network.chat.Component;

public final class DonutMoneySupport {
    private DonutMoneySupport() {}

    public static boolean available() {
        return false;
    }

    public static Component previewMoney() {
        return null;
    }
}
