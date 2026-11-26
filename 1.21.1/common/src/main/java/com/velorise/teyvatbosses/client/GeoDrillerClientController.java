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
    public static final float THIRD_PERSON_ZOOM = 6.2F;

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
        Minecraft mc = Minecraft.getInstance();
        return getPilotedDriller() != null && mc.options.getCameraType() != CameraType.FIRST_PERSON;
    }

    public static float getLockedThirdPersonYaw(float partialTick) {
        GeoDrillerEntity driller = getPilotedDriller();
        if (driller == null) return 0.0F;
        return Mth.rotLerp(partialTick, driller.yRotO, driller.getYRot());
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
            // 1st-person: carry vehicle turn smoothly and allow 180-deg (-90..+90) scenic glance left/right
            float nextYaw = mc.player.getYRot() + yawDelta;
            float rel = Mth.clamp(Mth.wrapDegrees(nextYaw - drillerYaw), -90.0F, 90.0F);
            mc.player.setYRot(drillerYaw + rel);
            mc.player.setXRot(Mth.clamp(mc.player.getXRot(), -45.0F, 55.0F));
        } else {
            // 3rd-person: hard-lock look directly forward with the driller
            mc.player.setYRot(drillerYaw);
            mc.player.yRotO = driller.yRotO;
            mc.player.setXRot(14.0F);
            mc.player.xRotO = 14.0F;
        }
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
        int y = sh - 80;
        int w = 192;
        int h = 68;
        gui.fill(x, y, x + w, y + h, 0xB8181410);
        gui.fill(x, y, x + w, y + 1, 0xFFE6A835);
        gui.fill(x, y + h - 1, x + w, y + h, 0xFFE6A835);

        String drillStr = driller.isDrillArmed() ? "ON" : "OFF";
        int drillCol = driller.isDrillArmed() ? 0xFF55FF55 : 0xFFFF6655;
        String pitchStr = switch (driller.getPitchMode()) {
            case GeoDrillerEntity.PITCH_DOWN -> "DOWN";
            case GeoDrillerEntity.PITCH_UP -> "UP";
            default -> "LEVEL";
        };
        String paveStr = driller.isPaveEnabled() ? "ON" : "OFF";
        int paveCol = driller.isPaveEnabled() ? 0xFF55FF55 : 0xFFAAAAAA;
        String lightStr = driller.isHeadlightOn() ? "ON" : "OFF";
        int lightCol = driller.isHeadlightOn() ? 0xFFFFE066 : 0xFFAAAAAA;

        gui.drawString(mc.font, "[LMB] DRILL: " + drillStr, x + 6, y + 6, drillCol, true);
        gui.drawString(mc.font, "[RMB] SURGE", x + 108, y + 6, driller.isSurging() ? 0xFFFFEE55 : 0xFFFFAA44, true);
        gui.drawString(mc.font, "[B] PAVE: " + paveStr, x + 6, y + 18, paveCol, true);
        gui.drawString(mc.font, "[SPACE] " + pitchStr, x + 108, y + 18, 0xFFFFCC55, true);
        gui.drawString(mc.font, "[F] LIGHT: " + lightStr, x + 6, y + 30, lightCol, true);
        gui.drawString(mc.font, "BORE: " + driller.getBoreLabel(), x + 108, y + 30, 0xFF88EEFF, true);

        // Fuel bar
        int fuel = driller.getFuel();
        int barW = w - 12;
        int fillW = (int) (barW * (fuel / (float) GeoDrillerEntity.MAX_FUEL));
        gui.drawString(mc.font, "FUEL: " + fuel + "/" + GeoDrillerEntity.MAX_FUEL, x + 6, y + 43, 0xFFFFB84D, true);
        gui.fill(x + 6, y + 55, x + 6 + barW, y + 61, 0xFF332A22);
        gui.fill(x + 6, y + 55, x + 6 + fillW, y + 61, fuel > 150 ? 0xFFF5A623 : 0xFFFF4444);
    }
}
