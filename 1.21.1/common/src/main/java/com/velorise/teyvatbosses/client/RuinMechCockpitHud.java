package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Titanfall-inspired Khaenri'ah Mech Cockpit HUD (compact zero-overlap layout):
 * <ul>
 *   <li><b>Embark Visor Drop & Scanline Boot:</b> Top/bottom armored shutters clamp shut and retract with a golden scanline sweep.</li>
 *   <li><b>Target Lock-On Aim Reticle:</b> Center Khaenri'ah crosshair that locks onto aimed hostiles in crimson with target name, HP bar, and distance.</li>
 *   <li><b>Cracked Visor Overlay:</b> Fractured glass cracks appear at the screen corners whenever Armor drops below 30%.</li>
 *   <li><b>Zero-Overlap Telemetry & 2x3 Skill Grid:</b> Compact bottom-left status panel and bottom-right 2x3 LMB/RMB skill grid clear of vanilla hotbar & mount hearts.</li>
 * </ul>
 */
public final class RuinMechCockpitHud {
    private static final ResourceLocation HUD_TEX = TeyvatBossesConstants.id("textures/gui/ruin_mech_hud.png");
    private static final int[] SKILL_COSTS = {12, 22, 30, 15, 18, 4};
    private static final int[] ICON_UV_IDX = {0, 1, 3, 2, 4, 5};

    private RuinMechCockpitHud() {}

    public static void render(GuiGraphics gui, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RuinMechEntity mech = RuinMechCameraController.getPilotedMech();
        if (mech == null || mc.options.hideGui || mc.level == null) return;

        int sw = gui.guiWidth();
        int sh = gui.guiHeight();
        boolean firstPerson = mc.options.getCameraType().isFirstPerson();

        RenderSystem.enableBlend();

        // 1. 1st-Person Cockpit Visor Frame, Embark Shutters & Cracked Glass
        if (firstPerson) {
            renderVisorOverlay(gui, mech, sw, sh, partialTicks);
        }

        // 2. Center Aim & Target Lock-On Reticle
        renderAimLockReticle(gui, mc, mech, sw, sh, partialTicks);

        // 3. Compact Bottom-Left Telemetry Panel (HP + Armor + 10-Cell Redstone Battery)
        //    Kept compact (124px wide at x=6) so it never touches the vanilla hotbar at any GUI scale.
        int panelX = 6;
        int panelW = 124;
        int panelH = 48;
        int panelY = sh - panelH - 6;

        gui.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xB8141110);
        drawBorder(gui, panelX, panelY, panelW, panelH, 0xCC9C7A48);

        float hpPct = Mth.clamp(mech.getHealth() / Math.max(1.0F, mech.getMaxHealth()), 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 4, panelY + 2, 0, 0, 14, 14, 256, 128);
        drawBar(gui, panelX + 20, panelY + 6, 72, 7, hpPct, 0xFFD92B3A, 0xFF361418);
        gui.drawString(mc.font, Math.round(mech.getHealth()) + "", panelX + 96, panelY + 6, 0xFFFFD8D8, true);

        float armPct = Mth.clamp(mech.getArmorIntegrity() / RuinMechEntity.MAX_ARMOR, 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 4, panelY + 17, 16, 0, 14, 14, 256, 128);
        int armColor = armPct < 0.30F ? 0xFFFF7043 : 0xFF64B5F6;
        drawBar(gui, panelX + 20, panelY + 20, 72, 7, armPct, armColor, 0xFF152536);
        gui.drawString(mc.font, Math.round(mech.getArmorIntegrity()) + "%", panelX + 96, panelY + 20, armPct < 0.30F ? 0xFFFFAB91 : 0xFFD6EEFF, true);

        float energy = mech.getEnergy();
        int fullCells = Mth.clamp((int) Math.ceil(energy / RuinMechEntity.ENERGY_PER_BLOCK), 0, RuinMechEntity.MAX_REDSTONE_CELLS);
        gui.blit(HUD_TEX, panelX + 4, panelY + 32, 32, 0, 14, 14, 256, 128);
        for (int i = 0; i < RuinMechEntity.MAX_REDSTONE_CELLS; i++) {
            int cx = panelX + 20 + i * 7;
            int cy = panelY + 34;
            boolean lit = i < fullCells;
            gui.blit(HUD_TEX, cx, cy, lit ? 48 : 56, 2, 6, 10, 256, 128);
        }
        int cellColor = fullCells <= 1 ? 0xFFFF5555 : 0xFFFFCC66;
        gui.drawString(mc.font, fullCells + "/10", panelX + 94, panelY + 35, cellColor, true);

        // 4. Compact Bottom-Right 2x3 Skill Grid (Top: L1/L2/L+, Bottom: R1/R2/R+)
        //    Only 76px wide at far right edge -> zero overlap with vanilla hotbar or mount hearts!
        int slotSize = 20;
        int gap = 3;
        int cols = 3;
        int rows = 2;
        int dockW = cols * slotSize + (cols - 1) * gap + 10;
        int dockH = rows * slotSize + (rows - 1) * gap + 14;
        int dockX = sw - dockW - 6;
        int dockY = sh - dockH - 6;
        gui.fill(dockX, dockY, dockX + dockW, dockY + dockH, 0xB8141110);
        drawBorder(gui, dockX, dockY, dockW, dockH, 0xCC9C7A48);

        int cd = mech.getActionCooldown();
        String[] keys = RuinMechInputController.HUD_BADGES;
        float lmbHold = RuinMechInputController.getLmbHoldProgress();
        float rmbHold = RuinMechInputController.getRmbHoldProgress();
        for (int i = 0; i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            int sx = dockX + 5 + col * (slotSize + gap);
            int sy = dockY + 8 + row * (slotSize + gap);
            gui.blit(HUD_TEX, sx, sy, ICON_UV_IDX[i] * 24, 24, 20, 20, 256, 128);

            boolean unavailable = (i == 5)
                    ? (cd > 0 || mech.getMissileAmmo() <= 0 || energy < RuinMechEntity.ENERGY_PER_MISSILE)
                    : (cd > 0 || energy < SKILL_COSTS[i]);
            if (unavailable) {
                gui.fill(sx, sy, sx + 20, sy + 20, 0x99000000);
            }

            float charge = (i == 2) ? lmbHold : (i == 5 ? rmbHold : 0.0F);
            if (charge > 0.01F) {
                int chH = Math.round(20 * charge);
                gui.fill(sx, sy + 20 - chH, sx + 20, sy + 20, 0x88FFB300);
            }

            if (i == 5) {
                int ammo = mech.getMissileAmmo();
                // 6-pip missile pod gauge cleanly aligned along the top border of the dock above R+
                for (int p = 0; p < RuinMechEntity.MAX_MISSILE_AMMO; p++) {
                    int px = sx + 1 + p * 3;
                    int py = dockY + 2;
                    int pipCol = (p < ammo) ? 0xFFFFAA22 : 0xAA2A2422;
                    gui.fill(px, py, px + 2, py + 4, pipCol);
                }
            }

            String lbl = keys[i];
            int lw = mc.font.width(lbl);
            int badgeColor = (i < 3) ? 0xFFFFD54F : 0xFF80DEEA;
            gui.fill(sx + 20 - lw - 2, sy + 11, sx + 20, sy + 20, 0xCC121010);
            gui.drawString(mc.font, lbl, sx + 19 - lw, sy + 12, badgeColor, false);
        }

        // 5. Sleek Thruster Charge Bar while holding Space for Supercharged Boost Leap (36 blocks)
        float spaceCharge = RuinMechInputController.getSpaceHoldProgress();
        if (spaceCharge > 0.01F) {
            int bw = 64;
            int bx = sw / 2 - bw / 2;
            int by = sh / 2 + 42;
            drawBar(gui, bx, by, bw, 5, spaceCharge, 0xFFFFB300, 0xAA181410);
        }

        RenderSystem.disableBlend();
    }

    private static void renderAimLockReticle(GuiGraphics gui, Minecraft mc, RuinMechEntity mech, int sw, int sh, float partialTicks) {
        int cx = sw / 2;
        int cy = sh / 2;

        Entity raw = mech.getLockedTargetId() != -1 ? mc.level.getEntity(mech.getLockedTargetId()) : null;
        LivingEntity locked = (raw instanceof LivingEntity le && le.isAlive()) ? le : null;
        float rmbCharge = RuinMechInputController.getRmbHoldProgress();

        if (locked != null) {
            boolean hard = mech.isHardLocked();
            float pulse = Mth.sin((mech.tickCount + partialTicks) * (hard ? 0.55F : 0.35F)) * (hard ? 1.0F : 1.6F);
            int spread = Math.round((hard ? 11.0F : 15.0F) - rmbCharge * 5.0F + pulse);
            int col = hard ? 0xFFFF1E1E : ((rmbCharge > 0.1F) ? 0xFFFF3A3A : 0xCCFF8A50);
            int len = hard ? 7 : 5;
            int t = hard ? 2 : 1;
            gui.fill(cx - spread, cy - spread, cx - spread + len, cy - spread + t, col);
            gui.fill(cx - spread, cy - spread, cx - spread + t, cy - spread + len, col);
            gui.fill(cx + spread - len, cy - spread, cx + spread, cy - spread + t, col);
            gui.fill(cx + spread - t, cy - spread, cx + spread, cy - spread + len, col);
            gui.fill(cx - spread, cy + spread - t, cx - spread + len, cy + spread, col);
            gui.fill(cx - spread, cy + spread - len, cx - spread + t, cy + spread, col);
            gui.fill(cx + spread - len, cy + spread - t, cx + spread, cy + spread, col);
            gui.fill(cx + spread - t, cy + spread - len, cx + spread, cy + spread, col);
            if (hard) {
                gui.fill(cx - 1, cy - 1, cx + 2, cy + 2, 0xFFFF2222);
            }

            int dist = Math.round((float) Math.sqrt(mech.distanceToSqr(locked)));
            String tag = (hard ? "[LOCK] " : "") + locked.getDisplayName().getString() + " [" + dist + "m]";
            int tw = mc.font.width(tag);
            int bx = cx - Math.max(44, tw / 2 + 4);
            int bw = Math.max(88, tw + 8);
            int by = cy + 20;
            gui.fill(bx, by, bx + bw, by + 17, 0xAA160E0E);
            drawBorder(gui, bx, by, bw, 17, 0xCCDD3333);
            gui.drawString(mc.font, tag, cx - tw / 2, by + 2, 0xFFFFCDD2, true);
            float tHp = Mth.clamp(locked.getHealth() / Math.max(1.0F, locked.getMaxHealth()), 0.0F, 1.0F);
            gui.fill(bx + 3, by + 12, bx + bw - 3, by + 15, 0xFF331111);
            gui.fill(bx + 3, by + 12, bx + 3 + Math.round((bw - 6) * tHp), by + 15, 0xFFFF3D3D);
        } else {
            int s = 9;
            int col = 0x88EEB84B;
            gui.fill(cx - s, cy - 2, cx - s + 1, cy + 3, col);
            gui.fill(cx + s - 1, cy - 2, cx + s, cy + 3, col);
            gui.fill(cx - 2, cy - s, cx + 3, cy - s + 1, col);
            gui.fill(cx - 2, cy + s - 1, cx + 3, cy + s, col);
        }
    }

    private static void renderVisorOverlay(GuiGraphics gui, RuinMechEntity mech, int sw, int sh, float partialTicks) {
        // Sleek top & bottom dark visor trim
        gui.fill(0, 0, sw, 8, 0x88100D0B);
        gui.fill(0, sh - 6, sw, sh, 0x88100D0B);
        gui.fill(0, 8, sw, 9, 0xAA9C7A48);

        // Khaenri'ah corner brackets (kept clear of vanilla top-right status icons & bottom panels)
        int m = 14;
        int len = 18;
        int col = 0xAAEEB84B;
        gui.fill(m, m, m + len, m + 2, col);
        gui.fill(m, m, m + 2, m + len, col);
        gui.fill(m, sh - 62, m + 2, sh - 76, col);
        gui.fill(sw - m - 2, sh - 68, sw - m, sh - 82, col);

        // Cracked visor glass fractures when Armor < 30%
        float armor = mech.getArmorIntegrity();
        if (armor < 30.0F) {
            boolean critical = armor <= 0.5F;
            int crackCol = critical ? 0xBBFFE0D0 : 0x77E8D8C8;
            drawPixelCrack(gui, sw - 2, 12, -1, 1, critical ? 52 : 34, crackCol);
            drawPixelCrack(gui, sw - 18, 12, -1, 2, critical ? 36 : 22, crackCol);
            drawPixelCrack(gui, 2, sh - 70, 1, -1, critical ? 44 : 26, crackCol);
            if (critical) {
                drawPixelCrack(gui, 2, 16, 1, 1, 38, crackCol);
            }
        }

        // Titanfall-style Embark Armored Visor Shutter Drop + Golden Scanline Boot (ANIM_ENTER_COCKPIT, 26 ticks)
        if (mech.getAnimId() == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float t = mech.getAnimTick() + partialTicks;
            if (t < 13.0F) {
                float p = Mth.clamp(t / 13.0F, 0.0F, 1.0F);
                float ease = p * p * (3.0F - 2.0F * p);
                int shutterH = Math.round((sh * 0.52F) * ease);
                gui.fill(0, 0, sw, shutterH, 0xEE14110F);
                gui.fill(0, sh - shutterH, sw, sh, 0xEE14110F);
                gui.fill(0, shutterH - 2, sw, shutterH, 0xFFEEB84B);
                gui.fill(0, sh - shutterH, sw, sh - shutterH + 2, 0xFFEEB84B);
            } else if (t < 26.0F) {
                float p = Mth.clamp((t - 13.0F) / 13.0F, 0.0F, 1.0F);
                float openEase = 1.0F - (float) Math.pow(1.0F - p, 3.0F);
                int shutterH = Math.round((sh * 0.52F) * (1.0F - openEase));
                if (shutterH > 8) {
                    gui.fill(0, 0, sw, shutterH, 0xDD14110F);
                    gui.fill(0, sh - shutterH, sw, sh, 0xDD14110F);
                }
                int scanY = Math.round(sh * p);
                int glowAlpha = Math.round((1.0F - p) * 140.0F);
                gui.fill(0, Math.max(0, scanY - 3), sw, scanY + 3, (glowAlpha << 24) | 0xFFBB33);
                gui.fill(0, scanY, sw, scanY + 1, 0xEEFFF0AA);
            }
        }

        // Low-energy warning pulse border when below 1 Redstone Block (<100 energy)
        if (mech.getEnergy() < RuinMechEntity.ENERGY_PER_BLOCK) {
            float pulse = 0.5F + 0.5F * Mth.sin((mech.tickCount + partialTicks) * 0.25F);
            int a = (int) (35 + pulse * 65);
            int red = (a << 24) | 0xDD1A1A;
            gui.fill(0, 0, sw, 4, red);
            gui.fill(0, sh - 4, sw, sh, red);
            gui.fill(0, 0, 4, sh, red);
            gui.fill(sw - 4, 0, sw, sh, red);
        }
    }

    private static void drawPixelCrack(GuiGraphics gui, int x0, int y0, int dx, int dy, int steps, int color) {
        int x = x0;
        int y = y0;
        for (int i = 0; i < steps; i++) {
            gui.fill(x, y, x + 2, y + 1, color);
            x += dx;
            if (i % 3 == 0) y += dy;
            if (i % 7 == 4) x += dx;
            if (i == steps / 2) {
                int bx = x, by = y;
                for (int b = 0; b < steps / 3; b++) {
                    gui.fill(bx, by, bx + 1, by + 1, color);
                    bx += dx;
                    by -= dy;
                }
            }
        }
    }

    private static void drawBar(GuiGraphics gui, int x, int y, int w, int h, float pct, int fg, int bg) {
        gui.fill(x, y, x + w, y + h, bg);
        int fillW = Math.round(w * pct);
        if (fillW > 0) {
            gui.fill(x, y, x + fillW, y + h, fg);
            gui.fill(x, y, x + fillW, y + 2, 0x44FFFFFF);
        }
        drawBorder(gui, x - 1, y - 1, w + 2, h + 2, 0xCC241C16);
    }

    private static void drawBorder(GuiGraphics gui, int x, int y, int w, int h, int color) {
        gui.fill(x, y, x + w, y + 1, color);
        gui.fill(x, y + h - 1, x + w, y + h, color);
        gui.fill(x, y, x + 1, y + h, color);
        gui.fill(x + w - 1, y, x + w, y + h, color);
    }
}
