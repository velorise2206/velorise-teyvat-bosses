package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Clean, zero-text-spam Khaenri'ah Mech Cockpit HUD:
 * <ul>
 *   <li><b>1st-Person Visor Frame:</b> Sleek bronze corner brackets, subtle top/bottom visor vignette,
 *       amber boot-up flash during cockpit entry, and red low-battery pulse when energy &lt; 100.</li>
 *   <li><b>VIP Pixel Telemetry Panel:</b> Crisp pixel-art Heart-Core (HP), Shield (Armor), and
 *       10-Cell Redstone Battery bar (each cell = 1 Redstone Block).</li>
 *   <li><b>6-Skill Pixel Icon Dock:</b> Shows all 6 Ruin Guard combat skills with keybind badges
 *       and live cooldown / low-energy darkening.</li>
 * </ul>
 */
public final class RuinMechCockpitHud {
    private static final ResourceLocation HUD_TEX = TeyvatBossesConstants.id("textures/gui/ruin_mech_hud.png");
    // Order matches HUD dock: [L1 Clap, L2 3-Hit, L+ Cyclone, R1 Stomp, R2 Leap, R+ Missiles]
    private static final int[] SKILL_COSTS = {12, 22, 30, 15, 18, 25};
    private static final int[] ICON_UV_IDX = {0, 1, 3, 2, 4, 5};

    private RuinMechCockpitHud() {}

    public static void render(GuiGraphics gui, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RuinMechEntity mech = RuinMechCameraController.getPilotedMech();
        if (mech == null || mc.options.hideGui) return;

        int sw = gui.guiWidth();
        int sh = gui.guiHeight();
        boolean firstPerson = mc.options.getCameraType().isFirstPerson();

        RenderSystem.enableBlend();

        // 1. 1st-Person Cockpit Visor Frame & Boot-up Flash
        if (firstPerson) {
            renderVisorOverlay(gui, mech, sw, sh, partialTicks);
        }

        // 2. Bottom-Left Telemetry Panel (HP + Armor + 10-Cell Redstone Battery)
        int panelX = 12;
        int panelY = sh - 66;
        int panelW = 148;
        int panelH = 54;

        // Dark armored backing with subtle bronze trim
        gui.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xB8141110);
        drawBorder(gui, panelX, panelY, panelW, panelH, 0xCC9C7A48);

        // Row 1: HP Heart-Core Icon + Segmented Crimson Bar
        float hpPct = Mth.clamp(mech.getHealth() / Math.max(1.0F, mech.getMaxHealth()), 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 5, panelY + 4, 0, 0, 16, 16, 256, 128);
        drawBar(gui, panelX + 24, panelY + 8, 86, 8, hpPct, 0xFFD92B3A, 0xFF361418);
        gui.drawString(mc.font, Math.round(mech.getHealth()) + "", panelX + 114, panelY + 8, 0xFFFFD8D8, true);

        // Row 2: Armor Shield Icon + Segmented Steel-Blue Bar
        float armPct = Mth.clamp(mech.getArmorIntegrity() / RuinMechEntity.MAX_ARMOR, 0.0F, 1.0F);
        gui.blit(HUD_TEX, panelX + 5, panelY + 20, 16, 0, 16, 16, 256, 128);
        drawBar(gui, panelX + 24, panelY + 24, 86, 8, armPct, 0xFF64B5F6, 0xFF152536);
        gui.drawString(mc.font, Math.round(mech.getArmorIntegrity()) + "%", panelX + 114, panelY + 24, 0xFFD6EEFF, true);

        // Row 3: Redstone Battery Icon + 10 Individual Glowing Redstone Cells
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

        // 3. Bottom-Right 6-Skill Pixel Icon Dock
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

            // Darken if on cooldown or insufficient energy
            if (cd > 0 || energy < SKILL_COSTS[i]) {
                gui.fill(sx + 1, sy + 1, sx + 21, sy + 21, 0x99000000);
            }

            // Live amber charge fill bar on Hold slots (L+ at i=2, R+ at i=5)
            float charge = (i == 2) ? lmbHold : (i == 5 ? rmbHold : 0.0F);
            if (charge > 0.01F) {
                int chH = Math.round(20 * charge);
                gui.fill(sx + 1, sy + 21 - chH, sx + 21, sy + 21, 0x88FFB300);
            }

            // Crisp LMB (gold) / RMB (cyan) gesture badge
            String lbl = keys[i];
            int lw = mc.font.width(lbl);
            int badgeColor = (i < 3) ? 0xFFFFD54F : 0xFF80DEEA;
            gui.fill(sx + 21 - lw - 2, sy + 13, sx + 21, sy + 22, 0xCC121010);
            gui.drawString(mc.font, lbl, sx + 20 - lw, sy + 14, badgeColor, false);
        }

        RenderSystem.disableBlend();
    }

    private static void renderVisorOverlay(GuiGraphics gui, RuinMechEntity mech, int sw, int sh, float partialTicks) {
        // Subtle top & bottom dark visor trim (keeps center 100% clear)
        gui.fill(0, 0, sw, 10, 0x88100D0B);
        gui.fill(0, sh - 8, sw, sh, 0x88100D0B);
        gui.fill(0, 10, sw, 11, 0xAA9C7A48);

        // Sleek Khaenri'ah corner HUD brackets
        int m = 18;
        int len = 24;
        int col = 0xAAEEB84B;
        // Top-Left
        gui.fill(m, m, m + len, m + 2, col);
        gui.fill(m, m, m + 2, m + len, col);
        // Top-Right
        gui.fill(sw - m - len, m, sw - m, m + 2, col);
        gui.fill(sw - m - 2, m, sw - m, m + len, col);
        // Bottom-Left & Bottom-Right (above panels)
        gui.fill(m, sh - 74, m + 2, sh - 74 - 14, col);
        gui.fill(sw - m - 2, sh - 74, sw - m, sh - 74 - 14, col);

        // Cockpit boot-up amber visor flash during ANIM_ENTER_COCKPIT
        if (mech.getAnimId() == RuinMechEntity.ANIM_ENTER_COCKPIT) {
            float p = Mth.clamp((mech.getAnimTick() + partialTicks) / 24.0F, 0.0F, 1.0F);
            int alpha = (int) ((1.0F - p) * 115.0F);
            if (alpha > 2) {
                gui.fill(0, 0, sw, sh, (alpha << 24) | 0xFF9922);
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
