package dev.novaclient.hud;

import dev.novaclient.gui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class KeystrokesModule extends HudModule {
    public KeystrokesModule() { super("keystrokes", "Keystrokes", 6, 190); }

    @Override public int width(Minecraft mc, boolean preview) { return 64; }
    @Override public int height(Minecraft mc, boolean preview) { return 56; }
    @Override public boolean isEmpty(Minecraft mc) { return false; }

    private void key(GuiGraphicsExtractor g, Minecraft mc, int x, int y, int w, int h, String label, boolean down) {
        Draw.roundRect(g, x, y, w, h, 3, down ? Draw.ACCENT : Draw.PANEL);
        Draw.centered(g, mc.font, label, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF, true);
    }

    @Override
    protected void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        var o = mc.options;
        key(g, mc, 22, 0, 20, 20, "W", o.keyUp.isDown());
        key(g, mc, 0, 22, 20, 20, "A", o.keyLeft.isDown());
        key(g, mc, 22, 22, 20, 20, "S", o.keyDown.isDown());
        key(g, mc, 44, 22, 20, 20, "D", o.keyRight.isDown());
        key(g, mc, 0, 44, 64, 12, "-----", o.keyJump.isDown());
    }
}
