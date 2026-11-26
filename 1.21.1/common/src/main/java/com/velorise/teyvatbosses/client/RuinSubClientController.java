package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.RuinSubmersibleEntity;
import com.velorise.teyvatbosses.network.RuinSubControlPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client-side 6-DoF submarine pilot input controller &amp; compact non-overlapping cockpit HUD.
 * Feeds W/A/S/D + Space/Ctrl directly into the client-authoritative {@link RuinSubmersibleEntity#travel}
 * (matching Alex's Caves Submarine &amp; Minecraft 1.21.1 vehicle physics) for instant, zero-lag movement.
 */
public final class RuinSubClientController {
    public static final float THIRD_PERSON_ZOOM = 7.5F;

    private static Consumer<RuinSubControlPayload> packetSender = p -> {};
    private static boolean prevLmb = false;
    private static boolean prevRmb = false;
    private static boolean prevF = false;
    private static boolean prevR = false;
    private static float lastFwd = 0.0F;
    private static float lastStrafe = 0.0F;
    private static int lastVert = 0;
    private static int heartbeat = 0;

    private RuinSubClientController() {}

    public static void setPacketSender(Consumer<RuinSubControlPayload> sender) {
        packetSender = sender != null ? sender : p -> {};
    }

    public static boolean isPilotingSubmersible() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getVehicle() instanceof RuinSubmersibleEntity;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.screen != null || !(mc.player.getVehicle() instanceof RuinSubmersibleEntity sub)) {
            prevLmb = prevRmb = prevF = prevR = false;
            lastFwd = 0.0F;
            lastStrafe = 0.0F;
            lastVert = 0;
            heartbeat = 0;
            return;
        }
        long win = mc.getWindow().getWindow();
        float fwd = (mc.options.keyUp.isDown() ? 1.0F : 0.0F) - (mc.options.keyDown.isDown() ? 1.0F : 0.0F);
        float strafe = (mc.options.keyLeft.isDown() ? 1.0F : 0.0F) - (mc.options.keyRight.isDown() ? 1.0F : 0.0F);
        boolean space = mc.options.keyJump.isDown();
        boolean ctrl = mc.options.keySprint.isDown();
        int vert = (space ? 1 : 0) - (ctrl ? 1 : 0);

        // Feed live inputs directly to client-authoritative vehicle travel() (0ms latency!)
        sub.setClientPilotInputs(fwd, strafe, vert);

        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean keyF = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
        boolean keyR = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;

        int action = RuinSubmersibleEntity.ACTION_NONE;
        if (lmb && !prevLmb) action = RuinSubmersibleEntity.ACTION_SONAR;
        else if (rmb && !prevRmb) action = RuinSubmersibleEntity.ACTION_TORPEDO;
        else if (keyR && !prevR) action = RuinSubmersibleEntity.ACTION_DASH;
        else if (keyF && !prevF) action = RuinSubmersibleEntity.ACTION_HEADLIGHT;

        prevLmb = lmb;
        prevRmb = rmb;
        prevF = keyF;
        prevR = keyR;

        Vec3 aim = mc.player.getLookAngle();
        if (action == RuinSubmersibleEntity.ACTION_DASH) {
            sub.triggerClientDash(aim);
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
        int yBase = sh - 46;

        int lx = cx - 196;
        g.fill(lx, yBase, lx + 96, yBase + 40, 0xAA07121C);
        g.renderOutline(lx, yBase, 96, 40, 0xFF22D3EE);
        int hpPct = Math.round(sub.getHealth() * 100.0F / sub.getMaxHealth());
        int enPct = Math.round(sub.getEnergy() * 100.0F / RuinSubmersibleEntity.MAX_ENERGY);
        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.hull", hpPct), lx + 5, yBase + 4, 0xFF67E8F9, false);
        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.power", enPct), lx + 5, yBase + 15, 0xFFFCD34D, false);
        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.depth", (int) sub.getY()), lx + 5, yBase + 27, 0xFFC2D4E6, false);

        int rx = cx + 100;
        g.fill(rx, yBase, rx + 96, yBase + 40, 0xAA07121C);
        g.renderOutline(rx, yBase, 96, 40, 0xFFF59E0B);
        int ammo = sub.getTorpedoAmmo();
        g.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.sub.torpedoes", ammo, 6), rx + 5, yBase + 4, 0xFFFCD34D, false);
        for (int i = 0; i < 6; i++) {
            int px = rx + 6 + i * 14;
            int col = i < ammo ? 0xFF22D3EE : 0xFF1E3448;
            g.fill(px, yBase + 16, px + 10, yBase + 22, col);
        }
        Component lightTxt = Component.translatable(sub.isHeadlightOn() ? "hud.velorise_teyvat_bosses.sub.light_on" : "hud.velorise_teyvat_bosses.sub.light_off");
        g.drawString(mc.font, lightTxt, rx + 5, yBase + 27, sub.isHeadlightOn() ? 0xFF67E8F9 : 0xFF92ABC2, false);
    }
}
