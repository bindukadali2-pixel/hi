package dev.novaclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class NovaConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("novaclient.json");

    public static NovaConfig INSTANCE = new NovaConfig();

    public boolean customTitleScreen = true;
    public String cape = "none";
    public Map<String, ModuleState> modules = new LinkedHashMap<>();

    public static final class ModuleState {
        public boolean enabled = true;
        public int x;
        public int y;
        public float scale = 1.0f;

        public ModuleState() {}

        public ModuleState(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE)) {
                NovaConfig loaded = GSON.fromJson(r, NovaConfig.class);
                if (loaded != null) {
                    if (loaded.modules == null) loaded.modules = new LinkedHashMap<>();
                    if (loaded.cape == null) loaded.cape = "none";
                    INSTANCE = loaded;
                }
            } catch (Exception e) {
                System.err.println("[NovaClient] Could not read config, using defaults: " + e);
            }
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(INSTANCE, w);
        } catch (IOException e) {
            System.err.println("[NovaClient] Could not save config: " + e);
        }
    }
}
