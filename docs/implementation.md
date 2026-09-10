# How it works

Selfaware shows the local player's vanilla nametag in both third-person views. It runs on the client and has no networking, configuration, or extra runtime API dependencies.

The name must disappear in first person, with F1, when invisible or spectating, and when the scoreboard team hides names from its own members. Other entities keep vanilla behavior. Sneaking keeps vanilla distance and text rendering. Name formatting, team prefixes, and scoreboard text remain Minecraft's responsibility.

Each row in versions.json picks the Minecraft version, loader, Java level, and renderer adapter. Every target compiles the same feature code. Loader entrypoints only register the mod. An adapter changes when Minecraft changes a method signature.

Every declared target must build, and its jar must contain valid metadata and mixins. The runtime checklist covers both F5 views, F1, first person, sneaking, invisibility, spectator mode, team visibility, and a second player. Compilation alone does not count as an in-game test.

## Reference notes

- [Simple Voice Chat](https://github.com/henkelmax/simple-voice-chat) separates common code from its loader projects and keeps Minecraft versions on separate branches. Selfaware follows the same boundary between behavior and loader registration.
- [3D Skin Layers](https://github.com/tr7zw/3d-Skin-Layers) shares source with Stonecutter, version manifests, and ProcessedModTemplate. Its current legacy.json and modern.json provided the starting list of targets.
- [Architectury Loom](https://docs.architectury.dev/loom/introduction/) supplies remapping and Forge/NeoForge development support for obfuscated releases. Minecraft 26.x uses Fabric Loom or NeoGradle directly.

Source-set adapters are enough for this first feature. Stonecutter becomes useful if the version-specific code grows past a few small adapters. Dependencies are pinned, and a new version needs its own matrix entry and build check.
