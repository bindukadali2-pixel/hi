package dev.novaclient.gui;

import dev.novaclient.config.NovaConfig;
import dev.novaclient.hud.HudManager;
import dev.novaclient.hud.HudModule;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Drag HUD modules around, scroll over one to resize it, toggle modules in the side panel. */
public class HudEditorScreen extends Screen {
    private static final int PANEL_W = 150;
    private static final int SNAP = 5;

    private final Screen parent;
    private HudModule dragging;
    private int dragOffX, dragOffY;
    private boolean snapX, snapY;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("HUD Editor"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        NovaConfig.save();
        minecraft.setScreen(parent);
    }

    private int panelX() { return width - PANEL_W - 10; }

    private Draw.Rect rowRect(int i) { return new Draw.Rect(panelX() + 6, 34 + i * 20, PANEL_W - 12, 16); }

    private Draw.Rect buttonRect(int i) {
        int n = HudManager.all().size();
        return new Draw.Rect(panelX() + 6, 34 + n * 20 + 8 + i * 22, PANEL_W - 12, 18);
    }

    private Draw.Rect panelRect() {
        int n = HudManager.all().size();
        return new Draw.Rect(panelX(), 10, PANEL_W, 34 + n * 20 + 8 + 3 * 22 + 4);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0x99000000);
        if (snapX) g.fill(width / 2, 0, width / 2 + 1, height, 0x88FFFFFF);
        if (snapY) g.fill(0, height / 2, width, height / 2 + 1, 0x88FFFFFF);

        for (HudModule m : HudManager.all()) {
            m.draw(g, minecraft, true);
            if (!m.isEnabled()) continue;
            int x = m.x(minecraft, true), y = m.y(minecraft, true);
            int w = m.scaledW(minecraft, true), h = m.scaledH(minecraft, true);
            boolean hot = m == dragging || new Draw.Rect(x, y, w, h).contains(mouseX, mouseY);
            g.outline(x - 1, y - 1, w + 2, h + 2, hot ? Draw.ACCENT2 : 0x66FFFFFF);
            if (hot) g.text(font, m.name() + "  x" + String.format("%.1f", m.scale()), x, y - 11, 0xFFFFFFFF, true);
        }

        // side panel
        Draw.Rect p = panelRect();
        Draw.roundRect(g, p.x(), p.y(), p.w(), p.h(), 6, 0xCC0E0E1A);
        g.text(font, "HUD MODULES", p.x() + 8, 18, Draw.ACCENT2, true);
        List<HudModule> mods = HudManager.all();
        for (int i = 0; i < mods.size(); i++) {
            HudModule m = mods.get(i);
            Draw.Rect r = rowRect(i);
            boolean over = r.contains(mouseX, mouseY);
            Draw.roundRect(g, r.x(), r.y(), r.w(), r.h(), 4, over ? 0x55FFFFFF : 0x33FFFFFF);
            g.text(font, m.name(), r.x() + 6, r.y() + 4, 0xFFFFFFFF, true);
            int px = r.x() + r.w() - 26;
            Draw.roundRect(g, px, r.y() + 3, 20, 10, 5, m.isEnabled() ? Draw.ACCENT : 0xFF444455);
            int kx = m.isEnabled() ? px + 11 : px + 1;
            Draw.roundRect(g, kx, r.y() + 4, 8, 8, 4, 0xFFFFFFFF);
        }
        String[] labels = {"Reset Positions", "Cosmetics", "Done"};
        for (int i = 0; i < labels.length; i++) {
            Draw.Rect r = buttonRect(i);
            boolean over = r.contains(mouseX, mouseY);
            Draw.roundRect(g, r.x(), r.y(), r.w(), r.h(), 4, i == 2 ? (over ? 0xFFA78BFA : Draw.ACCENT) : (over ? 0x66FFFFFF : 0x44FFFFFF));
            Draw.centered(g, font, labels[i], r.x() + r.w() / 2, r.y() + 5, 0xFFFFFFFF, true);
        }
        Draw.centered(g, font, "Drag modules  |  Scroll over a module to resize", width / 2 - PANEL_W / 2, height - 16, 0xAAFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        if (event.button() == 0) {
            List<HudModule> mods = HudManager.all();
            for (int i = 0; i < mods.size(); i++) {
                if (rowRect(i).contains(mx, my)) {
                    mods.get(i).setEnabled(!mods.get(i).isEnabled());
                    return true;
                }
            }
            if (buttonRect(0).contains(mx, my)) {
                for (HudModule m : mods) m.resetPosition();
                return true;
            }
            if (buttonRect(1).contains(mx, my)) {
                NovaConfig.save();
                minecraft.setScreen(new CosmeticsScreen(this));
                return true;
            }
            if (buttonRect(2).contains(mx, my)) {
                onClose();
                return true;
            }
            if (panelRect().contains(mx, my)) return true;

            for (int i = mods.size() - 1; i >= 0; i--) {
                HudModule m = mods.get(i);
                if (!m.isEnabled()) continue;
                Draw.Rect r = new Draw.Rect(m.x(minecraft, true), m.y(minecraft, true), m.scaledW(minecraft, true), m.scaledH(minecraft, true));
                if (r.contains(mx, my)) {
                    dragging = m;
                    dragOffX = (int) mx - r.x();
                    dragOffY = (int) my - r.y();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging == null) return super.mouseDragged(event, dragX, dragY);
        int w = dragging.scaledW(minecraft, true), h = dragging.scaledH(minecraft, true);
        int nx = (int) event.x() - dragOffX;
        int ny = (int) event.y() - dragOffY;
        nx = Math.max(0, Math.min(nx, width - w));
        ny = Math.max(0, Math.min(ny, height - h));
        snapX = Math.abs(nx + w / 2 - width / 2) < SNAP;
        snapY = Math.abs(ny + h / 2 - height / 2) < SNAP;
        if (snapX) nx = width / 2 - w / 2;
        if (snapY) ny = height / 2 - h / 2;
        if (nx < SNAP) nx = 0;
        if (ny < SNAP) ny = 0;
        if (nx > width - w - SNAP) nx = width - w;
        if (ny > height - h - SNAP) ny = height - h;
        dragging.setPos(nx, ny);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging != null) {
            dragging = null;
            snapX = snapY = false;
            NovaConfig.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (HudModule m : HudManager.all()) {
            if (!m.isEnabled()) continue;
            Draw.Rect r = new Draw.Rect(m.x(minecraft, true), m.y(minecraft, true), m.scaledW(minecraft, true), m.scaledH(minecraft, true));
            if (r.contains(mouseX, mouseY)) {
                m.setScale(m.scale() + (scrollY > 0 ? 0.1f : -0.1f));
                NovaConfig.save();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
