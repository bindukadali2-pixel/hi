package dev.novaclient.hud;

import dev.novaclient.gui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CoordsModule extends HudModule {
    public CoordsModule() { super("coords", "Coordinates", 6, 26); }

    private String text(Minecraft mc) {
        if (mc.player == null) return "XYZ: 0 / 64 / 0";
        return "XYZ: " + mc.player.getBlockX() + " / " + mc.player.getBlockY() + " / " + mc.player.getBlockZ();
    }

    @Override public int width(Minecraft mc, boolean preview) { return mc.font.width(text(mc)) + 10; }
    @Override public int height(Minecraft mc, boolean preview) { return 16; }
    @Override public boolean isEmpty(Minecraft mc) { return mc.player == null; }

    @Override
    protected void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        Draw.panel(g, 0, 0, width(mc, preview), height(mc, preview));
        g.text(mc.font, text(mc), 6, 4, 0xFFFFFFFF, true);
    }
}
