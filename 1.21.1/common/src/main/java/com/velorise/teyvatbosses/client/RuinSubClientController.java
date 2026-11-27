package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import com.velorise.teyvatbosses.network.RuinSubControlPayload;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client-side 6-DoF submarine pilot controller, camera banking/recoil generator,
 * Alex's Caves-style 1st-person glass canopy visor overlay, and Fontaine cockpit HUD.
 */
public final class RuinSubClientController {
    public static final float THIRD_PERSON_ZOOM = 7.8F;
    private static final ResourceLocation VISOR_TEX = TeyvatBossesConstants.id("textures/gui/sub_cockpit_visor.png");
    private static final String[] COMPASS_DIRS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    private static Consumer<RuinSubControlPayload> packetSender = p -> {};
    private static boolean prevRmb = false;
    private static int boostPulseTick = 0;
    private static float lastFwd = 0.0F;
    private static float lastStrafe = 0.0F;
    private static int lastVert = 0;
    private static int heartbeat = 0;

    // Smooth camera recoil & banking feedback
    private static float recoilPitch = 0.0F;
    private static float prevRecoilPitch = 0.0F;
    private static boolean targetLocked = false;

    private RuinSubClientController() {}

    public static void setPacketSender(Consumer<RuinSubControlPayload> sender) {
        packetSender = sender != null ? sender : p -> {};
    }

    public static boolean isPilotingSubmersible() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getVehicle() instanceof RuinSubmersibleEntity;
    }

    public static RuinSubmersibleEntity getPilotedSubmersible() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof RuinSubmersibleEntity sub) {
            return sub;
        }
        return null;
    }

    public static float[] computeCameraOffsets(float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        RuinSubmersibleEntity sub = getPilotedSubmersible();
        if (sub == null) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }
        float pitchOff = Mth.lerp(partialTick, prevRecoilPitch, recoilPitch);
        float rollOff = 0.0F;
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            rollOff = Mth.clamp(sub.getHullRollDeg(partialTick) * 0.22F, -5.5F, 5.5F);
        }
        return new float[]{0.0F, pitchOff, rollOff};
    }

    public static void tick(Minecraft mc) {
        prevRecoilPitch = recoilPitch;
        recoilPitch *= 0.68F;
        if (Math.abs(recoilPitch) < 0.02F) recoilPitch = 0.0F;

        if (mc.player == null || mc.screen != null || !(mc.player.getVehicle() instanceof RuinSubmersibleEntity sub)) {
            prevRmb = false;
            boostPulseTick = 0;
            lastFwd = 0.0F;
            lastStrafe = 0.0F;
            lastVert = 0;
            heartbeat = 0;
            targetLocked = false;
            return;
        }
        long win = mc.getWindow().getWindow();
        float fwd = (mc.options.keyUp.isDown() ? 1.0F : 0.0F) - (mc.options.keyDown.isDown() ? 1.0F : 0.0F);
        float strafe = (mc.options.keyLeft.isDown() ? 1.0F : 0.0F) - (mc.options.keyRight.isDown() ? 1.0F : 0.0F);
        boolean space = mc.options.keyJump.isDown();
        boolean ctrl = mc.options.keySprint.isDown();
        int vert = (space ? 1 : 0) - (ctrl ? 1 : 0);

        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean keyR = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;

        boolean boostHeld = keyR && sub.getEnergy() > 1.0F && sub.isInOrOnWater();
        sub.setClientPilotInputs(fwd, strafe, vert, boostHeld);

        int action = RuinSubmersibleEntity.ACTION_NONE;
        if (lmb && sub.canClientFireTorpedo()) {
            action = RuinSubmersibleEntity.ACTION_TORPEDO;
            sub.markClientTorpedoFired();
            recoilPitch = -1.5F;
        } else if (rmb && !prevRmb && sub.canClientTriggerSonar()) {
            action = RuinSubmersibleEntity.ACTION_SONAR;
            sub.markClientSonarTriggered();
            recoilPitch = -1.1F;
        } else if (boostHeld) {
            if (boostPulseTick <= 0) {
                action = RuinSubmersibleEntity.ACTION_DASH;
                boostPulseTick = 3;
            } else {
                boostPulseTick--;
            }
        } else {
            boostPulseTick = 0;
        }

        prevRmb = rmb;
        Vec3 aim = mc.player.getLookAngle();

        if (mc.level != null && (sub.tickCount & 1) == 0) {
            Vec3 eyePos = mc.player.getEyePosition();
            LivingEntity lock = sub.findConeTarget(mc.level, eyePos, aim, 34.0D);
            targetLocked = lock != null;
        }

        boolean inputChanged = (fwd != lastFwd || strafe != lastStrafe || vert != lastVert);
        boolean moving = (fwd != 0.0F || strafe != 0.0F || vert != 0);
        heartbeat++;

        if (inputChanged || action != RuinSubmersibleEntity.ACTION_NONE || (moving && heartbeat >= 5)) {
            lastFwd = fwd;
            lastStrafe = strafe;
            lastVert = vert;
            heartbeat = 0;
            packetSender.accept(new RuinSubControlPayload(fwd, strafe, vert, action, (float) aim.x, (float) aim.y, (float) aim.z));
        }
    }

    public static void renderHud(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !(mc.player.getVehicle() instanceof RuinSubmersibleEntity sub)) {
            return;
        }
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int cx = sw / 2;

        // Exact vanilla Minecraft crosshair center pixel: (sw - 15) / 2 + 7
        int crossX = (sw - 15) / 2 + 7;
        int crossY = (sh - 15) / 2 + 7;

        // 1st-person Fontaine curved glass canopy overlay + pixel-aligned reticle
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.blit(VISOR_TEX, 0, 0, 0.0F, 0.0F, sw, sh, sw, sh);
            RenderSystem.disableBlend();

            // Pixel-perfect symmetric corner brackets centered strictly on vanilla '+' crosshair
            int retCol = targetLocked ? 0xFFF59E0B : 0xAA22D3EE;
            int r = targetLocked ? 11 : 8;
            // Top-left corner
            g.fill(crossX - r, crossY - r, crossX - r + 4, crossY - r + 1, retCol);
            g.fill(crossX - r, crossY - r, crossX - r + 1, crossY - r + 4, retCol);
            // Top-right corner
            g.fill(crossX + r - 3, crossY - r, crossX + r + 1, crossY - r + 1, retCol);
            g.fill(crossX + r, crossY - r, crossX + r + 1, crossY - r + 4, retCol);
            // Bottom-left corner
            g.fill(crossX - r, crossY + r, crossX - r + 4, crossY + r + 1, retCol);
            g.fill(crossX - r, crossY + r - 3, crossX - r + 1, crossY + r + 1, retCol);
            // Bottom-right corner
            g.fill(crossX + r - 3, crossY + r, crossX + r + 1, crossY + r + 1, retCol);
            g.fill(crossX + r, crossY + r - 3, crossX + r + 1, crossY + r + 1, retCol);

            if (targetLocked) {
                g.drawCenteredString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.lock"), crossX, crossY + 15, 0xFFFBBF24);
            }
            int pitchInt = Math.round(-sub.getHullPitchDeg());
            if (Math.abs(pitchInt) >= 3) {
                String pTxt = (pitchInt > 0 ? "+" : "") + pitchInt + "\u00B0";
                g.drawString(mc.font, pTxt, crossX + 16, crossY - 3, 0xCC67E8F9, false);
            }
        }

        int yBase = sh - 52;

        // Left Panel: Hull, Power, Depth + Speed + Compass
        int lw = 104;
        int lh = 46;
        int lx = cx - 98 - lw;
        g.fill(lx, yBase, lx + lw, yBase + lh, 0xB807121C);
        g.renderOutline(lx, yBase, lw, lh, 0xFF22D3EE);

        int hpPct = Math.round(sub.getHealth() * 100.0F / sub.getMaxHealth());
        int enPct = Math.round(sub.getEnergy() * 100.0F / Math.max(1.0F, sub.getMaxEnergy()));
        int hpBarCol = hpPct > 40 ? 0xFF22D3EE : (hpPct > 20 ? 0xFFF59E0B : 0xFFEF4444);

        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.hull", hpPct), lx + 5, yBase + 4, 0xFF67E8F9, false);
        g.fill(lx + 5, yBase + 13, lx + lw - 5, yBase + 16, 0xFF17293B);
        g.fill(lx + 5, yBase + 13, lx + 5 + Math.round((lw - 10) * (hpPct / 100.0F)), yBase + 16, hpBarCol);

        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.power", enPct), lx + 5, yBase + 19, 0xFFFCD34D, false);
        g.fill(lx + 5, yBase + 28, lx + lw - 5, yBase + 31, 0xFF17293B);
        g.fill(lx + 5, yBase + 28, lx + 5 + Math.round((lw - 10) * (enPct / 100.0F)), yBase + 31, 0xFFF59E0B);

        int speedKt = Math.round((float) (sub.getDeltaMovement().length() * 20.0D * 1.94D));
        float normYaw = ((sub.getYRot() % 360.0F) + 360.0F) % 360.0F;
        String heading = COMPASS_DIRS[Math.round(normYaw / 45.0F) & 7];
        String navLine = "Y:" + (int) sub.getY() + "  " + speedKt + "kt  " + heading;
        g.drawString(mc.font, navLine, lx + 5, yBase + 35, 0xFFC2D4E6, false);

        // Right Panel: Torpedoes [LMB], Sonar [RMB], Dash [R], Light [F] — zero text overlap!
        int rx = cx + 98;
        int rw = 126;
        int rh = 46;
        g.fill(rx, yBase, rx + rw, yBase + rh, 0xB807121C);
        g.renderOutline(rx, yBase, rw, rh, 0xFFF59E0B);

        int ammo = sub.getTorpedoAmmo();
        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.torpedoes_lmb", ammo, RuinSubmersibleEntity.MAX_TORPEDOES), rx + 5, yBase + 4, 0xFFFCD34D, false);
        for (int i = 0; i < RuinSubmersibleEntity.MAX_TORPEDOES; i++) {
            int px = rx + 6 + i * 19;
            int col = i < ammo ? (targetLocked ? 0xFFFBBF24 : 0xFF22D3EE) : 0xFF1E3448;
            g.fill(px, yBase + 14, px + 14, yBase + 19, col);
        }

        boolean sonarReady = sub.getClientSonarCooldown() <= 0;
        boolean boostingNow = sub.isClientBoosting();
        int sonarCol = sonarReady ? 0xFF67E8F9 : 0xFF64748B;
        int dashCol = boostingNow ? 0xFF22D3EE : (sub.getEnergy() > 1.0F ? 0xFFFCD34D : 0xFF64748B);
        Component sonarTxt = Component.translatable("hud.velorise_teyvat_bosses.sub.sonar_rmb");
        Component dashTxt = Component.translatable("hud.velorise_teyvat_bosses.sub.dash_r");
        g.drawString(mc.font, sonarTxt, rx + 5, yBase + 26, sonarCol, false);
        g.drawString(mc.font, dashTxt, rx + rw - 5 - mc.font.width(dashTxt), yBase + 26, dashCol, false);
    }
}
