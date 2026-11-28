package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.GoldflameQucusaurMountEntity;
import com.velorise.teyvatbosses.network.GoldflameQucusaurControlPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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

    public static void tick(Minecraft mc) {
        GoldflameQucusaurMountEntity mount = getRiddenMount();
        if (mount == null || mc.screen != null || !mount.isSaddled()) {
            if (breathSent) {
                packetSender.accept(new GoldflameQucusaurControlPayload(GoldflameQucusaurControlPayload.ACTION_BREATH_STOP));
                breathSent = false;
            }
            prevLmb = prevRmb = prevSpace = prevDescend = prevBoost = false;
            lmbHoldTicks = 0;
            return;
        }

        long win = mc.getWindow().getWindow();
        boolean lmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rmb = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean space = mc.options.keyJump.isDown();
        boolean descend = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS;
        boolean boost = mc.options.keySprint.isDown();

        // Space: Takeoff from ground, Ascend while flying
        if (space && !prevSpace) {
            if (!mount.isFlying()) {
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
        int x = 10;
        int y = h - 46;
        int barW = 128;

        gui.fill(x - 3, y - 3, x + barW + 3, y + 36, 0xAA1A0A08);
        gui.drawString(mc.font, Component.translatable("hud.velorise_teyvat_bosses.qucusaur_phlogiston",
                (int) mount.getPhlogiston(), (int) GoldflameQucusaurMountEntity.MAX_PHLOGISTON), x, y, 0xFFFFD060, true);

        int fillW = (int) (barW * (mount.getPhlogiston() / GoldflameQucusaurMountEntity.MAX_PHLOGISTON));
        gui.fill(x, y + 11, x + barW, y + 18, 0xFF3A1610);
        gui.fill(x, y + 11, x + fillW, y + 18, 0xFFFF9A1F);

        Component hint = mount.isFlying()
                ? Component.translatable("hud.velorise_teyvat_bosses.qucusaur_hint_air")
                : Component.translatable("hud.velorise_teyvat_bosses.qucusaur_hint_ground");
        gui.drawString(mc.font, hint, x, y + 22, 0xFFE8D8C8, true);
    }
}
