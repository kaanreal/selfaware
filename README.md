<div align="center">
<img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Title.png" alt="Selfaware" width="204" style="margin: 5px 10px;">

<p align="center" style="text-align: center;">
  <a href="https://modrinth.com/mod/selfaware"><img src="https://voxelforge-oss.github.io/voxicons/voxicons/icons-64/brands/modrinth.png" alt="Modrinth" style="margin: 5px 10px;"></a>
  <a href="https://github.com/kaanreal/selfaware"><img src="https://voxelforge-oss.github.io/voxicons/voxicons/icons-64/brands/github.png" alt="GitHub" style="margin: 5px 10px;"></a>
  <a href="https://buymeacoffee.com/kaandev"><img src="https://voxelforge-oss.github.io/voxicons/voxicons/icons-64/brands/kofi.png" alt="Buy Me a Coffee" style="margin: 5px 10px;"></a>
</p>

### See your own nametag in third person, with server-aware styling, DonutSMP extras, and optional Simple Voice Chat icons.

</div>

---

Minecraft normally hides your own nametag in first person and renders it differently in third person. Selfaware gives the local player the same kind of label that can be seen above other players, while keeping the server's own content and behavior rules intact.

> [!NOTE]
> Selfaware changes what you see locally, so no server installation is needed for its features.

---

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Features.png" alt="Features" width="188"></p>

### Your nametag, finally visible

- See your nametag in third person, including front-facing F5 views.
- Keep Minecraft's normal scale, centering, depth, opacity, and occlusion behavior.
- See the label while looking at your player in inventory screens.
- Use the live player preview in the settings screen.

### Server formatting

- Learn the visual presentation used by compatible server-side TextDisplay nametags.
- Match details such as shadows, background, opacity, transformation, billboard, and depth behavior.
- Save learned presentation per server address and reuse it after reconnecting.
- Keep all local content local. Remote text is used only to confirm a presentation, never as your own rank, balance, or name.

### DonutSMP

On DonutSMP, Selfaware reads your own rank and prefix from your local TAB entry. The optional balance line comes from Donut's live TAB footer, including its server-provided text styling.

Rank and balance can be toggled independently. These extras are limited to DonutSMP and are not taken from arbitrary scoreboards or other players.

### Simple Voice Chat

Simple Voice Chat is optional. When it is installed, Selfaware can show your current voice status and group icons beside your own nametag using the same speaker-style assets used for player nametags.

### Settings

Open the settings with any of these:

- Unbound by default, then configurable in Minecraft Controls
- `Minecraft Controls` to rebind the key
- `/selfaware`
- `Mod Menu` → `Configure`

The settings screen contains General, Voice Chat, and DonutSMP sections. It also includes a live 3D preview and small explanations for each option.

---

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Compatibility%20%26%20Dependencies.png" alt="Compatibility &amp; Dependencies" width="568"></p>

The current release is **1.0.0**. Download the JAR matching both your exact
Minecraft version and loader.

| Minecraft | Loaders |
| --- | --- |
| 1.16.5 | Fabric, Forge, Quilt |
| 1.18.2 | Fabric, Forge, Quilt |
| 1.19.2 | Fabric, Forge, Quilt |
| 1.19.4 | Fabric, Forge, Quilt |
| 1.20.1 | Fabric, Forge, Quilt |
| 1.20.2 | Fabric, Forge, NeoForge, Quilt |
| 1.20.4 | Fabric, Forge, NeoForge, Quilt |
| 1.20.6 | Fabric, Forge, NeoForge, Quilt |
| 1.21.1 | Fabric, Forge, NeoForge, Quilt |
| 1.21.3 | Fabric, Forge, NeoForge, Quilt |
| 1.21.4 | Fabric, Forge, NeoForge, Quilt |
| 1.21.5 | Fabric, Forge, NeoForge, Quilt |
| 1.21.8 | Fabric, Forge, NeoForge, Quilt |
| 1.21.10 | Fabric, Forge, NeoForge, Quilt |
| 1.21.11 | Fabric, NeoForge, Quilt |
| 26.1 | Fabric, Quilt |
| 26.1.2 | NeoForge |
| 26.2 | Fabric, NeoForge, Quilt |

These are the exact release targets, not one broad Minecraft version range. The
1.16.5 artifact uses Java 8,
1.18.2 through 1.20.4 use Java 17,
1.20.6 through 1.21.11 use Java 21, and 26.x uses Java 25.

Selfaware keeps shared behavior in `src/main` and Minecraft-facing code in
small version-family adapters. Every adapter is compiled against its own
Minecraft mappings and produces its own JAR. `versions.json` is the single
source of truth for supported versions, loader versions, Java versions, and
adapters.

| Dependency | When it is needed | Notes |
| --- | --- | --- |
| Fabric API | Fabric and Quilt before 26.x | Required runtime dependency |
| Mod Menu | Fabric and Quilt | Adds the Configure entry |
| Simple Voice Chat | Optional | Adds voice icon integration |
| DonutSMP | Optional server integration | Enables local rank and TAB balance features on supported targets |

---

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Installation.png" alt="Installation" width="228"></p>

1. Install the loader matching your download: Fabric, Quilt, Forge, or NeoForge.
2. Install the required dependencies for that loader and Minecraft version.
3. Grab the matching release JAR from [GitHub](https://github.com/kaanreal/selfaware/releases) or [Modrinth](https://modrinth.com/mod/selfaware).
4. Put that JAR in your `.minecraft/mods` folder.
5. Launch Minecraft and bind **Open Selfaware menu** in Controls, or run `/selfaware`.

Fabric and Quilt builds before 26.x require Fabric API and Mod Menu. The 26.x
Fabric and Quilt builds require Mod Menu only. Forge and NeoForge need neither.
Simple Voice Chat is optional.

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Configuration.png" alt="Configuration" width="272"></p>

Selfaware stores its settings in:

```text
config/selfaware.properties
```

Learned visual presentations are stored separately in:

```text
config/selfaware-server-presentations.json
```

Presentation storage contains visual properties only. It does not save text, usernames, ranks, prefixes, balances, icons, or scores.

---

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/Privacy%20and%20server%20rules.png" alt="Privacy and server rules" width="528"></p>

Selfaware does not use an external service or make HTTP requests. Its server formatting support observes data already sent to the client, and its DonutSMP and voice integrations read local client state.

The mod is visual and client-side, but server rules can change. Check the current rules of a server before using client modifications there.

<p align="center"><img src="https://raw.githubusercontent.com/kaanreal/selfaware/main/assets/License.png" alt="License" width="156"></p>

Selfaware is created by Kaanreal and released under the [MIT License](LICENSE).

### Building

The 1.21.x builds require JDK 21 and the 26.x builds require JDK 25. Older
targets use the Java release listed in `versions.json`. From the project folder,
run:

```text
gradlew build
```

To list or validate the configured targets:

```text
python3 scripts/build.py --list
python3 scripts/build.py --verify-only
```

To build all configured targets and validate their JAR metadata:

```text
python3 scripts/build.py
```

JARs are collected in `build/<target>/libs/`. A single target can be selected,
for example:

```text
python3 scripts/build.py 1.20.1-fabric
```

`versions.json` is the source of truth for the matrix. The small adapter folders
under `src/adapters/` bridge Minecraft API changes while the feature code stays
shared under `src/main/`.
