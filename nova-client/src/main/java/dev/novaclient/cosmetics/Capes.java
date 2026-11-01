package dev.novaclient.cosmetics;

import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Client-side cape catalogue. Capes are only visible to you (no server sync). */
public final class Capes {
    public record Cape(String id, String name) {}

    public static final List<Cape> ALL = List.of(
            new Cape("none", "No Cape"),
            new Cape("nova", "Nova"),
            new Cape("crimson", "Crimson"),
            new Cape("aurora", "Aurora"));

    private static final Map<String, ClientAsset.Texture> CACHE = new HashMap<>();

    /** Full texture path, e.g. novaclient:textures/cape/nova.png, or null for "none". */
    public static Identifier textureOf(String id) {
        if (id == null || id.equals("none")) return null;
        return Identifier.fromNamespaceAndPath("novaclient", "textures/cape/" + id + ".png");
    }

    public static ClientAsset.Texture assetOf(String id) {
        Identifier tex = textureOf(id);
        if (tex == null) return null;
        return CACHE.computeIfAbsent(id, k -> new ClientAsset.ResourceTexture(tex, tex));
    }
}
