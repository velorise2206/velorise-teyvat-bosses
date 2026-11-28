package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import com.velorise.teyvatbosses.network.GoldflameQucusaurControlPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Client input controller &amp; minimal Phlogiston bar HUD for {@link GoldflameQucusaurMountEntity}:
 * <ul>
 *   <li>Flight: W + Look pitch (up/down), Space/C altitude override, Ctrl boost.</li>
 *   <li>LMB Hold: Continuous Goldflame Breath Torrent (instant, no tap combo).</li>
 *   <li>RMB Tap: Piercing Forward Flame Dash.</li>
 *   <li>RMB Hold (Air Only): Continuous Aimed Golden Drill Dive.</li>
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
    private static boolean aimDiveSent = false;
    private static int rmbHoldTicks = 0;
    private static int lastMountId = -1;
    private static int mountGraceTicks = 0;
    private static boolean waitingForRmbRelease = false;

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
            if (aimDiveSent) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_RMB_AIM_DIVE_STOP));
                aimDiveSent = false;
            }
            prevLmb = prevRmb = prevSpace = prevDescend = prevBoost = false;
            rmbHoldTicks = 0;
            lastMountId = -1;
            mountGraceTicks = 0;
            waitingForRmbRelease = false;
            smoothZoom = THIRD_PERSON_ZOOM_GROUND;
            prevCamRoll = curCamRoll = 0.0F;
            return;
        }

        // Smooth 3rd-person dragon camera zoom & subtle bank roll
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

        // C key: Descend
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

        // LMB: Hold ONLY -> Continuous Golden Flame Breath (no tap combo)
        if (lmb && !breathSent) {
            breathSent = true;
            packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_START));
        } else if (!lmb && breathSent) {
            breathSent = false;
            packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_STOP));
        }

        // Prevent the Right-Click used to mount the dragon from triggering Dash immediately
        if (mount.getId() != lastMountId) {
            lastMountId = mount.getId();
            mountGraceTicks = 8;
            waitingForRmbRelease = true;
            rmbHoldTicks = 0;
            prevRmb = false;
        }
        if (mountGraceTicks > 0) mountGraceTicks--;
        if (waitingForRmbRelease) {
            if (!rmb) waitingForRmbRelease = false;
            rmb = false;
        } else if (mountGraceTicks > 0) {
            rmb = false;
        }

        // RMB: Tap = Forward Flame Dash; Hold (>=7 ticks, AIR ONLY) = Continuous Aimed Drill Dive
        if (rmb) {
            rmbHoldTicks++;
            if (rmbHoldTicks == 7 && !aimDiveSent && mount.isFlying()) {
                aimDiveSent = true;
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_RMB_AIM_DIVE_START));
            }
        } else if (prevRmb) {
            if (aimDiveSent) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_RMB_AIM_DIVE_STOP));
                aimDiveSent = false;
            } else if (rmbHoldTicks > 0 && rmbHoldTicks < 7) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_RMB_DASH));
            }
            rmbHoldTicks = 0;
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
        int h = gui.guiHeight();

        // Sleek Genshin-style slim Phlogiston bar in the bottom-left corner (no box or text)
        int barW = 112;
        int barH = 4;
        int x = 10;
        int y = h - 14;

        // Tapered translucent pill trough (1px inset on top & bottom rows for sleek rounded ends)
        gui.fill(x + 1, y, x + barW - 1, y + barH, 0xAA180B06);
        gui.fill(x, y + 1, x + barW, y + barH - 1, 0xAA180B06);

        float pct = Mth.clamp(mount.getPhlogiston() / GoldflameQucusaurMountEntity.MAX_PHLOGISTON, 0.0F, 1.0F);
        int fillW = (int) (barW * pct);
        if (fillW > 2) {
            // Segmented molten crimson-orange -> warm gold -> bright cream-white gradient fill
            int segs = 12;
            for (int i = 0; i < segs; i++) {
                int sx0 = x + (fillW * i) / segs;
                int sx1 = x + (fillW * (i + 1)) / segs;
                if (sx1 <= sx0) continue;
                float t = (i + 0.5F) / segs;
                int r = (int) Mth.lerp(t, 238, 255);
                int g = (int) Mth.lerp(t, 82, 232);
                int b = (int) Mth.lerp(t, 24, 152);
                int col = 0xFF000000 | (r << 16) | (g << 8) | b;
                int coreCol = 0xFF000000 | (Math.min(255, r + 14) << 16) | (Math.min(255, g + 26) << 8) | Math.min(255, b + 36);
                int c0 = Math.max(x + 1, sx0);
                int c1 = Math.min(x + barW - 1, sx1);
                if (c1 > c0) {
                    gui.fill(c0, y, c1, y + barH, col);
                }
                gui.fill(sx0, y + 1, sx1, y + barH - 1, coreCol);
            }
            // Bright glowing leading spark at the fill edge (Genshin Phlogiston highlight)
            int tipX = Math.min(x + barW - 2, x + fillW - 2);
            gui.fill(tipX, y, tipX + 2, y + barH, 0xFFFFF8D0);
        }
    }
}
