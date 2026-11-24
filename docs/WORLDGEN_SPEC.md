# Velorise's Teyvat — 3D World Generation Specification (`veloriseteyvat:teyvat`)

## 1. Identifiers & Multi-Loader Layout

- **Dimension ID**: `veloriseteyvat:teyvat`
- **Dimension Type ID**: `veloriseteyvat:teyvat`
- **Custom Chunk Generator ID**: `veloriseteyvat:teyvat_generator`
- **Custom Biome Source ID**: `veloriseteyvat:teyvat_biome_source`
- **Custom Density Function ID**: `veloriseteyvat:teyvat_density`
- **Shared Data Directory**: `shared-resources/data/veloriseteyvat/` (automatically packaged into `1.21.1/neoforge`, `1.21.1/fabric`, and `1.20.1/forge`)
- **Java Common Package**: `com.velorise.teyvat.worldgen` (`1.21.1/common/src/main/java/com/velorise/teyvat/worldgen/`)

## 2. World Bounds & Vertical Y-Axis Budget

The Teyvat dimension is bounded to a playable **`4096 × 4096` block** continent (`X, Z ∈ [-2048, +2048]`, `256 × 256` chunks) surrounded by the **Dark Sea** (`Y = 30` seabed, `Y = 63` ocean surface).

Vertical height is extended to **`512` blocks** (`min_y = -128`, `height = 512`, `Y ∈ [-128, 383]`):

| Y Range | Stratum | Primary Teyvat Usage |
| :--- | :--- | :--- |
| `-128 .. 0` | Underground 3D Layer | The Chasm Underground Mines, Sumeru desert/rainforest caves, Dragonspine sub-caverns |
| `1 .. 63` | Standard Ocean & Seabed | Main sea level (`Y = 63`), Cider Lake (`Y = 50..63`), Inazuma archipelago waters |
| `64 .. 145` | Surface Terrain & Cliffs | Mondstadt rolling hills, Liyue plains, Sumeru valleys, terraced cliffs (`smoothstep` shelves for 3D Wall Climbing) |
| `146 .. 175` | Elevated Plateau Sea | Fontaine elevated plateau sea (`Y = 150`) powered by regional `Multi-Aquifer` |
| `176 .. 280` | Alpine Peaks & Karst Pillars | Dragonspine summit (~`Y = 235`), Jueyun Karst pillars, Natlan volcano |
| `281 .. 383` | Sky Stratum | Celestia floating island, Jade Chamber, Skyfrost Nail spire |

## 3. Bounded 7-Nation Geography & Unlock Progression

All 7 nations exist inside the fixed `4096 × 4096` macro-map so border terrain generates seamlessly without chunk walls. Progression is controlled by `RegionBarrierManager` (`SavedData`), with **only Mondstadt unlocked by default** in `v1.0: Mondstadt Prologue`:

| Region ID | Name | Default State | Approximate Bounds `(X, Z)` |
| :--- | :--- | :--- | :--- |
| `0` | `DARK_SEA` | World Border | Outside `[-2048, 2048]` or outer ocean |
| `1` | `MONDSTADT` | **Unlocked (`v1.0`)** | `X: +120 .. +1620`, `Z: -1350 .. +120` |
| `2` | `DRAGONSPINE` | Locked (border visible from Mondstadt) | `X: +180 .. +880`, `Z: +120 .. +720` |
| `3` | `LIYUE` | Locked (`v1.1`) | `X: -620 .. +420`, `Z: +520 .. +1650` |
| `4` | `INAZUMA` | Locked (`v1.2`) | `X: +920 .. +1860`, `Z: +920 .. +1860` |
| `5` | `SUMERU` | Locked | `X: -1680 .. -620`, `Z: +120 .. +1580` |
| `6` | `FONTAINE` | Locked | `X: -1450 .. -220`, `Z: -1150 .. +120` |
| `7` | `NATLAN` | Locked | `X: -1950 .. -1350`, `Z: +180 .. +1350` |
| `8` | `SNEZHNAYA` | Locked | `X: -1850 .. +250`, `Z: -1920 .. -1150` |

## 4. Mondstadt (`v1.0`) Canonical Landmarks, Spawn & Boss Pads

All major landmarks and boss arenas have deterministic `(X, Z)` coordinates, target surface `Y`, and flat `pad_radius` baked into `teyvat_macro.bin` (`Sculpt3DMask = 0`) so arenas never float or clip:

| ID | Landmark / Boss | Sub-Area | `(X, Y, Z)` | Pad Radius | Reference |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `spawn_starfell_beach` | **Canon Player Spawn** | Starfell Beach (NE Coast) | `(1340, 65, -720)` | `18` | Canon opening overlooking Starfell Lake & Mondstadt |
| `starfell_lake` | Starfell Lake (Statue of The Seven) | Starfell Valley | `(1140, 68, -680)` | `20` | [Mondstadt Locations](https://genshin-impact.fandom.com/wiki/Category:Mondstadt_Locations) |
| `mondstadt_city` | Mondstadt City Island | Cider Lake | `(720, 78, -620)` | `110` | [Mondstadt City](https://genshin-impact.fandom.com/wiki/Mondstadt_City) |
| `windrise_oak` | Windrise Great Oak | Galesong Hill / Windwail | `(960, 74, -290)` | `36` | [Windrise](https://genshin-impact.fandom.com/wiki/Windrise) |
| `boss_cryo_regisvine` | **Cryo Regisvine** | Falcon Coast / Starfell Ice Cavern | `(1180, 56, -420)` | `28` | [Cryo Regisvine](https://genshinimpact.wiki.fextralife.com/Cryo_Regisvine) |
| `boss_anemo_hypostasis` | **Anemo Hypostasis** | Stormbearer Mountains | `(1240, 118, -980)` | `26` | [Anemo Hypostasis](https://genshin-impact.fandom.com/wiki/Anemo_Hypostasis) |
| `boss_electro_hypostasis` | **Electro Hypostasis** | Cape Oath | `(1360, 86, -80)` | `26` | [Electro Hypostasis](https://genshin-impact.fandom.com/wiki/Electro_Hypostasis) |
| `boss_cryo_hypostasis` | **Cryo Hypostasis** | Dragonspine Foothills (North) | `(640, 112, 220)` | `26` | [Cryo Hypostasis](https://genshin-impact.fandom.com/wiki/Cryo_Hypostasis) |
| `boss_andrius` | **Andrius (Weekly Boss)** | Wolvendom Arena | `(420, 96, -340)` | `34` | [Andrius](https://genshinimpact.wiki.fextralife.com/Andrius) |
| `boss_dvalin` | **Dvalin / Stormterror (Weekly Boss)** | Stormterror's Lair Spire | `(260, 112, -1040)` | `44` | [Mondstadt Geography](https://en.wikipedia.org/wiki/Mondstadt) |

## 5. Binary Format: `teyvat_macro.bin` (`TVM1`)

Stored at `shared-resources/data/veloriseteyvat/worldgen/teyvat_macro.bin`.

- **Header (24 bytes, big-endian)**:
  - `char[4]` magic = `"TVM1"`
  - `int32` width (`512`)
  - `int32` height (`512`)
  - `int32` worldMinX (`-2048`)
  - `int32` worldMinZ (`-2048`)
  - `int32` blocksPerPixel (`8`)
- **Payload**: Zlib-compressed `width * height * 4` bytes (`RGBA` per pixel):
  - **R (`0..255`)**: `TargetElevation` mapped linearly to `Y ∈ [24, 279]` (`targetY = 24 + R`)
  - **G (`0..255`)**: Packed Region + SubBiome byte: `regionId = (G >> 4) & 0x0F`, `subBiomeId = G & 0x0F`
  - **B (`0..255`)**: `Sculpt3DMask`: `0` = flat Structure Pad (zero 3D warp/cave), `1..127` = Cave density weight, `128..255` = 3D Overhang / Cliff warp intensity
  - **A (`0..255`)**: `WaterRoadMask`: `0..63` = river/lake carve depth, `64..191` = normal terrain, `192..255` = elevated Fontaine water mask (`Y = 150`)

## 6. External Reference Sources

- 3D Coordinate Point Cloud: [Sam5440/Genshin_Impact_Teleport](https://github.com/Sam5440/Genshin_Impact_Teleport)
- Official Multi-Layered Map: [HoYoLAB Teyvat Interactive Map](https://act.hoyolab.com/ys/app/interactive-map/index.html)
- Open-Source Community Map: [Kongying Tavern (`genshin-map-v5`)](https://github.com/kongying-tavern/genshin-map-v5)
- High-Res Orthographic & Altitude Charts: [Teyvat Map Institute Satellite v7.2](https://www.reddit.com/r/Genshin_Impact/comments/1xqm6ip/satellite_map_of_teyvat_ver_72_fixed_teyvat_map/), [Teyvat Altitude Chart](https://www.reddit.com/r/Genshin_Impact/comments/1ftri2y/teyvat_altitude_chart_colorcoded_side_view_teyvat/), [Teyvat Sideview with Heights v5.8](https://www.reddit.com/r/Genshin_Impact/comments/1mh4ity/teyvat_sideview_with_heights_ver_58_teyvat_map/)
