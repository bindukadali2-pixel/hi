package dev.novaclient.hud;

import dev.novaclient.gui.Draw;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PotionModule extends HudModule {
    private static final String[] ROMAN = {"", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    private record Line(String name, String time, int color, boolean urgent) {}

    public PotionModule() { super("potions", "Potion Effects", 6, 52); }

    private List<Line> lines(Minecraft mc, boolean preview) {
        List<Line> out = new ArrayList<>();
        if (mc.player != null) {
            List<MobEffectInstance> effects = new ArrayList<>(mc.player.getActiveEffects());
            effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration).reversed());
            for (MobEffectInstance inst : effects) {
                String name = inst.getEffect().value().getDisplayName().getString();
                int amp = inst.getAmplifier();
                if (amp > 0) name += " " + (amp < ROMAN.length ? ROMAN[amp] : String.valueOf(amp + 1));
                String time;
                boolean urgent = false;
                if (inst.isInfiniteDuration()) {
                    time = "**";
                } else {
                    int secs = inst.getDuration() / 20;
                    time = String.format("%d:%02d", secs / 60, secs % 60);
                    urgent = secs < 10;
                }
                out.add(new Line(name, time, 0xFF000000 | inst.getEffect().value().getColor(), urgent));
            }
        }
        if (out.isEmpty() && preview) {
            out.add(new Line("Speed II", "3:42", 0xFF7CAFC6, false));
            out.add(new Line("Regeneration", "0:08", 0xFFCD5CAB, true));
            out.add(new Line("Fire Resistance", "8:00", 0xFFE49A3A, false));
        }
        return out;
    }

    @Override
    public int width(Minecraft mc, boolean preview) {
        int nameW = 0, timeW = 0;
        for (Line l : lines(mc, preview)) {
            nameW = Math.max(nameW, mc.font.width(l.name()));
            timeW = Math.max(timeW, mc.font.width(l.time()));
        }
        return 6 + 6 + 4 + nameW + 12 + timeW + 6;
    }

    @Override
    public int height(Minecraft mc, boolean preview) {
        return 6 + lines(mc, preview).size() * 12;
    }

    @Override public boolean isEmpty(Minecraft mc) { return lines(mc, false).isEmpty(); }

    @Override
    protected void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        List<Line> lines = lines(mc, preview);
        int w = width(mc, preview);
        Draw.panel(g, 0, 0, w, height(mc, preview));
        boolean flash = (Util.getMillis() / 250) % 2 == 0;
        int y = 4;
        for (Line l : lines) {
            g.fill(7, y + 1, 13, y + 7, l.color());
            g.text(mc.font, l.name(), 17, y, 0xFFFFFFFF, true);
            int timeColor = l.urgent() ? (flash ? 0xFFFF5555 : 0xFFAA2222) : 0xFFCCCCCC;
            g.text(mc.font, l.time(), w - 6 - mc.font.width(l.time()), y, timeColor, true);
            y += 12;
        }
    }
}
