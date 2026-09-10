# Selfaware

Keep the feature in shared source. Version adapters only bridge Minecraft API changes; loader entrypoints only register the mod.

versions.json is the source of truth for local builds and CI. Pin versions. Adding a row means building it and checking its jar, not just adding a support claim to the README.

Use Minecraft's nametag renderer. Keep the mixin restricted to the local player and respect camera, HUD, invisibility, spectator, and team visibility. Client classes belong only in client mixins.

Run python3 scripts/build.py for the full matrix. Record runtime checks separately from builds. Never describe a target as tested in-game based only on compilation.

Write short, plain README text. Commit subjects start with one cozy emoji and a concrete message, for example `🌸 add third-person nametags`. Keep one coherent change per commit. Commit and push only when asked.
