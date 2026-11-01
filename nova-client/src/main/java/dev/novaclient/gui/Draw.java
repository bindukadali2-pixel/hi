package dev.novaclient.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Draw {
    public static final int ACCENT = 0xFF8B5CF6;
    public static final int ACCENT2 = 0xFF22D3EE;
    public static final int PANEL = 0x99101018;

    private Draw() {}

    public record Rect(int x, int y, int w, int h) {
        public boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int out = 0;
        for (int shift = 24; shift >= 0; shift -= 8) {
            int ca = (a >> shift) & 0xFF;
            int cb = (b >> shift) & 0xFF;
            out |= ((int) (ca + (cb - ca) * t) & 0xFF) << shift;
        }
        return out;
    }

    public static void vGradient(GuiGraphicsExtractor g, int x, int y, int w, int h, int top, int bottom) {
        final int step = 3;
        for (int i = 0; i < h; i += step) {
            int hh = Math.min(step, h - i);
            g.fill(x, y + i, x + w, y + i + hh, lerp(top, bottom, i / (float) h));
        }
    }

    public static void roundRect(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int color) {
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        g.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        roundRect(g, x, y, w, h, 3, PANEL);
        g.fill(x + 1, y + 3, x + 2, y + h - 3, ACCENT);
    }

    public static void centered(GuiGraphicsExtractor g, Font font, String s, int cx, int y, int color, boolean shadow) {
        g.text(font, s, cx - font.width(s) / 2, y, color, shadow);
    }
}
