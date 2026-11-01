# Nova Client (Fabric, Minecraft 26.1.2)

A Lunar-style client mod:

- **Home screen** replacing the title screen: dark gradient, drifting stars, big logo, pill buttons (Singleplayer, Multiplayer, HUD Editor, Cosmetics, Options, Quit).
- **HUD modules**: FPS, Coordinates, Potion Effects (with live timers, flashing when almost out), Armor HUD (durability), Keystrokes.
- **Drag support**: press **Right Shift** in-game (or use HUD Editor on the home screen). Drag modules anywhere (snaps to edges and screen centre), scroll over a module to resize it, toggle modules in the side panel.
- **Cosmetics**: three capes (Nova, Crimson, Aurora). Client-side only, so only you can see them.

Settings are saved to `config/novaclient.json`.

## Build the jar

Requirements: **JDK 25** and **Gradle 9.3+** (or open the folder in IntelliJ IDEA, which sets this up for you).

    gradle build

The jar is written to `build/libs/nova-client-1.0.0.jar`. Put it in your `mods` folder together with **Fabric API for 26.1.2** and Fabric Loader 0.19.3+.

No local setup? Push this folder to a GitHub repo: the included workflow (`.github/workflows/build.yml`) builds the jar and attaches it as a downloadable artifact.

## Customising

- Accent colours: `Draw.ACCENT` / `Draw.ACCENT2` in `gui/Draw.java`
- Add a HUD module: extend `HudModule` and add it to the list in `hud/HudManager.java`
- Add a cape: drop a 64x32 PNG in `assets/novaclient/textures/cape/` and add it to `cosmetics/Capes.java`

## Known risk spots (26.1 changed a lot of names)

If the build fails, the likeliest places are: `ArmorModule` (`g.item`), `AbstractClientPlayerMixin` / `Capes` (`PlayerSkin`, `ClientAsset`), and the screen mouse method signatures. Deleting the mixin file and its entry in `novaclient.mixins.json` removes the cape feature but leaves everything else working.
