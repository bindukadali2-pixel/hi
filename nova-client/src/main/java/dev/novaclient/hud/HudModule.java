package dev.novaclient.hud;

import dev.novaclient.config.NovaConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudModule {
    public final String id;
    public final String name;
    private final int defX, defY;

    protected HudModule(String id, String name, int defX, int defY) {
        this.id = id;
        this.name = name;
        this.defX = defX;
        this.defY = defY;
    }

    public NovaConfig.ModuleState state() {
        return NovaConfig.INSTANCE.modules.computeIfAbsent(id, k -> new NovaConfig.ModuleState(defX, defY));
    }

    public boolean isEnabled() { return state().enabled; }
    public void setEnabled(boolean v) { state().enabled = v; }
    public float scale() { return state().scale; }

    public void setScale(float s) { state().scale = Math.max(0.5f, Math.min(2.0f, s)); }

    public void setPos(int x, int y) {
        state().x = x;
        state().y = y;
    }

    public void resetPosition() {
        state().x = defX;
        state().y = defY;
        state().scale = 1.0f;
    }

    /** Unscaled content size. */
    public abstract int width(Minecraft mc, boolean preview);
    public abstract int height(Minecraft mc, boolean preview);
    /** True when there is nothing to draw in-game (e.g. no potion effects). */
    public abstract boolean isEmpty(Minecraft mc);
    /** Draw at local (0,0); the caller applies translation and scale. */
    protected abstract void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview);

    public int scaledW(Minecraft mc, boolean preview) { return Math.max(1, Math.round(width(mc, preview) * scale())); }
    public int scaledH(Minecraft mc, boolean preview) { return Math.max(1, Math.round(height(mc, preview) * scale())); }

    public int x(Minecraft mc, boolean preview) {
        int max = Math.max(0, mc.getWindow().getGuiScaledWidth() - scaledW(mc, preview));
        return Math.max(0, Math.min(state().x, max));
    }

    public int y(Minecraft mc, boolean preview) {
        int max = Math.max(0, mc.getWindow().getGuiScaledHeight() - scaledH(mc, preview));
        return Math.max(0, Math.min(state().y, max));
    }

    public final void draw(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        if (!isEnabled()) return;
        if (!preview && isEmpty(mc)) return;
        g.pose().pushMatrix();
        g.pose().translate((float) x(mc, preview), (float) y(mc, preview));
        g.pose().scale(scale(), scale());
        render(g, mc, preview);
        g.pose().popMatrix();
    }
}
