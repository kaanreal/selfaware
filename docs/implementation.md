# How it works

Selfaware shows the local player's vanilla nametag in both third-person views. It runs on the client and has no networking or required runtime dependencies.

The name must disappear in first person, with F1, when invisible or spectating, and when the scoreboard team hides names from its own members. Other entities keep vanilla behavior. Sneaking keeps vanilla distance and text rendering. Name formatting, team prefixes, and scoreboard text remain Minecraft's responsibility.

Mod Menu opens a small Selfaware config screen with nametag, Simple Voice Chat icon, and server formatting toggles. The integration is optional, so Selfaware does not depend on Mod Menu at runtime. `SelfawareConfig` stores the settings in `config/selfaware.properties`, so the choice survives restarts and is shared by every version-specific jar. The SVC toggle is checked before the optional integration renders anything, and the nametag toggle is checked before the local name is selected.

Each row in versions.json picks the Minecraft version, loader, Java level, and renderer adapter. Every target compiles the same feature code. Loader entrypoints only register the mod. An adapter changes when Minecraft changes a method signature.

Simple Voice Chat is an optional compile-only API dependency. When it is installed, Selfaware registers its normal VoicechatPlugin entrypoint on Fabric and Quilt and its Forge plugin annotation for Forge and NeoForge. The plugin reads the local voice state and client icon settings through the public client API. Version-specific mixins call Simple Voice Chat's own private nameplate renderer after it skips the local player, so its sprite atlas, offsets, alpha, config settings, hidden HUD behavior, and NameTagIconRenderEvent cancellation stay identical. The hook runs at every return because Simple Voice Chat exits early for the local player. The mixin is marked pseudo and does nothing when the voice chat renderer is absent.

The local selection follows Simple Voice Chat's order: whisper speaker, speaker, disconnected, then speaker off. A group icon is not shown on the local name because Simple Voice Chat only uses that icon when a player's group differs from the viewer's group, which cannot happen for the viewer itself.

The 1.21.10, 1.21.11, and 26.x renderers pass the nameplate pose into their icon method, so those adapters copy Simple Voice Chat's attachment translation, camera rotation, and scale before calling it. Older renderers keep that pose work inside their own icon method. This split follows the renderer code instead of guessing at screen offsets.

The SVC smoke matrix resolves a released jar for each exact version and loader. Fabric and Quilt load that jar from the run directory because the published file contains nested API modules. Forge and NeoForge receive the same version through a remapped Maven runtime dependency. This keeps the compatibility test close to the way each loader actually resolves mods.

Every declared target must build, and its jar must contain valid metadata and mixins. The config screen has small source adapters because Screen and Button APIs changed several times between 1.16.5 and 26.2. Fabric and Quilt targets compile against the matching Mod Menu API release, while Forge and NeoForge jars do not include the integration. The runtime checklist covers both F5 views, F1, first person, sneaking, invisibility, spectator mode, team visibility, and a second player. Compilation alone does not count as an in-game test.

## Reference notes

- [Simple Voice Chat](https://github.com/henkelmax/simple-voice-chat) separates common code from its loader projects and keeps Minecraft versions on separate branches. Selfaware follows the same boundary between behavior and loader registration.
- [3D Skin Layers](https://github.com/tr7zw/3d-Skin-Layers) shares source with Stonecutter, version manifests, and ProcessedModTemplate. Its current legacy.json and modern.json provided the starting list of targets.
- [Architectury Loom](https://docs.architectury.dev/loom/introduction/) supplies remapping and Forge/NeoForge development support for obfuscated releases. Minecraft 26.x uses Fabric Loom or NeoGradle directly.

Source-set adapters are enough for this first feature. Stonecutter becomes useful if the version-specific code grows past a few small adapters. Dependencies are pinned, and a new version needs its own matrix entry and build check.

## Server formatting

The opt-in setting `server_formatting_enabled` changes only the local player's name selected by the entity renderer. Off preserves the existing vanilla path. On uses that player's tab-list component when present and nonblank, otherwise their vanilla team-formatted name. It copies the component so the appearance hook can identify this exact draw without affecting another player, chat, or the tab list. It does not combine tab and team prefixes, which could duplicate a rank.

On 1.19.4+, the detector reads text displays within 32 blocks once per client tick. A candidate must contain a connected player's exact profile name with username boundaries, and either ride that player or sit within one horizontal block and -0.5 to 3 blocks of their head. Invisible players and spectators are excluded. The closest candidate supplies its shadow flag and custom background color. The last observed appearance is remembered for the current server and stored in the shared config, so a missing candidate does not make the local tag revert to vanilla. A new server loads only its own cached appearance. Before 1.19.4, text displays do not exist and only the name fallback applies.

Minecraft still renders the name, score line, distance, depth behavior, and sneaking alpha. The appearance hook changes only shadow and background for the local name component, leaving the second vanilla pass without a background. On 1.21.1, the Donut money option fills a missing local below-name score from a compact money value in the local player's tab-list display. The option is exposed only by the 1.21.1 adapter and only while the current server address is `donutsmp.net` or a subdomain. Text-display scaling, billboards, opacity, custom fonts from another player's component, and extra lines are not reconstructed. A plugin that offsets text using a transformation rather than mounting or positioning it near the head may not be detected. Nicknames that omit the profile name are deliberately not guessed.

### Research notes (2026-09-12)

- [TAB's nametag guide](https://github.com/NEZNAMY/TAB/wiki/Feature-guide:-Nametags) describes scoreboard teams: prefixes, suffixes, name color, and visibility. These already reach the vanilla player renderer. Teams cannot encode arbitrary text-display shadows or backgrounds.
- [TAB's tab-list guide](https://github.com/NEZNAMY/TAB/wiki/Feature-guide:-Tablist-name-formatting) treats tab names separately. Using the local tab entry is a useful fallback, but it is not proof that the server gives that same overhead name to other viewers.
- [Paper's display documentation](https://docs.papermc.io/paper/dev/display-entities/) documents separate text-display entities and background/shadow controls. [DisplayTags](https://github.com/imskeptical/DisplayTags/blob/main/src/main/java/me/itsskeptical/displaytags/nametags/Nametag.java) creates a client text display, applies configurable text shadow/background, and mounts it on the player for viewers.
- [TAB's below-name guide](https://github.com/NEZNAMY/TAB/wiki/Feature-guide:-Belowname) describes a separate scoreboard objective. A money line might instead be a plugin's display text. A screenshot alone cannot distinguish those paths or establish DonutSMP's current plugin setup.

There is no generic packet containing a server's nametag template. A client cannot recover an unsent balance, rank, or viewer-specific line by inspecting a neighbor's tag. Nearby appearance matching is an approximation explicitly chosen for this feature; the sampled text is never substituted into the local name.
