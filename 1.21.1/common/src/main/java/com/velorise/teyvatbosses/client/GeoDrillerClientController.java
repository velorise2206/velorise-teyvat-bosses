package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.network.GeoDrillerControlPayload;
import com.velorise.teyvatbosses.registry.TeyvatBossesSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Client-side input, camera rumble, smooth drill coast-down audio, and compact icon-based HUD
 * for {@link GeoDrillerEntity}.
 */
public final class GeoDrillerClientController {
    public static final float THIRD_PERSON_ZOOM = 5.2F;

    private static Consumer<GeoDrillerControlPayload> packetSender = p -> {};
    private static final Map<GeoDrillerEntity, DrillerSpinSoundInstance> ACTIVE_SPIN_SOUNDS = new WeakHashMap<>();

    private static boolean prevLmb = false;
    private static boolean prevR = false;
    private static boolean prevRmb = false;
    private static boolean prevB = false;
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
        return false;
    }

    public static float getLockedThirdPersonYaw(float partialTick) {
        GeoDrillerEntity driller = getPilotedDriller();
        if (driller == null) return 0.0F;
        return Mth.rotLerp(partialTick, driller.yRotO, driller.getYRot());
    }

    /**
     * Ensures a looping client-side drill spin sound is active for {@code driller} and smoothly
     * fades both volume and pitch down as the heavy drill head coasts to a stop after shutdown/dismount.
     */
    public static void ensureSpinSound(GeoDrillerEntity driller) {
        if (driller == null || !driller.isAlive() || driller.drillSpool <= 0.03F) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;
        DrillerSpinSoundInstance existing = ACTIVE_SPIN_SOUNDS.get(driller);
        if (existing != null && !existing.isStopped()) return;
        SoundEvent boreSfx = TeyvatBossesSounds.drillBoreSpin();
        if (boreSfx == null) return;
        DrillerSpinSoundInstance inst = new DrillerSpinSoundInstance(driller, boreSfx);
        ACTIVE_SPIN_SOUNDS.put(driller, inst);
        mc.getSoundManager().play(inst);
    }

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
            prevLmb = prevRmb = prevR = prevB = prevSpace = false;
            wasPiloting = false;
            return;
        }

        if (driller.drillSpool > 0.03F) {
            ensureSpinSound(driller);
        }

        float drillerYaw = driller.getYRot();
        if (!wasPiloting) {
            lastDrillerYaw = drillerYaw;
            wasPiloting = true;
            long winInit = mc.getWindow().getWindow();
            prevLmb = GLFW.glfwGetMouseButton(winInit, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            prevRmb = GLFW.glfwGetMouseButton(winInit, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        }
        lastDrillerYaw = drillerYaw;
        mc.player.setYBodyRot(drillerYaw);
        mc.player.yBodyRotO = driller.yBodyRotO;

        if (mc.screen != null || !driller.isFullyAssembled()) {
            prevLmb = prevRmb = prevR = prevB = prevSpace = false;
            return;
        }

        long win = mc.getWindow().getWindow();
        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean keyR = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
        boolean keyB = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_B) == GLFW.GLFW_PRESS;
        boolean keySpace = mc.options.keyJump.isDown();

        if ((lmb && !prevLmb) || (keyR && !prevR)) {
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
        prevLmb = lmb;
        prevRmb = rmb;
        prevR = keyR;
        prevB = keyB;
        prevSpace = keySpace;
    }

    /**
     * Ultra-compact icon + keybind HUD strip tucked cleanly in the bottom-left corner
     * so it never overlaps the hotbar or dismount prompt even at high GUI scales.
     */
    public static void renderHud(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !(mc.player.getVehicle() instanceof GeoDrillerEntity driller)) {
            return;
        }
        int sw = gui.guiWidth();
        int sh = gui.guiHeight();
        int w = 108;
        int h = 38;
        int x = 6;
        int y = (x + w + 4 < (sw / 2 - 91)) ? (sh - h - 6) : (sh - h - 26);

        gui.fill(x, y, x + w, y + h, 0xB814110D);
        gui.fill(x, y, x + w, y + 1, 0xFFE6A835);
        gui.fill(x, y + h - 1, x + w, y + h, 0xFFE6A835);
        gui.fill(x, y, x + 1, y + h, 0x998C6220);
        gui.fill(x + w - 1, y, x + w, y + h, 0x998C6220);

        boolean drillOn = driller.isDrillArmed();
        boolean surging = driller.isSurging();
        int pitch = driller.getPitchMode();
        boolean paveOn = driller.isPaveEnabled();

        int slotY = y + 3;
        int sx = x + 4;

        // 1. Drill [L]
        drawPillBg(gui, sx, slotY, 23, 19, drillOn ? 0x55226633 : 0x44221C16, drillOn ? 0xFF55FF77 : 0xFF555555);
        drawDrillIcon(gui, sx + 2, slotY + 3, drillOn ? 0xFF55FF77 : 0xFF888888);
        gui.drawString(mc.font, "L", sx + 14, slotY + 6, drillOn ? 0xFFCCFFDD : 0xFF999999, false);
        sx += 25;

        // 2. Surge [R]
        drawPillBg(gui, sx, slotY, 23, 19, surging ? 0x77886611 : 0x44221C16, surging ? 0xFFFFEE55 : 0xFFAA7733);
        drawSurgeIcon(gui, sx + 2, slotY + 3, surging ? 0xFFFFF066 : 0xFFFFAA44);
        gui.drawString(mc.font, "R", sx + 14, slotY + 6, surging ? 0xFFFFFFBB : 0xFFFFCC88, false);
        sx += 25;

        // 3. Bore Pitch [Sp]
        int pitchCol = pitch == GeoDrillerEntity.PITCH_LEVEL ? 0xFFFFCC55 : 0xFF66EEFF;
        drawPillBg(gui, sx, slotY, 25, 19, 0x44221C16, pitchCol);
        drawPitchIcon(gui, sx + 2, slotY + 3, pitch, pitchCol);
        gui.drawString(mc.font, "Sp", sx + 12, slotY + 6, pitchCol, false);
        sx += 27;

        // 4. Auto-Pave [B]
        drawPillBg(gui, sx, slotY, 23, 19, paveOn ? 0x55226633 : 0x44221C16, paveOn ? 0xFF55FF77 : 0xFF555555);
        drawPaveIcon(gui, sx + 2, slotY + 3, paveOn ? 0xFF77FF99 : 0xFF777777);
        gui.drawString(mc.font, "B", sx + 14, slotY + 6, paveOn ? 0xFFCCFFDD : 0xFF888888, false);

        // Bottom row: Fuel icon + sleek progress bar + compact Bore/Enchant badge
        int botY = y + 25;
        int fuel = driller.getFuel();
        int maxFuel = Math.max(1, driller.getMaxFuel());
        drawFuelIcon(gui, x + 5, botY, fuel > 150 ? 0xFFF5A623 : 0xFFFF4444);

        int barX = x + 15;
        int barW = 46;
        int fillW = (int) (barW * Mth.clamp(fuel / (float) maxFuel, 0.0F, 1.0F));
        gui.fill(barX, botY + 2, barX + barW, botY + 8, 0xFF2B221A);
        gui.fill(barX, botY + 2, barX + fillW, botY + 8, fuel > 150 ? 0xFFF5A623 : 0xFFFF4444);

        StringBuilder badge = new StringBuilder(driller.getBoreLabel());
        if (driller.hasSilkTouch()) badge.append(" S");
        else if (driller.getFortuneLevel() > 0) badge.append(" F").append(driller.getFortuneLevel());
        if (driller.getEfficiencyLevel() > 0) badge.append(" E").append(driller.getEfficiencyLevel());
        gui.drawString(mc.font, badge.toString(), barX + barW + 4, botY + 1, 0xFF88EEFF, false);
    }

    private static void drawPillBg(GuiGraphics g, int x, int y, int w, int h, int fill, int border) {
        g.fill(x, y, x + w, y + h, fill);
        g.fill(x, y, x + w, y + 1, border);
        g.fill(x, y + h - 1, x + w, y + h, border);
    }

    private static void drawDrillIcon(GuiGraphics g, int x, int y, int col) {
        g.fill(x, y + 1, x + 3, y + 9, col);
        g.fill(x + 3, y + 2, x + 6, y + 8, col);
        g.fill(x + 6, y + 3, x + 8, y + 7, col);
        g.fill(x + 8, y + 4, x + 10, y + 6, col);
    }

    private static void drawSurgeIcon(GuiGraphics g, int x, int y, int col) {
        g.fill(x + 4, y + 1, x + 7, y + 4, col);
        g.fill(x + 2, y + 4, x + 8, y + 6, col);
        g.fill(x + 3, y + 6, x + 6, y + 9, col);
    }

    private static void drawPitchIcon(GuiGraphics g, int x, int y, int pitch, int col) {
        if (pitch == GeoDrillerEntity.PITCH_DOWN) {
            g.fill(x + 1, y + 2, x + 3, y + 4, col);
            g.fill(x + 3, y + 4, x + 5, y + 6, col);
            g.fill(x + 5, y + 6, x + 8, y + 9, col);
            g.fill(x + 3, y + 7, x + 8, y + 9, col);
        } else if (pitch == GeoDrillerEntity.PITCH_UP) {
            g.fill(x + 1, y + 7, x + 3, y + 9, col);
            g.fill(x + 3, y + 5, x + 5, y + 7, col);
            g.fill(x + 5, y + 2, x + 8, y + 5, col);
            g.fill(x + 3, y + 2, x + 8, y + 4, col);
        } else {
            g.fill(x + 1, y + 4, x + 7, y + 6, col);
            g.fill(x + 5, y + 2, x + 7, y + 8, col);
            g.fill(x + 7, y + 4, x + 9, y + 6, col);
        }
    }

    private static void drawPaveIcon(GuiGraphics g, int x, int y, int col) {
        g.fill(x + 1, y + 2, x + 5, y + 5, col);
        g.fill(x + 6, y + 2, x + 9, y + 5, col);
        g.fill(x + 1, y + 6, x + 3, y + 9, col);
        g.fill(x + 4, y + 6, x + 9, y + 9, col);
    }



    private static void drawFuelIcon(GuiGraphics g, int x, int y, int col) {
        g.fill(x + 3, y + 1, x + 5, y + 3, col);
        g.fill(x + 2, y + 3, x + 6, y + 6, col);
        g.fill(x + 1, y + 5, x + 7, y + 9, col);
    }

    private static final class DrillerSpinSoundInstance extends AbstractTickableSoundInstance {
        private final GeoDrillerEntity driller;

        DrillerSpinSoundInstance(GeoDrillerEntity driller, SoundEvent sound) {
            super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.driller = driller;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.05F;
            this.pitch = 0.55F;
            updatePosAndMix();
        }

        private void updatePosAndMix() {
            Vec3 fwd = Vec3.directionFromRotation(0.0F, driller.getYRot()).normalize();
            this.x = driller.getX() + fwd.x * 1.8D;
            this.y = driller.getY() + 0.9D;
            this.z = driller.getZ() + fwd.z * 1.8D;
            float sp = Mth.clamp(driller.drillSpool, 0.0F, 1.0F);
            this.volume = Math.max(0.01F, sp * sp * 0.85F);
            this.pitch = 0.42F + sp * (driller.isSurging() ? 0.80F : 0.60F);
        }

        @Override
        public void tick() {
            if (driller == null || !driller.isAlive() || driller.isRemoved() || driller.drillSpool <= 0.015F) {
                this.stop();
                return;
            }
            updatePosAndMix();
        }
    }
}
