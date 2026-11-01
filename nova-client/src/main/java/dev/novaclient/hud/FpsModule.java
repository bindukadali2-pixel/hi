package dev.novaclient.hud;

import dev.novaclient.gui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FpsModule extends HudModule {
    public FpsModule() { super("fps", "FPS", 6, 6); }

    private String text(Minecraft mc) { return "FPS: " + mc.getFps(); }

    @Override public int width(Minecraft mc, boolean preview) { return mc.font.width(text(mc)) + 10; }
    @Override public int height(Minecraft mc, boolean preview) { return 16; }
    @Override public boolean isEmpty(Minecraft mc) { return false; }

    @Override
    protected void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        Draw.panel(g, 0, 0, width(mc, preview), height(mc, preview));
        g.text(mc.font, text(mc), 6, 4, 0xFFFFFFFF, true);
    }
}
