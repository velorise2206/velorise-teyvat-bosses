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
 * Titanfall-inspired Khaenri'ah Mech Cockpit HUD:
 * <ul>
 *   <li><b>Embark Visor Drop & Scanline Boot:</b> Top/bottom armored shutters clamp shut and retract with a golden scanline sweep.</li>
 *   <li><b>Target Lock-On Aim Reticle:</b> Center Khaenri'ah crosshair that locks onto aimed hostiles in crimson with target name, HP bar, and distance.</li>
 *   <li><b>Cracked Visor Overlay:</b> Fractured glass cracks appear at the screen corners whenever Armor drops below 30%.</li>
 *   <li><b>VIP Pixel Telemetry & 6-Skill LMB/RMB Dock:</b> Crisp HP, Armor, 10-Cell Redstone Battery, and L1/L2/L+/R1/R2/R+ gesture icons.</li>
 * </ul>
 */
public final class RuinMechCockpitHud {
    private static final ResourceLocation HUD_TEX = TeyvatBossesConstants.id("textures/gui/ruin_mech_hud.png");
    private static final int[] SKILL_COSTS = {12, 22, 30, 15, 18, 25};
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

        // 2. Center Aim & Target Lock-On Reticle (shows WHO you are aiming at!)
        renderAimLockReticle(gui, mc, mech, sw, sh, partialTicks);

        // 3. Bottom-Left Telemetry Panel (HP + Armor + 10-Cell Redstone Battery)
        int panelX = 12;
        int panelY = sh - 66;
        int panelW = 148;
        int panelH = 54;

        gui.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xB8141110);
        drawBorder(gui, panelX, panelY, panelW, panelH, 0xCC9C7A48);

        float hpPct = Mth.clamp(mech.getHealth() / Math.max(1.0F, mech.getMaxHealth()), 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 5, panelY + 4, 0, 0, 16, 16, 256, 128);
        drawBar(gui, panelX + 24, panelY + 8, 86, 8, hpPct, 0xFFD92B3A, 0xFF361418);
        gui.drawString(mc.font, Math.round(mech.getHealth()) + "", panelX + 114, panelY + 8, 0xFFFFD8D8, true);

        float armPct = Mth.clamp(mech.getArmorIntegrity() / RuinMechEntity.MAX_ARMOR, 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 5, panelY + 20, 16, 0, 16, 16, 256, 128);
        int armColor = armPct < 0.30F ? 0xFFFF7043 : 0xFF64B5F6;
        drawBar(gui, panelX + 24, panelY + 24, 86, 8, armPct, armColor, 0xFF152536);
        gui.drawString(mc.font, Math.round(mech.getArmorIntegrity()) + "%", panelX + 114, panelY + 24, armPct < 0.30F ? 0xFFFFAB91 : 0xFFD6EEFF, true);

        float energy = mech.getEnergy();
        int fullCells = Mth.clamp((int) Math.ceil(energy / RuinMechEntity.ENERGY_PER_BLOCK), 0, RuinMechEntity.MAX_REDSTONE_CELLS);
        gui.blit(HUD_TEX, panelX + 5, panelY + 36, 32, 0, 16, 16, 256, 128);
        for (int i = 0; i < RuinMechEntity.MAX_REDSTONE_CELLS; i++) {
            int cx = panelX + 24 + i * 9;
            int cy = panelY + 38;
            boolean lit = i < fullCells;
            gui.blit(HUD_TEX, cx, cy, lit ? 48 : 56, 2, 7, 11, 256, 128);
        }
        int cellColor = fullCells <= 1 ? 0xFFFF5555 : 0xFFFFCC66;
        gui.drawString(mc.font, fullCells + "/10", panelX + 116, panelY + 40, cellColor, true);

        // 4. Bottom-Right 6-Skill Pixel Icon Dock (L1/L2/L+ | R1/R2/R+)
        int slotSize = 22;
        int gap = 4;
        int dockW = 6 * slotSize + 5 * gap + 10;
        int dockX = sw - dockW - 12;
        int dockY = sh - 40;
        gui.fill(dockX, dockY, dockX + dockW, dockY + 32, 0xB8141110);
        drawBorder(gui, dockX, dockY, dockW, 32, 0xCC9C7A48);

        int cd = mech.getActionCooldown();
        String[] keys = RuinMechInputController.HUD_BADGES;
        float lmbHold = RuinMechInputController.getLmbHoldProgress();
        float rmbHold = RuinMechInputController.getRmbHoldProgress();
        for (int i = 0; i < 6; i++) {
            int sx = dockX + 5 + i * (slotSize + gap);
            int sy = dockY + 4;
            gui.blit(HUD_TEX, sx + 1, sy + 1, ICON_UV_IDX[i] * 24, 24, 20, 20, 256, 128);

            if (cd > 0 || energy < SKILL_COSTS[i]) {
                gui.fill(sx + 1, sy + 1, sx + 21, sy + 21, 0x99000000);
            }

            float charge = (i == 2) ? lmbHold : (i == 5 ? rmbHold : 0.0F);
            if (charge > 0.01F) {
                int chH = Math.round(20 * charge);
                gui.fill(sx + 1, sy + 21 - chH, sx + 21, sy + 21, 0x88FFB300);
            }

            String lbl = keys[i];
            int lw = mc.font.width(lbl);
            int badgeColor = (i < 3) ? 0xFFFFD54F : 0xFF80DEEA;
            gui.fill(sx + 21 - lw - 2, sy + 13, sx + 21, sy + 22, 0xCC121010);
            gui.drawString(mc.font, lbl, sx + 20 - lw, sy + 14, badgeColor, false);
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
            // 4 corner brackets (double thickness when Hard-Locked via MMB)
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

            // Target readout card right beneath reticle: [LOCK] + Name + Distance + Live HP Bar
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
            // Subtle Khaenri'ah golden standby crosshair brackets
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
        gui.fill(0, 0, sw, 10, 0x88100D0B);
        gui.fill(0, sh - 8, sw, sh, 0x88100D0B);
        gui.fill(0, 10, sw, 11, 0xAA9C7A48);

        // Khaenri'ah corner brackets
        int m = 18;
        int len = 24;
        int col = 0xAAEEB84B;
        gui.fill(m, m, m + len, m + 2, col);
        gui.fill(m, m, m + 2, m + len, col);
        gui.fill(sw - m - len, m, sw - m, m + 2, col);
        gui.fill(sw - m - 2, m, sw - m, m + len, col);
        gui.fill(m, sh - 74, m + 2, sh - 74 - 14, col);
        gui.fill(sw - m - 2, sh - 74, sw - m, sh - 74 - 14, col);

        // Cracked visor glass fractures when Armor < 30%
        float armor = mech.getArmorIntegrity();
        if (armor < 30.0F) {
            boolean critical = armor <= 0.5F;
            int crackCol = critical ? 0xBBFFE0D0 : 0x77E8D8C8;
            // Top-right jagged crack radiating inward
            drawPixelCrack(gui, sw - 2, 12, -1, 1, critical ? 52 : 34, crackCol);
            drawPixelCrack(gui, sw - 18, 12, -1, 2, critical ? 36 : 22, crackCol);
            // Bottom-left jagged crack above telemetry panel
            drawPixelCrack(gui, 2, sh - 82, 1, -1, critical ? 44 : 26, crackCol);
            if (critical) {
                drawPixelCrack(gui, 2, 18, 1, 1, 38, crackCol);
            }
        }

        // Titanfall-style Embark Armored Visor Shutter Drop + Golden Scanline Boot (ANIM_ENTER_COCKPIT, 26 ticks)
        if (mech.getAnimId() == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float t = mech.getAnimTick() + partialTicks;
            if (t < 13.0F) {
                // Phase 1: Heavy armored shutters drop from top & rise from bottom and clamp shut at center
                float p = Mth.clamp(t / 13.0F, 0.0F, 1.0F);
                float ease = p * p * (3.0F - 2.0F * p);
                int shutterH = Math.round((sh * 0.52F) * ease);
                gui.fill(0, 0, sw, shutterH, 0xEE14110F);
                gui.fill(0, sh - shutterH, sw, sh, 0xEE14110F);
                gui.fill(0, shutterH - 2, sw, shutterH, 0xFFEEB84B);
                gui.fill(0, sh - shutterH, sw, sh - shutterH + 2, 0xFFEEB84B);
            } else if (t < 26.0F) {
                // Phase 2: Shutters open to visor rim + glowing golden scanline sweeps top-to-bottom
                float p = Mth.clamp((t - 13.0F) / 13.0F, 0.0F, 1.0F);
                float openEase = 1.0F - (float) Math.pow(1.0F - p, 3.0F);
                int shutterH = Math.round((sh * 0.52F) * (1.0F - openEase));
                if (shutterH > 10) {
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
                // Small branch fracture
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
