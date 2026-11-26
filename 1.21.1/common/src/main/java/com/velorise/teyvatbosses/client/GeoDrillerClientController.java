package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.network.GeoDrillerControlPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client-side input & compact cockpit HUD controller for {@link GeoDrillerEntity}.
 * Keys: R = Toggle Drill ON/OFF, Space = Cycle Bore Angle (Level / Down / Up), F = Toggle Headlight.
 */
public final class GeoDrillerClientController {
    private static Consumer<GeoDrillerControlPayload> packetSender = p -> {};
    private static boolean prevR = false;
    private static boolean prevF = false;
    private static boolean prevSpace = false;

    private GeoDrillerClientController() {}

    public static void setPacketSender(Consumer<GeoDrillerControlPayload> sender) {
        packetSender = sender != null ? sender : p -> {};
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.screen != null || !(mc.player.getVehicle() instanceof GeoDrillerEntity driller) || !driller.isFullyAssembled()) {
            prevR = prevF = prevSpace = false;
            return;
        }
        long win = mc.getWindow().getWindow();
        boolean keyR = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
        boolean keyF = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
        boolean keySpace = mc.options.keyJump.isDown();

        if (keyR && !prevR) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_TOGGLE_DRILL));
        }
        if (keySpace && !prevSpace) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_CYCLE_PITCH));
        }
        if (keyF && !prevF) {
            packetSender.accept(new GeoDrillerControlPayload(GeoDrillerControlPayload.ACTION_TOGGLE_LIGHT));
        }
        prevR = keyR;
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
        int y = sh - 68;
        int w = 166;
        int h = 56;
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
        String lightStr = driller.isHeadlightOn() ? "ON" : "OFF";
        int lightCol = driller.isHeadlightOn() ? 0xFFFFE066 : 0xFFAAAAAA;

        gui.drawString(mc.font, "[R] DRILL: " + drillStr, x + 6, y + 6, drillCol, true);
        gui.drawString(mc.font, "[SPACE] " + pitchStr, x + 88, y + 6, 0xFFFFCC55, true);
        gui.drawString(mc.font, "[F] LIGHT: " + lightStr, x + 6, y + 18, lightCol, true);
        gui.drawString(mc.font, "BORE: " + driller.getBoreLabel(), x + 88, y + 18, 0xFF88EEFF, true);

        // Fuel bar
        int fuel = driller.getFuel();
        int barW = w - 12;
        int fillW = (int) (barW * (fuel / (float) GeoDrillerEntity.MAX_FUEL));
        gui.drawString(mc.font, "FUEL: " + fuel + "/" + GeoDrillerEntity.MAX_FUEL, x + 6, y + 31, 0xFFFFB84D, true);
        gui.fill(x + 6, y + 43, x + 6 + barW, y + 49, 0xFF332A22);
        gui.fill(x + 6, y + 43, x + 6 + fillW, y + 49, fuel > 150 ? 0xFFF5A623 : 0xFFFF4444);
    }
}
