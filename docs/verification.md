# Verification

The 60-target matrix passed on 2026-09-10. `python3 scripts/build.py --verify-only` checked every jar's loader metadata, mixin configuration, refmap where required, class list, and Java class-file level. The regular default build and the JUnit visibility tests also pass.

Runtime smoke checks reached a loaded client with no Selfaware mixin errors for Fabric 1.21.1, Quilt 1.21.1, Forge 1.20.1, NeoForge 1.21.1, and NeoForge 26.2. The Forge check uses Java 17, the 1.21 checks use Java 21, and the 26.2 check uses Java 25. Gradle selects the target's runtime from `versions.json` and can provision missing JDKs.

Kaan manually checked the feature on Fabric 1.21.1: the nametag is visible in both F5 views and disappears with F1.

The remaining in-game checklist is sneaking, invisibility, spectator mode, all four team visibility settings, formatted names, another player's nametag, a vanilla server, and compatibility with 3D Skin Layers and Simple Voice Chat. These are behavior checks for the next release pass, not build-matrix claims.

Build logs and machine-readable results are written to `build/verification/`. CI keeps the jars, reports, and logs for each target.
