package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import com.velorise.teyvatbosses.network.GoldflameQucusaurControlPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client input controller & compact Phlogiston HUD for {@link GoldflameQucusaurMountEntity}:
 * <ul>
 *   <li>Ground: WASD walk, Space = Takeoff, RMB = Heatwave Takeoff.</li>
 *   <li>Air: Space = Ascend, C = Descend, Sprint = Glide Boost, RMB = Golden Dive.</li>
 *   <li>Both: LMB Tap = Fireball Spit, LMB Hold = Golden Flame Breath.</li>
 * </ul>
 */
public final class GoldflameQucusaurClientController {
    private static final ResourceLocation PHLOGISTON_BG =
            TeyvatBossesConstants.id("textures/gui/phlogiston_bar_bg.png");
    private static final ResourceLocation PHLOGISTON_FILL =
            TeyvatBossesConstants.id("textures/gui/phlogiston_bar_fill.png");
    public static final float THIRD_PERSON_ZOOM_GROUND = 6.8F;
    public static final float THIRD_PERSON_ZOOM_FLY = 7.8F;
    public static final float THIRD_PERSON_ZOOM_BOOST = 8.6F;
    private static float smoothZoom = THIRD_PERSON_ZOOM_GROUND;
    private static float prevCamRoll = 0.0F;
    private static float curCamRoll = 0.0F;
    private static Consumer<GoldflameQucusaurControlPayload> packetSender = p -> {};

    private static boolean prevLmb = false;
    private static boolean prevRmb = false;
    private static boolean prevSpace = false;
    private static boolean prevDescend = false;
    private static boolean prevBoost = false;
    private static boolean breathSent = false;
    private static int lmbHoldTicks = 0;

    private GoldflameQucusaurClientController() {}

    public static void setPacketSender(Consumer<GoldflameQucusaurControlPayload> sender) {
        packetSender = sender != null ? sender : p -> {};
    }

    public static GoldflameQucusaurMountEntity getRiddenMount() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof GoldflameQucusaurMountEntity mount) {
            return mount;
        }
        return null;
    }

    public static float getThirdPersonZoom() {
        return smoothZoom;
    }

    public static float[] computeCameraOffsets(float partialTicks) {
        if (getRiddenMount() == null) {
            return new float[]{0.0F, 0.0F, 0.0F};
        }
        float roll = Mth.lerp(partialTicks, prevCamRoll, curCamRoll);
        return new float[]{0.0F, 0.0F, roll};
    }

    public static void tick(Minecraft mc) {
        GoldflameQucusaurMountEntity mount = getRiddenMount();
        if (mount == null || mc.screen != null || !mount.isSaddled()) {
            if (breathSent) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_STOP));
                breathSent = false;
            }
            prevLmb = prevRmb = prevSpace = prevDescend = prevBoost = false;
            lmbHoldTicks = 0;
            smoothZoom = THIRD_PERSON_ZOOM_GROUND;
            prevCamRoll = curCamRoll = 0.0F;
            return;
        }

        // Smooth 3rd-person dragon camera zoom (Ice and Fire / Dragon Mounts style) & subtle bank roll
        float targetZoom = mount.isFlying()
                ? (mount.isBoosting() ? THIRD_PERSON_ZOOM_BOOST : THIRD_PERSON_ZOOM_FLY)
                : THIRD_PERSON_ZOOM_GROUND;
        smoothZoom = Mth.lerp(0.14F, smoothZoom, targetZoom);
        prevCamRoll = curCamRoll;
        boolean firstPerson = mc.options.getCameraType().isFirstPerson();
        float rollFactor = firstPerson ? 0.42F : 0.15F;
        float targetRoll = mount.isFlying() ? -mount.getBankRoll() * rollFactor : 0.0F;
        curCamRoll = Mth.lerp(0.18F, curCamRoll, targetRoll);

        long win = mc.getWindow().getWindow();
        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean space = mc.options.keyJump.isDown();
        boolean descend = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS;
        boolean boost = mc.options.keySprint.isDown();

        // Apply live flight inputs directly on client mount so travel() responds immediately!
        mount.setClientFlightInputs(space, descend, boost);

        // Space: Takeoff from ground, Ascend while flying
        if (space && !prevSpace) {
            if (!mount.isFlying()) {
                mount.triggerClientTakeoff();
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_SPACE_TAKEOFF));
            }
            packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_ASCEND_START));
        } else if (!space && prevSpace) {
            packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_ASCEND_STOP));
        }

        // C key: Descend (keeps Shift free for dismount)
        if (descend != prevDescend) {
            packetSender.accept(new GoldflameQucusaurControlPayload(
                    descend ? GoldflameQucusaurControlPayload.ACTION_DESCEND_START
                            : GoldflameQucusaurControlPayload.ACTION_DESCEND_STOP));
        }

        // Sprint: Glide Boost in air
        if (boost != prevBoost) {
            packetSender.accept(new GoldflameQucusaurControlPayload(
                    boost ? GoldflameQucusaurControlPayload.ACTION_BOOST_START
                          : GoldflameQucusaurControlPayload.ACTION_BOOST_STOP));
        }

        // LMB: Tap (<6 ticks) = Fireball Spit, Hold (>=6 ticks) = Continuous Golden Flame Breath
        if (lmb) {
            lmbHoldTicks++;
            if (lmbHoldTicks == 6 && !breathSent) {
                breathSent = true;
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_START));
            }
        } else if (prevLmb) {
            if (breathSent) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_STOP));
                breathSent = false;
            } else if (lmbHoldTicks > 0 && lmbHoldTicks < 6) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_FIREBALL_SPIT));
            }
            lmbHoldTicks = 0;
        }

        // RMB: Golden Dive (air) / Heatwave Takeoff (ground)
        if (rmb && !prevRmb) {
            packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_RMB_SKILL));
        }

        prevLmb = lmb;
        prevRmb = rmb;
        prevSpace = space;
        prevDescend = descend;
        prevBoost = boost;
    }

    public static void renderHud(GuiGraphics gui) {
        GoldflameQucusaurMountEntity mount = getRiddenMount();
        if (mount == null) return;
        Minecraft mc = Minecraft.getInstance();
        int h = gui.guiHeight();
        int barW = 136;
        int barH = 14;
        int x = 10;
        int y = h - 54;

        Component title = Component.translatable("hud.velorise_teyvat_bosses.qucusaur_phlogiston",
                (int) mount.getPhlogiston(), (int) GoldflameQucusaurMountEntity.MAX_PHLOGISTON);
        String line1 = mount.isFlying() ? "[Space/C] Up/Down  [Ctrl] Boost" : "[Space] Fly  [RMB] Heatwave";
        String line2 = mount.isFlying() ? "[LMB] Fire/Breath  [RMB] Dive" : "[LMB] Fire/Breath  [Shift] Dismount";

        int textW = Math.max(barW, Math.max(mc.font.width(line1), mc.font.width(line2)));
        int panelW = Math.min(textW + 10, Math.max(barW + 10, (gui.guiWidth() / 2) - 102));

        // Clean compact dark panel that never overlaps the centered vanilla hotbar
        gui.fill(x - 4, y - 4, x + panelW, y + 48, 0xB8180A08);
        gui.fill(x - 4, y - 4, x + panelW, y - 3, 0xCCCE8A1C);
        gui.fill(x - 4, y + 47, x + panelW, y + 48, 0xCC8A4C14);

        gui.drawString(mc.font, title, x, y, 0xFFFFD868, true);

        // Custom Natlan Phlogiston bar texture
        gui.blit(PHLOGISTON_BG, x, y + 11, 0, 0, barW, barH, barW, barH);
        float pct = Math.max(0.0F, Math.min(1.0F, mount.getPhlogiston() / GoldflameQucusaurMountEntity.MAX_PHLOGISTON));
        int fillW = (int) (barW * pct);
        if (fillW > 3) {
            gui.blit(PHLOGISTON_FILL, x, y + 11, 0, 0, fillW, barH, barW, barH);
        }

        gui.drawString(mc.font, line1, x, y + 28, 0xFFF2DFC4, true);
        gui.drawString(mc.font, line2, x, y + 38, 0xFFD9C0A3, true);
    }
}
