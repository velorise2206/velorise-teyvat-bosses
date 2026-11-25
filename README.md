# Velorise: Teyvat Bosses

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%20%7C%201.20.1-brightgreen.svg)](https://minecraft.net/)
[![Loaders](https://img.shields.io/badge/Modloader-NeoForge%20%7C%20Forge%20%7C%20Fabric-orange.svg)](https://github.com/velorise2206)
[![Addon For](https://img.shields.io/badge/Addon%20For-Velorise's%20Staves-8b5cf6.svg)](https://github.com/velorise2206/velorise-staves)

The official **Endgame Boss Expansion Addon** for **[Velorise's Staves](https://github.com/velorise2206/velorise-staves)** across **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1**.

Built directly on top of **`VeloriseElementAPI`** (Elemental Gauge Theory, Elemental Shield Gauges, and Innate Auras) and tuned to pair seamlessly with **`Camera Lock-On`** and **`Velorise Simple Map`**, **Velorise: Teyvat Bosses** brings action-focused boss encounters, biome-matched Overworld arenas, and high-value endgame progression without inventory bloat.

---

## Core Design — Quality-First Boss Combat

Instead of flooding the world with dozens of static mobs or hundreds of throwaway drops, every encounter in **Velorise: Teyvat Bosses** is engineered around **deliberate, telegraphed boss combat**:

- **Weighted, Telegraphed Animations**: Clear windup cues ➔ tick-accurate `attackTicks` hitboxes and particles ➔ deliberate recovery windows.
- **Tactile Game Feel**: Limb/weapon-anchored VFX, **Screen Shake**, **Emissive Glow** layers, and custom sound design.
- **True Elemental Mechanics**: Bosses leverage `VeloriseElementAPI` for **Elemental Shield Gauges**, **Innate Auras**, and full **EGT Reactions** — rewarding counter-element setups over brute-force sword swinging.

---

## Overworld Arenas & Encounter Flow

Bosses integrate directly into your **Overworld** exploration rather than locking encounters behind a separate custom dimension:

- **Biome-Matched Overworld Boss Arenas**: Dedicated boss arenas and regional encounters generate naturally across fitting Overworld biomes.
- **Dormant State**: Bosses rest peacefully inside their arenas (`dormant_sitting`, `dormant_slumber`) until approached or provoked.
- **Ley Line Pedestal (In-Place Re-Summon)**: Defeated bosses never vanish permanently from a multiplayer world — an indestructible central altar respawns the boss over time or lets players re-summon it on demand using Elemental Essences / Resin.
- **Per-Player Trounce Blossom**: Defeating a boss spawns a central **Trounce Blossom** granting instanced loot per participating player, preventing loot-stealing or drops falling off ledges.

---

## Roadmap

### `v1.0: Mondstadt Prologue` (Planned Initial Release)

| Tier | Roster | Biome / Setting | Core Mechanics & Role |
| :--- | :--- | :--- | :--- |
| **Elite Mobs** | Hilichurl, Mitachurl, Lawachurl, Ruin Guard | Plains, Forests & Ancient Ruin Outposts | Regional exploration threats; Mitachurl shields demand Pyro/Reaction counters; Ruin Guards fire homing missiles and spin attacks. |
| **World Bosses** | **Cryo Regisvine**, **Anemo Hypostasis**, **Electro Hypostasis**, **Cryo Hypostasis** | Ice Caverns, Windswept Peaks, Plains & Snowy Slopes | Core EGT mastery checks featuring Elemental Cores/Shields; primary source of Essences and early-tier Boss Relics. |
| **Weekly Bosses** | **Andrius** *(Dominator of Wolves)* & **Dvalin** *(Stormterror)* | Frostbound Wolf Arena & Stormspire Ruin | Flagship `v1.0` endgame encounters dropping Trounce materials to awaken Mondstadt Archon Weapons. |

*(Subsequent updates expand nation by nation: `v1.1 Liyue` — Primo Geovishap, Azhdaha, Childe; `v1.2 Inazuma` — Maguu Kenki, Raiden Mech, and beyond.)*

---

## Anti-Bloat Endgame Rewards

Every drop has a clear progression purpose:

1. **Trounce Refinement Materials** to awaken and empower Archon Weapons from `Velorise's Staves`.
2. **Boss Relics** grouped by Boss family with distinct passive effects (no RNG artifact stat bloat).
3. **Elemental Essences** in bulk & high-tier **`Elemental Mastery IV–V`** enchantment books.
4. **National Wind Glider Skins** & **3D Miniature Boss Trophy Blocks**.

---

## Technical Architecture

### Model & Animation Pipeline
- **Native Mojang (`HierarchicalModel` + `AnimationDefinition`)** *(Primary for Elite Mobs & World Bosses)*: Zero external rendering dependencies, native OpenGL pipeline, and direct per-frame bone coordinate tracking for weapon-anchored particles and hitboxes.
- **GeckoLib / AzureLib Runtime** *(Optional / Mega Weekly Bosses)*: Used for ultra-high-poly multi-segment bosses such as **Dvalin** or **Azhdaha** where data-driven `.geo.json` / `.animation.json` avoids Java's 64KB method bytecode ceiling.

### Building from Source

Supports **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1**:

```powershell
# Build all loaders
cmd.exe /c "set NONINTERACTIVE=1 && build-all.bat"
```

---

## Recommended Companion Mods

Enhance your boss-hunting experience with companion mods by **velorise2206**:

- **[Velorise's Staves](https://github.com/velorise2206/velorise-staves)**: Core elemental weapons, EGT reaction engine, 3D Wall Climbing, Wind Glider, and Plunging Attacks *(Required Core)*.
- **[Camera Lock-On](https://github.com/velorise2206/camera-lock-on)**: Zelda/Souls-style target locking — ideal for strafing swift bosses and aiming spells cleanly.
- **[Velorise Simple Map](https://github.com/velorise2206/Simple-Map)**: Clean, lightweight minimap and world map for locating Overworld boss arenas.

---

## Project Structure

```text
velorise_teyvat_bosses/
├── 1.20.1/            # Forge 1.20.1 module
├── 1.21.1/            # Common + NeoForge + Fabric 1.21.1 modules
├── models/            # Source Blockbench / geometry files (.bbmodel / .geo.json / .java)
├── textures/          # Diffuse PNGs and Emissive glow maps
├── animations/        # Animation JSON / Java AnimationDefinitions
├── docs/              # Boss stats, arena worldgen & AI documentation
├── shared-resources/  # Cross-loader shared assets & data
└── README.md
```

---

## License & Disclaimer

Copyright (c) 2026 **velorise2206** — **MIT (Code & Documentation Only)**.

- *Velorise: Teyvat Bosses* is an **unofficial, non-commercial fan-made mod** inspired by *Genshin Impact* and is **not affiliated with, endorsed by, or associated with HoYoverse / Cognosphere Pte. Ltd.** (or Mojang / Microsoft). All *Genshin Impact* trademarks, names, and related IP belong to **HoYoverse / Cognosphere Pte. Ltd.**
