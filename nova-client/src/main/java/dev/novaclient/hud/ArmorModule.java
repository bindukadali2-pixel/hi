package dev.novaclient.hud;

import dev.novaclient.gui.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ArmorModule() { super("armor", "Armor HUD", 6, 120); }

    private List<ItemStack> stacks(Minecraft mc, boolean preview) {
        List<ItemStack> out = new ArrayList<>();
        if (mc.player != null) {
            for (EquipmentSlot slot : SLOTS) {
                ItemStack s = mc.player.getItemBySlot(slot);
                if (!s.isEmpty()) out.add(s);
            }
        }
        if (out.isEmpty() && preview) {
            ItemStack[] demo = {
                    new ItemStack(Items.DIAMOND_HELMET), new ItemStack(Items.DIAMOND_CHESTPLATE),
                    new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.DIAMOND_BOOTS)};
            int[] dmg = {40, 120, 200, 10};
            for (int i = 0; i < demo.length; i++) {
                demo[i].setDamageValue(dmg[i]);
                out.add(demo[i]);
            }
        }
        return out;
    }

    private String durability(ItemStack s) {
        return s.isDamageableItem() ? String.valueOf(s.getMaxDamage() - s.getDamageValue()) : "";
    }

    @Override
    public int width(Minecraft mc, boolean preview) {
        int textW = 0;
        for (ItemStack s : stacks(mc, preview)) textW = Math.max(textW, mc.font.width(durability(s)));
        return 6 + 16 + 4 + textW + 6;
    }

    @Override public int height(Minecraft mc, boolean preview) { return 4 + stacks(mc, preview).size() * 18; }
    @Override public boolean isEmpty(Minecraft mc) { return stacks(mc, false).isEmpty(); }

    @Override
    protected void render(GuiGraphicsExtractor g, Minecraft mc, boolean preview) {
        Draw.panel(g, 0, 0, width(mc, preview), height(mc, preview));
        int y = 3;
        for (ItemStack s : stacks(mc, preview)) {
            g.item(s, 5, y);
            if (s.isDamageableItem()) {
                float pct = 1f - s.getDamageValue() / (float) s.getMaxDamage();
                int color = pct > 0.5f ? 0xFF55FF55 : pct > 0.2f ? 0xFFFFDD55 : 0xFFFF5555;
                g.text(mc.font, durability(s), 25, y + 4, color, true);
            }
            y += 18;
        }
    }
}
