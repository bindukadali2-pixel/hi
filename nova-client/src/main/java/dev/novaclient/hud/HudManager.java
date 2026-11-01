package dev.novaclient.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import dev.novaclient.gui.HudEditorScreen;

import java.util.List;

public final class HudManager {
    private static final List<HudModule> MODULES = List.of(
            new FpsModule(), new CoordsModule(), new PotionModule(), new ArmorModule(), new KeystrokesModule());

    private HudManager() {}

    public static List<HudModule> all() { return MODULES; }

    public static void init() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("novaclient", "hud"), (g, delta) -> render(g));
    }

    private static void render(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.screen instanceof HudEditorScreen) return;
        for (HudModule m : MODULES) m.draw(g, mc, false);
    }
}
