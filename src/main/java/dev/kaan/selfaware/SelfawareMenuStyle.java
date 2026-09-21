package dev.kaan.selfaware;

import net.minecraft.network.chat.Component;

public final class SelfawareMenuStyle {
    private SelfawareMenuStyle() {}

    public interface Canvas {
        void fill(int left, int top, int right, int bottom, int color);

        void text(Component text, int x, int y, int color);

        void centeredText(Component text, int x, int y, int color);
    }

    public static void render(Canvas canvas, int width, int height, int mouseX, int mouseY) {
        int left = SelfawareMenu.previewLeft(width);
        int right = SelfawareMenu.previewRight(width);
        int top = SelfawareMenu.previewTop();
        int bottom = SelfawareMenu.previewBottom(height);
        int optionsLeft = SelfawareMenu.optionsLeft(width);
        int optionsRight = optionsLeft + SelfawareMenu.optionsWidth(width);

        canvas.fill(left, top, right, bottom, 0x88000000);
        canvas.fill(optionsLeft, top, optionsRight, bottom, 0x76000000);
        canvas.fill(optionsLeft, top, optionsLeft + 2, bottom, 0xFF35B8FF);
        canvas.text(text("Preview"), left + 10, top + 9, 0xFFFFFFFF);
        canvas.text(text("Settings"), optionsLeft + 10, top + 9, 0xFFFFFFFF);

        drawToggle(canvas, width, height, 0, "Nametag", SelfawareConfig.nametagEnabled(), true, mouseX, mouseY);
        drawToggle(canvas, width, height, 1, "Voice chat icons", SelfawareConfig.svcIconsEnabled(),
                SelfawareMenu.svcIconsAvailable(), mouseX, mouseY);
        drawToggle(canvas, width, height, 2, "Server formatting", SelfawareConfig.serverFormattingEnabled(),
                true, mouseX, mouseY);
        drawToggle(canvas, width, height, 3, "DonutSMP rank", SelfawareConfig.donutRankEnabled(),
                SelfawareMenu.donutRankAvailable(), mouseX, mouseY);
        drawToggle(canvas, width, height, 4, "DonutSMP money", SelfawareConfig.donutMoneyEnabled(),
                SelfawareMenu.donutMoneyAvailable(), mouseX, mouseY);
        drawDone(canvas, width, height, mouseX, mouseY);
    }

    private static void drawToggle(Canvas canvas, int width, int height, int row, String label, boolean enabled,
                                   boolean available, int mouseX, int mouseY) {
        int x = SelfawareMenu.buttonLeft(width);
        int y = SelfawareMenu.rowY(height, row);
        int w = SelfawareMenu.buttonWidth(width);
        int h = SelfawareMenu.rowHeight(height);
        boolean hovered = available && inside(mouseX, mouseY, x, y, w, h);
        canvas.fill(x, y, x + w, y + h, hovered ? 0xEE253548 : 0xDD151D29);
        canvas.fill(x, y, x + 3, y + h, available ? 0xFF35B8FF : 0xFF566170);
        canvas.fill(x + 3, y, x + w, y + 1, hovered ? 0xFF45627E : 0xFF293747);

        int textY = y + (h - 8) / 2;
        canvas.text(text(label), x + 11, textY, available ? 0xFFFFFFFF : 0xFF89929D);

        String state = available ? (enabled ? "ON" : "OFF") : "N/A";
        int pillHeight = Math.min(18, h - 6);
        int pillWidth = h < 26 ? 36 : 46;
        int pillX = x + w - pillWidth - 6;
        int pillY = y + (h - pillHeight) / 2;
        int color = !available ? 0xFF303844 : enabled ? 0xFF198754 : 0xFF493C46;
        int textColor = !available ? 0xFF7F8995 : enabled ? 0xFFE7FFF2 : 0xFFFFEAF0;
        canvas.fill(pillX, pillY, pillX + pillWidth, pillY + pillHeight, color);
        canvas.centeredText(text(state), pillX + pillWidth / 2, pillY + (pillHeight - 8) / 2, textColor);
    }

    private static void drawDone(Canvas canvas, int width, int height, int mouseX, int mouseY) {
        int x = SelfawareMenu.doneLeft(width);
        int y = SelfawareMenu.doneY(height);
        int w = SelfawareMenu.doneWidth(width);
        int h = SelfawareMenu.rowHeight(height);
        boolean hovered = inside(mouseX, mouseY, x, y, w, h);
        canvas.fill(x, y, x + w, y + h, hovered ? 0xEE258BC0 : 0xDD17658F);
        canvas.fill(x, y, x + w, y + 2, hovered ? 0xFF8EDBFF : 0xFF35B8FF);
        canvas.centeredText(SelfawareMenu.doneLabel(), x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    private static boolean inside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static Component text(String value) {
        return Component.nullToEmpty(value);
    }
}
