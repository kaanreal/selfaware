# Verification

The 60-target matrix passed on 2026-09-11. `python3 scripts/build.py` and `python3 scripts/build.py --verify-only` checked every jar's loader metadata, mixin configuration, refmap where required, class list, optional Simple Voice Chat boundary, and Java class-file level. The focused JUnit suite for Fabric 1.19.4 also passes, including the persisted toggle defaults and parser checks.

Runtime smoke checks reached a loaded client with no Selfaware mixin errors for Fabric 1.21.1, Quilt 1.21.1, Forge 1.20.1, NeoForge 1.21.1, and NeoForge 26.2. The Forge check uses Java 17, the 1.21 checks use Java 21, and the 26.2 check uses Java 25. Gradle selects the target's runtime from `versions.json` and can provision missing JDKs.

The optional integration was smoke-tested on Fabric 1.19.4 and Fabric 1.21.1 with the released Simple Voice Chat jar for each exact version. Both clients loaded the plugin and applied the Selfaware mixin to Simple Voice Chat's RenderEvents class without an injection error.

Kaan manually checked the feature on Fabric 1.21.1 and Fabric 26.2: the nametag and Simple Voice Chat icon are visible in both F5 views and disappear with F1. The 26.2 check used the disabled state, which places the speaker-off icon beside the local name.

## How to test the matrix

Do not open one client per target. `python3 scripts/build.py` builds and verifies every target in `versions.json` in one sequential run. That catches Minecraft mappings, Java levels, loader metadata, mixin lists, refmaps, and the optional API boundary.

Runtime checks use one client for each renderer adapter family: legacy, partial 1.21.1, modern 1.21.3-1.21.8, modern 1.21.10, modern 1.21.11, modern 26.1, and modern 26.2. The loader smoke set adds one Fabric, Quilt, Forge, and NeoForge target when those loader paths are changed. Each client only needs to reach the title screen or a quick-play world, then its log is checked for plugin loading, mixin application, and errors before the process closes.

`python3 scripts/smoke.py` runs that set sequentially. It uses the configured timeout to close each client, saves the Gradle output under `build/smoke/`, and checks the latest Minecraft log plus the exported Simple Voice Chat renderer when that mod is installed. Pass target ids to run only a smaller smoke set.

`python3 scripts/smoke.py --svc-all` resolves the latest released SVC file for every declared version and loader. It temporarily isolates local test jars, uses the published jar directly on Fabric and Quilt, and lets Loom remap the Maven dependency on Forge and NeoForge. Unsupported SVC pairs are recorded as skipped. This is the full dependency matrix, while the normal smoke command stays small enough for quick local checks.

The raw Forge 1.20.1 jar in the run directory is a production file and expects obfuscated Forge names. The all-SVC runner avoids that dev-launcher mismatch by resolving the same release through the remapped Maven path. Forge 1.20.1, Fabric 1.21.1, Fabric 26.2, and NeoForge 26.2 have passed this path locally.

The visual check stays on Fabric 1.21.1 and the newest Fabric target. Those cover the oldest special renderer and the current renderer while keeping the manual part small. Check the vanilla settings buttons, focus, clicks, the server-only Donut money button, and saved toggles. A new Minecraft or Simple Voice Chat renderer family adds one new smoke target, not another full manual pass.

The remaining in-game checklist is sneaking, invisibility, spectator mode, all four team visibility settings, formatted names, another player's nametag, a vanilla server, compatibility with 3D Skin Layers, and the live talking, whispering, and disconnected states. These are behavior checks for the next release pass, not build-matrix claims.

Build logs and machine-readable results are written to `build/verification/`. CI keeps the jars, reports, and logs for each target.
