# Velorise: Teyvat Bosses

[![License: MIT](https://img.shields.io/badge/License-MIT-red.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%20%7C%201.20.1-brightgreen.svg)](https://minecraft.net/)
[![Loaders](https://img.shields.io/badge/Modloader-NeoForge%20%7C%20Fabric%20%7C%20Forge-orange.svg)](https://github.com/velorise2206)
[![Addon For](https://img.shields.io/badge/Addon%20For-Velorise's%20Staves-8b5cf6.svg)](https://github.com/velorise2206/velorise-staves)

An action-focused Overworld boss & mecha expansion mod across **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1**. **Velorise: Teyvat Bosses** works standalone or pairs with **[Velorise's Staves](https://github.com/velorise2206/velorise-staves)** via `StavesElementBridge` for elemental reactions, innate auras, and shield counters.

---

## Implemented Bosses (`1.21.1`)


| Boss                               | Entity ID                                 | Arena Structure                                                    | Key Mechanics & Drops                                                                                                                                                                                                               |
| :----------------------------------- | :------------------------------------------ | :------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Cryo Regisvine**                 | `velorise_teyvat_bosses:cryo_regisvine`   | `cryo_regisvine_arena` *(underground ice cavern + surface tunnel)* | Root & corolla weakpoint breaks, frost darts, ice pillars; drops Cryo Trophy & *Frostbound Bloom* Music Disc.                                                                                                                       |
| **Anemo Hypostasis**               | `velorise_teyvat_bosses:anemo_hypostasis` | `anemo_hypostasis_arena`                                           | Innate Anemo aura (Swirl-reactive), tornadoes, wind currents, Wind Crystal revival phase; drops Anemo Trophy & *Gale of Beth* Music Disc.                                                                                           |
| **The Open-Eyed**                  | `velorise_teyvat_bosses:the_open_eyed`    | `the_open_eyed_arena`                                              | Dormant crimson-star ambush, Elemental Stars, Supreme Overseer & Phenomena's Delirium Sky Spears; drops The Open-Eyed Trophy & *Vigil of the Unblinking* Music Disc.                                                                |
| **Ruin Guard**                     | `velorise_teyvat_bosses:ruin_guard`       | `thousand_winds_temple_arena`                                      | Physical boss (mutation-immune), eye-core stun, 6-missile barrage, 3-hit alternating-arm combo, cyclone spin, Phase 2 Overdrive; drops Ruin Mech salvage parts, Ruin Guard Trophy & *Iron Sentinel of Thousand Winds* Music Disc.   |
| **Astralion: Prismatic Sovereign** | `velorise_teyvat_bosses:astralion`        | Spawn Egg / Summon                                                 | 7-element rotating Omni-boss across 3 forms (**Monolith**, **Leviathan**, **Cosmic Singularity**), 100-pt Prismatic Shield counter-break, Sevenfold Cataclysm; drops Astralion Trophy & *Sovereign of the Seven Prisms* Music Disc. |

---

## Pilotable Ruin Mech (`velorise_teyvat_bosses:ruin_mech`)

Assemble your own ancient war machine outdoors from 5 salvage parts dropped by **Ruin Guard**:

1. **Outdoor Assembly**: Right-click the ground with `ruin_mech_core` to deploy the chassis, then weld `ruin_mech_left_arm`, `ruin_mech_right_arm`, `ruin_mech_left_leg`, and `ruin_mech_right_leg` directly onto it.
2. **Maintenance & Power**:
   - **Hull & Armor Repair**: Right-click with **Iron Ingot** (`+40 HP / +15% Armor`) or **Iron Block** (`+180 HP / +60% Armor`).
   - **Reactor Recharge**: Right-click with **Redstone Block** to trigger the rear-hatch animation and restore `+100` energy (up to `1000` max / 10 HUD cells).
3. **Cockpit HUD, Camera & Missile Magazine**:
   - 1st-person Khaenri'ah visor overlay with startup seal animation, armor-crack damage states, 2×3 skill grid, 10-cell energy bar, and a **6-missile magazine** recharged by landing melee hits.
   - 3rd-person detached camera (`7.2` blocks) with stride-synced walk bob/roll and impact shudder.
   - **Controls**:
     - `LMB` **Tap (`L1`)**: Double Clap | **Double-Tap (`L2`)**: 3-Hit Combo | **Hold (`L+`)**: Cyclone Spin
     - `RMB` **Tap (`R1`)**: Stomp Shockwave | **Double-Tap (`R2`)**: Leap Slam / Terrain Leap | **Hold (`R+`)**: Fire Loaded Homing Missiles
     - `Space` **Tap**: Boost Leap (`~14` blocks) | **Hold (`0.45s`)**: Super Boost Leap (`~36` blocks)
     - `MMB` (Middle Mouse): Toggle Hard Lock-On (`[LOCK]` radar)

---

## Overworld Arenas, Rewards & Original Audio

- **Natural-Blended Overworld Arenas**: 4 structure arenas (`anemo_hypostasis_arena`, `cryo_regisvine_arena`, `the_open_eyed_arena`, `thousand_winds_temple_arena`) with terrain-hugging perimeter blending and safe `/locate` surface coordinates.
- **Trounce Blossom & Respawn Anchor**: Defeated bosses spawn a **Trounce Blossom** (`trounce_blossom`) for reward claims, followed by a **Ley Line Respawn Blossom** (`boss_respawn_anchor`) for in-place re-summoning.
- **5 Collectible 3D Boss Trophies**: `cryo_regisvine_trophy`, `anemo_hypostasis_trophy`, `the_open_eyed_trophy`, `ruin_guard_trophy`, and `astralion_trophy` rendered from the bosses' actual 3D models (right-click placed trophies to toggle static statue vs. animated hologram mode).
- **Original Boss Themes & Mech SFX**: 5 original `.ogg` boss themes playable in-combat and via Jukebox Music Discs (`music_disc_*`), plus synthesized mechanical servo/joint/clank/reactor/radar SFX.

---

## Building from Source

Supports **NeoForge 1.21.1**, **Fabric 1.21.1**, and **Forge 1.20.1** (uses JDK 17 for `1.20.1` and JDK 21 for `1.21.1`):

```powershell
cmd.exe /c "set NONINTERACTIVE=1 && build-all.bat"
```

---

## Project Structure

```text
velorise_teyvat_bosses/
├── 1.20.1/            # Forge 1.20.1 module
├── 1.21.1/            # Common + NeoForge + Fabric 1.21.1 modules
├── models/            # Blockbench .bbmodel source files
├── shared-resources/  # Shared assets, sounds, textures, structures, loot tables & tags
├── build-all.bat      # Multi-loader build script
└── README.md
```

---

## License & Disclaimer

Copyright (c) 2026 **velorise2206** — **MIT (Code & Documentation Only)**.

- *Velorise: Teyvat Bosses* is an **unofficial, non-commercial fan-made mod** inspired by *Genshin Impact* and is **not affiliated with, endorsed by, or associated with HoYoverse / Cognosphere Pte. Ltd.** (or Mojang / Microsoft). All *Genshin Impact* trademarks, names, and related IP belong to **HoYoverse / Cognosphere Pte. Ltd.**
