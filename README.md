# Velorise's Teyvat

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%20%7C%201.20.1-brightgreen.svg)](https://minecraft.net/)
[![Loaders](https://img.shields.io/badge/Modloader-NeoForge%20%7C%20Forge%20%7C%20Fabric-orange.svg)](https://github.com/velorise2206)
[![Addon For](https://img.shields.io/badge/Addon%20For-Velorise's%20Staves-8b5cf6.svg)](https://github.com/velorise2206/velorise-staves)

The official **World & Endgame Expansion Addon** for **[Velorise's Staves](https://github.com/velorise2206/velorise-staves)** across **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1**.

Built directly on top of **`VeloriseElementAPI`** (Elemental Gauge Theory, Elemental Shield Gauges, and Innate Auras) and tuned to pair seamlessly with **`Camera Lock-On`** and **`Velorise Simple Map`**, **Velorise's Teyvat** introduces a dedicated **Teyvat Dimension** featuring faithful regional geography, telegraphed Boss encounters, and high-value endgame progression without cluttering your inventory.

---

## The Teyvat Dimension (`teyvat:teyvat`)

Rather than scattering massive arenas randomly across the Vanilla Overworld, Teyvat exists as its own cohesive dimension with a deliberate exploration flow:

- **Celestia Portal**: Step through the ancient Celestia Gateway to enter the **Teyvat Dimension**.
- **Canon Opening — Starfell Beach**: Upon arrival, players descend onto the iconic coastal shores of Mondstadt, recreating the opening atmosphere of *Genshin Impact* with clear sightlines and landmarks leading toward **Starfell Lake**, **Windrise**, **Wolvendom**, and **Stormterror's Lair**.
- **Genshin-Inspired World Generation**:
  - **Deterministic Macro-Region Layout**: Fixed regional placement via a custom `BiomeSource` with domain-warped borders so nations transition naturally along rivers and valleys instead of random Overworld biome chaos.
  - **Terraced Cliffs & Rolling Hills**: Combines broad, low-frequency rolling plains with stepped terrace functions (`smoothstep` elevation shelves) designed specifically to shine alongside **Velorise's Staves** **3D Wall Climbing** and **Wind Glider**.
  - **Heightmap-Blended Boss Arenas**: Key landmarks and boss arenas sit at deterministic regional coordinates with smooth terrain blending so arenas integrate naturally into cliffsides and coastlines without floating or awkward clipping.

---

## Roadmap — Quality-First Boss Design

Instead of flooding the game with dozens of static, basic-damage mobs or hundreds of useless drops, every encounter in **Velorise's Teyvat** is engineered around **action-focused Boss combat**:

- **Weighted, Telegraphed Animations**: Clear windup cues ➔ tick-accurate `attackTicks` hitboxes and particles ➔ deliberate recovery windows.
- **Tactile Game Feel**: Limb/weapon-anchored VFX, **Screen Shake**, **Emissive Glow** layers, and custom sound design.
- **True Elemental Mechanics**: Bosses leverage `VeloriseElementAPI` for **Elemental Shield Gauges**, **Innate Auras**, and full **EGT Reactions** — rewarding counter-element setups over brute-force sword swinging.

### `v1.0: Mondstadt Prologue` (Planned Initial Release)

| Tier | Roster | Canon Location | Core Mechanics & Role |
| :--- | :--- | :--- | :--- |
| **Elite Mobs** | Hilichurl, Mitachurl, Lawachurl, Ruin Guard | Roadside Camps, Ancient Ruins & Stormterror's Lair | Regional exploration threats; Mitachurl shields demand Pyro/Reaction counters; Ruin Guards fire homing missiles and spin attacks. |
| **World Bosses** | **Cryo Regisvine**, **Anemo Hypostasis**, **Electro Hypostasis**, **Cryo Hypostasis** | Starfell Ice Cavern, Stormbearer Mountains, Cape Oath, Dragonspine | Core EGT mastery checks featuring Elemental Cores/Shields; primary source of Essences and early-tier Boss Relics. |
| **Weekly Bosses** | **Andrius** *(Dominator of Wolves)* & **Dvalin** *(Stormterror)* | Wolvendom Arena & Stormterror's Lair Spire | Flagship `v1.0` endgame encounters dropping Trounce materials to awaken Mondstadt Archon Weapons. |

*(Subsequent updates expand nation by nation: `v1.1 Liyue` — Primo Geovishap, Azhdaha, Childe; `v1.2 Inazuma` — Maguu Kenki, Raiden Mech, and beyond.)*

---

## Respawn Loop & Anti-Bloat Rewards

- **Dormant State**: Bosses rest peacefully inside their arenas (`dormant_sitting`, `dormant_slumber`) until approached or provoked.
- **Ley Line Pedestal (In-Place Re-Summon)**: Defeated bosses never vanish permanently from a multiplayer world — an indestructible altar at the arena center respawns the boss over time or allows instant re-summoning using Elemental Essences / Resin.
- **Per-Player Trounce Blossom**: Defeating a boss spawns a central **Trounce Blossom** granting instanced loot per participating player, preventing loot-stealing or drops falling off ledges.
- **High-Value, Zero-Bloat Drops**:
  1. **Trounce Refinement Materials** to awaken and empower Archon Weapons from `Velorise's Staves`.
  2. **Boss Relics** grouped by Boss family with distinct passive effects (no RNG artifact stat bloat).
  3. **Elemental Essences** in bulk & high-tier **`Elemental Mastery IV–V`** enchantment books.
  4. **National Wind Glider Skins** & **3D Miniature Boss Trophy Blocks**.

---

## Technical Architecture

### Model & Animation Pipeline
- **Native Mojang (`HierarchicalModel` + `AnimationDefinition`)** *(Primary for Elite Mobs & World Bosses)*: Zero external rendering dependencies, native OpenGL pipeline, and direct per-frame bone coordinate tracking for weapon-anchored particles and hitboxes.
- **GeckoLib / AzureLib Runtime** *(Optional / Mega Weekly Bosses)*: Used for ultra-high-poly (~2,500-cube) multi-segment bosses such as **Dvalin** or **Azhdaha** where data-driven `.geo.json` / `.animation.json` avoids Java's 64KB method bytecode ceiling.

### Building from Source

Supports **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1**:

```powershell
# Build all loaders
cmd.exe /c "set NONINTERACTIVE=1 && build-all.bat"
```

---

## Recommended Companion Mods

Enhance your Teyvat adventure with companion mods by **velorise2206**:

- **[Velorise's Staves](https://github.com/velorise2206/velorise-staves)**: Core elemental weapons, EGT reaction engine, 3D Wall Climbing, Wind Glider, and Plunging Attacks *(Required Core)*.
- **[Camera Lock-On](https://github.com/velorise2206/camera-lock-on)**: Zelda/Souls-style target locking — ideal for strafing swift bosses and aiming spells cleanly.
- **[Velorise Simple Map](https://github.com/velorise2206/Simple-Map)**: Clean, lightweight minimap and world map for navigating Teyvat's regions and marking boss arenas.

---

## Project Structure

```text
velorise_teyvat/
├── 1.20.1/            # Forge 1.20.1 module
├── 1.21.1/            # Common + NeoForge + Fabric 1.21.1 modules
├── models/            # Source Blockbench / geometry files (.bbmodel / .geo.json / .java)
├── textures/          # Diffuse PNGs and Emissive glow maps
├── animations/        # Animation JSON / Java AnimationDefinitions
├── docs/              # Worldgen specifications, boss stats & AI documentation
├── shared-resources/  # Cross-loader shared assets & data
└── README.md
```

---

## License & Disclaimer

Copyright (c) 2026 **velorise2206** — **MIT (Code & Documentation Only)**.

- *Velorise's Teyvat* is an **unofficial, non-commercial fan-made mod** inspired by *Genshin Impact* and is **not affiliated with, endorsed by, or associated with HoYoverse / Cognosphere Pte. Ltd.** (or Mojang / Microsoft). All *Genshin Impact* trademarks, names, and related IP belong to **HoYoverse / Cognosphere Pte. Ltd.**
