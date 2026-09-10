# Selfaware

See your own nametag in third person. That's it for now.

Press F5 to see it from either side. Minecraft still handles the text, team prefix, and name color. The tag hides with F1, in first person, while invisible or spectating, and when your team hides names from its own members.

Install it on your client. Servers and other players don't need it. No Fabric API or Architectury API dependency.

## Versions

| Loader | Minecraft versions |
| --- | --- |
| Fabric | 1.16.5, 1.18.2, 1.19.2, 1.19.4, 1.20.1, 1.20.2, 1.20.4, 1.20.6, 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 |
| Quilt | 1.16.5, 1.18.2, 1.19.2, 1.19.4, 1.20.1, 1.20.2, 1.20.4, 1.20.6, 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 |
| Forge | 1.16.5, 1.18.2, 1.19.2, 1.19.4, 1.20.1, 1.20.2, 1.20.4, 1.20.6, 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.8, 1.21.10 |
| NeoForge | 1.20.2, 1.20.4, 1.20.6, 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1.2, 26.2 |

## Building

Use JDK 25 to run Gradle. The build compiles each jar for its Minecraft version's Java requirement, and `runClient` selects the matching game runtime. Missing JDKs can be provisioned by Gradle's toolchain resolver.

```sh
./gradlew build
./gradlew build -Ptarget=1.20.1-forge
./gradlew runClient -Ptarget=1.21.1-fabric
python3 scripts/build.py
```

The default target is 1.21.1 Fabric. Jars go in `build/<target>/libs/`; use the jar without `-sources` in its name.

[versions.json](versions.json) lists the exact targets. `python3 scripts/build.py --list` prints their ids. The full build runs the same tests and checks jar metadata, mixins, and Java compatibility for every target. CI reads this same list.

Pass `--loader fabric`, `--loader forge`, `--loader neoforge`, or `--loader quilt` to build one loader's full set.

Every version uses the same feature code. Small renderer adapters cover Minecraft API changes, and the loader entrypoints only register the mod. The version setup follows ideas from [3D Skin Layers](https://github.com/tr7zw/3d-Skin-Layers) and [Simple Voice Chat](https://github.com/henkelmax/simple-voice-chat). [docs/implementation.md](docs/implementation.md) has the details.

Build verification and in-game checks are tracked separately in [docs/verification.md](docs/verification.md). A listed target is not a promise that every modpack works with it.
