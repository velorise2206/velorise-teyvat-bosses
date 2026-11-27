package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.network.GeoDrillerControlPayload;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client-side input, camera lock/zoom, and compact cockpit HUD controller for {@link GeoDrillerEntity}.
 * Controls: LMB = Toggle Drill ON/OFF, RMB = Forward Drill Surge, B = Toggle Auto-Pave ON/OFF,
 * Space = Cycle Bore Angle (Level / Down / Up), F = Toggle Headlight.
 */
public final class GeoDrillerClientController {
    public static final float THIRD_PERSON_ZOOM = 5.2F;

    private static Consumer<GeoDrillerControlPayload> packetSender = p -> {};
    private static boolean prevLmb = false;
    private static boolean prevRmb = false;
    private static boolean prevB = false;
    private static boolean prevF = false;
    private static boolean prevSpace = false;
    private static float lastDrillerYaw = 0.0F;
    private static boolean wasPiloting = false;

    private GeoDrillerClientController() {}

    public static void setPacketSender(Consumer<GeoDrillerControlPayload> sender) {
        packetSender = sender != null ? sender : p -> {};
    }

    public static GeoDrillerEntity getPilotedDriller() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof GeoDrillerEntity driller) {
            return driller;
        }
        return null;
    }

    public static boolean isPilotingDriller() {
        return getPilotedDriller() != null;
    }

    public static boolean isThirdPersonLocked() {
        // 3rd-person camera is never hard-locked; only the driver's seated posture is locked
        return false;
    }

    public static float getLockedThirdPersonYaw(float partialTick) {
        GeoDrillerEntity driller = getPilotedDriller();
        if (driller == null) return 0.0F;
        return Mth.rotLerp(partialTick, driller.yRotO, driller.getYRot());
    }

    /**
     * Computes AAA diesel/drilling camera rumble &amp; steering roll {@code [yawOff, pitchOff, rollOff]}
     * for full parity with Ruin Mech &amp; Ruin Submersible on both Fabric and NeoForge.
     */
    public static float[] computeCameraOffsets(float partialTick) {
        GeoDrillerEntity driller = getPilotedDriller();
        if (driller == null || !driller.isFullyAssembled() || driller.getFuel() <= 0) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }
        float t = driller.tickCount + partialTick;
        float driveSpool = Mth.lerp(partialTick, driller.prevDriveSpool, driller.driveSpool);
        float drillSpool = Mth.lerp(partialTick, driller.prevDrillSpool, driller.drillSpool);
        float rumble = Math.max(driveSpool * 0.45F, drillSpool * 0.90F);
        if (driller.isSurging()) {
            rumble = 1.45F;
        }
        float yawOff = Mth.sin(t * 1.9F) * 0.24F * rumble;
        float pitchOff = Mth.cos(t * 2.7F) * 0.32F * rumble;
        float steerRoll = Mth.clamp(-driller.getSteerInput() * 1.85F, -2.0F, 2.0F) + Mth.sin(t * 2.3F) * 0.35F * rumble;
        return new float[]{yawOff, pitchOff, steerRoll};
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || !(mc.player.getVehicle() instanceof GeoDrillerEntity driller)) {
            prevLmb = prevRmb = prevB = prevF = prevSpace = false;
            wasPiloting = false;
            return;
        }

        // Enforce strict seated camera & head constraints
        float drillerYaw = driller.getYRot();
        if (!wasPiloting) {
            lastDrillerYaw = drillerYaw;
            wasPiloting = true;
            mc.player.setYRot(drillerYaw);
            mc.player.yRotO = drillerYaw;
            // Ignore mount click so mounting with RMB does not immediately trigger Surge
            long winInit = mc.getWindow().getWindow();
            prevLmb = GLFW.glfwGetMouseButton(winInit, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            prevRmb = GLFW.glfwGetMouseButton(winInit, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        }
        float yawDelta = Mth.wrapDegrees(drillerYaw - lastDrillerYaw);
        lastDrillerYaw = drillerYaw;

        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            // 1st-person: carry vehicle turn smoothly and limit look to 180-deg front cockpit arc
            float nextYaw = mc.player.getYRot() + yawDelta;
            float rel = Mth.clamp(Mth.wrapDegrees(nextYaw - drillerYaw), -90.0F, 90.0F);
            float clamped = drillerYaw + rel;
            mc.player.setYRot(clamped);
            mc.player.setYHeadRot(clamped);
            mc.player.setXRot(Mth.clamp(mc.player.getXRot(), -45.0F, 55.0F));
        }
        // 3rd-person: do NOT overwrite yRot or yHeadRot (Camera.setup reads yHeadRot!) so 360-deg free-look works completely
        mc.player.setYBodyRot(drillerYaw);
        mc.player.yBodyRotO = driller.yBodyRotO;

        if (mc.screen != null || !driller.isFullyAssembled()) {
            prevLmb = prevRmb = prevB = prevF = prevSpace = false;
            return;
        }

        long win = mc.getWindow().getWindow();
        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean keyB = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS;
        boolean keyF = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
        boolean keySpace = mc.options.keyJump.isDown();

        if (lmb && !prevLmb) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_TOGGLE_DRILL));
        }
        if (rmb && !prevRmb) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_DRILL_SURGE));
        }
        if (keyB && !prevB) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_TOGGLE_PAVE));
        }
        if (keySpace && !prevSpace) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_CYCLE_PITCH));
        }
        if (keyF && !prevF) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_TOGGLE_LIGHT));
        }
        prevLmb = lmb;
        prevRmb = rmb;
        prevB = keyB;
        prevF = keyF;
        prevSpace = keySpace;
    }

    public static void renderHud(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !(mc.player.getVehicle() instanceof GeoDrillerEntity driller)) {
            return;
        }
        int sh = gui.guiHeight();
        int x = 12;
        int y = sh - 96;
        int w = 212;
        int h = 84;
        // Industrial brass-gold bordered cockpit telemetry panel
        gui.fill(x, y, x + w, y + h, 0xC416120E);
        gui.fill(x, y, x + w, y + 2, 0xFFE6A835);
        gui.fill(x, y + h - 2, x + w, y + h, 0xFFE6A835);
        gui.fill(x, y, x + 2, y + h, 0xAA8C6220);
        gui.fill(x + w - 2, y, x + w, y + h, 0xAA8C6220);

        String drillStr = driller.isDrillArmed() ? "ON" : "OFF";
        int drillCol = driller.isDrillArmed() ? 0xFF55FF77 : 0xFFFF6655;
        String pitchStr = switch (driller.getPitchMode()) {
            case GeoDrillerEntity.PITCH_DOWN -> "DOWN";
            case GeoDrillerEntity.PITCH_UP -> "UP";
            default -> "LEVEL";
        };
        String paveStr = driller.isPaveEnabled() ? "ON" : "OFF";
        int paveCol = driller.isPaveEnabled() ? 0xFF55FF77 : 0xFFAAAAAA;
        String lightStr = driller.isHeadlightOn() ? "ON" : "OFF";
        int lightCol = driller.isHeadlightOn() ? 0xFFFFE477 : 0xFFAAAAAA;
        String surgeStr = driller.isSurging() ? "SURGING!" : "READY";
        int surgeCol = driller.isSurging() ? 0xFFFFF066 : 0xFFFFAA44;

        gui.drawString(mc.font, "[LMB] DRILL: " + drillStr, x + 7, y + 6, drillCol, true);
        gui.drawString(mc.font, "[RMB] " + surgeStr, x + 114, y + 6, surgeCol, true);
        gui.drawString(mc.font, "[B] PAVE: " + paveStr, x + 7, y + 18, paveCol, true);
        gui.drawString(mc.font, "[SPACE] " + pitchStr, x + 114, y + 18, 0xFFFFCC55, true);
        gui.drawString(mc.font, "[F] LIGHT: " + lightStr, x + 7, y + 30, lightCol, true);
        gui.drawString(mc.font, "BORE: " + driller.getBoreLabel(), x + 114, y + 30, 0xFF88EEFF, true);

        // Cargo capacity & installed Bore Enchantment badges
        int cargoSlots = driller.getChestCount() >= 2 ? 54 : (driller.getChestCount() == 1 ? 27 : 0);
        StringBuilder ench = new StringBuilder();
        if (driller.hasSilkTouch()) ench.append("SILK ");
        else if (driller.getFortuneLevel() > 0) ench.append("F").append(driller.getFortuneLevel()).append(' ');
        if (driller.getEfficiencyLevel() > 0) ench.append("E").append(driller.getEfficiencyLevel()).append(' ');
        if (driller.getUnbreakingLevel() > 0) ench.append("U").append(driller.getUnbreakingLevel());
        String enchStr = ench.length() > 0 ? ench.toString().trim() : "STD";
        gui.drawString(mc.font, "CARGO: " + cargoSlots + "s", x + 7, y + 43, cargoSlots > 0 ? 0xFFD9C298 : 0xFF888888, true);
        gui.drawString(mc.font, "MOD: " + enchStr, x + 114, y + 43, 0xFFD89EFF, true);

        // Fuel bar
        int fuel = driller.getFuel();
        int barW = w - 14;
        int fillW = (int) (barW * (fuel / (float) GeoDrillerEntity.MAX_FUEL));
        gui.drawString(mc.font, "FUEL: " + fuel + "/" + GeoDrillerEntity.MAX_FUEL, x + 7, y + 57, 0xFFFFB84D, true);
        gui.fill(x + 7, y + 70, x + 7 + barW, y + 76, 0xFF2B221A);
        gui.fill(x + 7, y + 70, x + 7 + fillW, y + 76, fuel > 150 ? 0xFFF5A623 : 0xFFFF4444);
    }
}
