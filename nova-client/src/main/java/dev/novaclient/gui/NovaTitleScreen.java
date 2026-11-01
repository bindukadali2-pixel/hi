package dev.novaclient.gui;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** Lunar-style home screen: dark gradient, drifting stars, big logo, pill buttons. */
public class NovaTitleScreen extends Screen {
    private record Btn(String label, Draw.Rect rect, boolean primary, Runnable action) {}

    private final float[] hover = new float[8];

    public NovaTitleScreen() {
        super(Component.literal("Nova Client"));
    }

    @Override public boolean shouldCloseOnEsc() { return false; }

    private List<Btn> buttons() {
        List<Btn> l = new ArrayList<>();
        int w = 240, h = 26, gap = 8, half = (w - gap) / 2;
        int x = width / 2 - w / 2;
        int y = height / 2 - 14;
        l.add(new Btn("SINGLEPLAYER", new Draw.Rect(x, y, w, h), true,
                () -> minecraft.setScreen(new SelectWorldScreen(this))));
        y += h + gap;
        l.add(new Btn("MULTIPLAYER", new Draw.Rect(x, y, w, h), false,
                () -> minecraft.setScreen(new JoinMultiplayerScreen(this))));
        y += h + gap;
        l.add(new Btn("HUD EDITOR", new Draw.Rect(x, y, half, h), false,
                () -> minecraft.setScreen(new HudEditorScreen(this))));
        l.add(new Btn("COSMETICS", new Draw.Rect(x + half + gap, y, half, h), false,
                () -> minecraft.setScreen(new CosmeticsScreen(this))));
        y += h + gap;
        l.add(new Btn("OPTIONS", new Draw.Rect(x, y, half, h), false,
                () -> minecraft.setScreen(new OptionsScreen(this, minecraft.options))));
        l.add(new Btn("QUIT", new Draw.Rect(x + half + gap, y, half, h), false, () -> minecraft.stop()));
        return l;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        Draw.vGradient(g, 0, 0, width, height, 0xFF090914, 0xFF2B1250);

        long t = Util.getMillis();
        for (int i = 0; i < 70; i++) {
            int sx = (int) (((i * 7919L) % width + t / (40 + (i % 5) * 20)) % width);
            int sy = (int) ((i * 104729L) % height);
            int a = 60 + (i * 37) % 120;
            int size = 1 + (i % 11 == 0 ? 1 : 0);
            g.fill(sx, sy, sx + size, sy + size, (a << 24) | 0xFFFFFF);
        }

        // Logo
        g.pose().pushMatrix();
        g.pose().translate(width / 2f, height / 2f - 86f);
        g.pose().scale(5f, 5f);
        String logo = "NOVA";
        g.text(font, logo, -font.width(logo) / 2, 0, 0xFFFFFFFF, true);
        g.pose().popMatrix();
        Draw.centered(g, font, "C L I E N T", width / 2, height / 2 - 36, Draw.ACCENT2, true);

        // Buttons
        List<Btn> btns = buttons();
        for (int i = 0; i < btns.size(); i++) {
            Btn b = btns.get(i);
            boolean over = b.rect().contains(mouseX, mouseY);
            hover[i] += ((over ? 1f : 0f) - hover[i]) * 0.25f;
            Draw.Rect r = b.rect();
            int base = b.primary() ? Draw.ACCENT : 0x80000000;
            int color = Draw.lerp(base, b.primary() ? 0xFFA78BFA : Draw.ACCENT, hover[i]);
            Draw.roundRect(g, r.x(), r.y(), r.w(), r.h(), 5, color);
            if (!b.primary()) g.fill(r.x() + 6, r.y() + r.h() - 2, r.x() + 6 + (int) ((r.w() - 12) * hover[i]),
                    r.y() + r.h() - 1, Draw.ACCENT2);
            Draw.centered(g, font, b.label(), r.x() + r.w() / 2, r.y() + (r.h() - 8) / 2, 0xFFFFFFFF, true);
        }

        g.text(font, minecraft.getUser().getName(), 8, 8, 0xFFCCCCCC, true);
        String ver = "Nova Client 1.0.0  |  Minecraft 26.1.2";
        g.text(font, ver, width - font.width(ver) - 8, height - 14, 0x88FFFFFF, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (Btn b : buttons()) {
                if (b.rect().contains(event.x(), event.y())) {
                    b.action().run();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
