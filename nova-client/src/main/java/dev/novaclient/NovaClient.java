package dev.novaclient;

import com.mojang.blaze3d.platform.InputConstants;
import dev.novaclient.config.NovaConfig;
import dev.novaclient.gui.HudEditorScreen;
import dev.novaclient.gui.NovaTitleScreen;
import dev.novaclient.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;

public class NovaClient implements ClientModInitializer {
    public static final String MOD_ID = "novaclient";
    private static KeyMapping openEditor;

    @Override
    public void onInitializeClient() {
        NovaConfig.load();
        HudManager.init();

        openEditor = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.novaclient.editor",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_RSHIFT,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditor.consumeClick()) {
                if (client.screen == null) client.setScreen(new HudEditorScreen(null));
            }
            if (NovaConfig.INSTANCE.customTitleScreen && client.screen instanceof TitleScreen) {
                client.setScreen(new NovaTitleScreen());
            }
        });
    }
}
