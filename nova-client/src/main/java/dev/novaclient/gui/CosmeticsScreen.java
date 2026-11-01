package dev.novaclient.gui;

import dev.novaclient.config.NovaConfig;
import dev.novaclient.cosmetics.Capes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/** Pick a cape. Capes are client-side only, so only you can see them. */
public class CosmeticsScreen extends Screen {
    private final Screen parent;

    public CosmeticsScreen(Screen parent) {
        super(Component.literal("Cosmetics"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        NovaConfig.save();
        minecraft.setScreen(parent);
    }

    private Draw.Rect card(int i) {
        int n = Capes.ALL.size();
        int cw = 80, ch = 120, gap = 12;
        int total = n * cw + (n - 1) * gap;
        return new Draw.Rect(width / 2 - total / 2 + i * (cw + gap), height / 2 - ch / 2, cw, ch);
    }

    private Draw.Rect back() { return new Draw.Rect(width / 2 - 50, height - 40, 100, 20); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        Draw.vGradient(g, 0, 0, width, height, 0xFF090914, 0xFF2B1250);
        Draw.centered(g, font, "COSMETICS", width / 2, 30, 0xFFFFFFFF, true);
        Draw.centered(g, font, "Capes", width / 2, 44, Draw.ACCENT2, true);

        List<Capes.Cape> all = Capes.ALL;
        for (int i = 0; i < all.size(); i++) {
            Capes.Cape c = all.get(i);
            Draw.Rect r = card(i);
            boolean selected = c.id().equals(NovaConfig.INSTANCE.cape);
            boolean over = r.contains(mouseX, mouseY);
            Draw.roundRect(g, r.x(), r.y(), r.w(), r.h(), 6, over ? 0xAA2A2A44 : 0x99141426);
            if (selected) g.outline(r.x(), r.y(), r.w(), r.h(), Draw.ACCENT);
            Identifier tex = Capes.textureOf(c.id());
            if (tex != null) {
                // back panel of a cape texture is the 10x16 region at (1,1)
                g.blit(RenderPipelines.GUI_TEXTURED, tex, r.x() + r.w() / 2 - 20, r.y() + 12, 1, 1, 40, 64, 10, 16, 64, 32);
            } else {
                Draw.centered(g, font, "-", r.x() + r.w() / 2, r.y() + 40, 0x88FFFFFF, true);
            }
            Draw.centered(g, font, c.name(), r.x() + r.w() / 2, r.y() + 88, 0xFFFFFFFF, true);
            Draw.centered(g, font, selected ? "Equipped" : "Click to equip", r.x() + r.w() / 2, r.y() + 102,
                    selected ? Draw.ACCENT2 : 0x88FFFFFF, false);
        }

        Draw.Rect b = back();
        Draw.roundRect(g, b.x(), b.y(), b.w(), b.h(), 5, b.contains(mouseX, mouseY) ? 0xFFA78BFA : Draw.ACCENT);
        Draw.centered(g, font, "Back", b.x() + b.w() / 2, b.y() + 6, 0xFFFFFFFF, true);
        Draw.centered(g, font, "Client-side cosmetics: only you can see them.", width / 2, height - 14, 0x66FFFFFF, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (int i = 0; i < Capes.ALL.size(); i++) {
                if (card(i).contains(event.x(), event.y())) {
                    NovaConfig.INSTANCE.cape = Capes.ALL.get(i).id();
                    NovaConfig.save();
                    return true;
                }
            }
            if (back().contains(event.x(), event.y())) {
                onClose();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
