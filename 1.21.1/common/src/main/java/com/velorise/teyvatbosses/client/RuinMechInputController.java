package com.velorise.teyvatbosses.client;

import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import java.util.function.IntConsumer;

/**
 * Compact LMB + RMB gesture controller (Tap / Double-Tap / Hold) + Space Mobility Leap + MMB Lock-On:
 * <ul>
 *   <li><b>LMB Tap:</b> Double Clap</li>
 *   <li><b>LMB Double-Tap:</b> 3-Hit Heavy Combo</li>
 *   <li><b>LMB Hold (0.45s):</b> Cyclone Whirlwind Spin</li>
 *   <li><b>RMB Tap:</b> Stomp Shockwave</li>
 *   <li><b>RMB Double-Tap:</b> Leap Slam</li>
 *   <li><b>RMB Hold (0.45s):</b> 6x Homing Missile Barrage</li>
 *   <li><b>Space:</b> Mobility Long Jump (no combat slam)</li>
 *   <li><b>MMB:</b> Toggle Hard Lock-On</li>
 * </ul>
 */
public final class RuinMechInputController {
    private static final int HOLD_TICKS = 9;
    private static final int DOUBLE_TAP_WINDOW = 6;

    private static final ButtonTracker LMB = new ButtonTracker(
            RuinMechEntity.SKILL_DOUBLE_CLAP,
            RuinMechEntity.SKILL_MELEE_3HIT,
            RuinMechEntity.SKILL_CYCLONE
    );

    private static final ButtonTracker RMB = new ButtonTracker(
            RuinMechEntity.SKILL_STOMP,
            RuinMechEntity.SKILL_LEAP_SLAM,
            RuinMechEntity.SKILL_MISSILES
    );

    public static final String[] HUD_BADGES = {"L1", "L2", "L+", "R1", "R2", "R+"};
    private static boolean mmbWasDown = false;
    private static boolean spaceWasDown = false;
    private static boolean spaceConsumed = false;
    private static int spaceHoldTicks = 0;

    private RuinMechInputController() {}

    public static void tickAndDispatch(IntConsumer skillSender) {
        Minecraft mc = Minecraft.getInstance();
        if (RuinMechCameraController.getPilotedMech() == null || mc.screen != null) {
            LMB.reset();
            RMB.reset();
            mmbWasDown = false;
            spaceWasDown = false;
            spaceConsumed = false;
            spaceHoldTicks = 0;
            return;
        }
        boolean mmbDown = mc.options.keyPickItem.isDown();
        if (mmbDown && !mmbWasDown) {
            skillSender.accept(RuinMechEntity.SKILL_TOGGLE_LOCK);
        }
        mmbWasDown = mmbDown;

        boolean spaceDown = mc.options.keyJump.isDown();
        if (spaceDown && !spaceWasDown) {
            spaceConsumed = false;
            spaceHoldTicks = 1;
        } else if (spaceDown) {
            if (!spaceConsumed) {
                spaceHoldTicks++;
                if (spaceHoldTicks >= HOLD_TICKS) {
                    spaceConsumed = true;
                    skillSender.accept(RuinMechEntity.SKILL_SUPER_LEAP);
                }
            }
        } else if (spaceWasDown) {
            if (!spaceConsumed) {
                skillSender.accept(RuinMechEntity.SKILL_BOOST_LEAP);
            }
            spaceHoldTicks = 0;
            spaceConsumed = false;
        }
        spaceWasDown = spaceDown;

        LMB.update(mc.options.keyAttack.isDown(), skillSender);
        RMB.update(mc.options.keyUse.isDown(), skillSender);
    }

    public static float getLmbHoldProgress() {
        return LMB.getHoldProgress();
    }

    public static float getRmbHoldProgress() {
        return RMB.getHoldProgress();
    }

    public static float getSpaceHoldProgress() {
        if (!spaceWasDown || spaceConsumed || spaceHoldTicks <= 1) return 0.0F;
        return Mth.clamp((float) spaceHoldTicks / (float) HOLD_TICKS, 0.0F, 1.0F);
    }

    private static final class ButtonTracker {
        private final int tapSkill;
        private final int doubleTapSkill;
        private final int holdSkill;

        private boolean wasDown = false;
        private boolean consumed = false;
        private int holdTicks = 0;
        private int pendingTapTimer = 0;

        ButtonTracker(int tapSkill, int doubleTapSkill, int holdSkill) {
            this.tapSkill = tapSkill;
            this.doubleTapSkill = doubleTapSkill;
            this.holdSkill = holdSkill;
        }

        void reset() {
            wasDown = false;
            consumed = false;
            holdTicks = 0;
            pendingTapTimer = 0;
        }

        float getHoldProgress() {
            if (!wasDown || consumed || holdTicks <= 1) return 0.0F;
            return Mth.clamp((float) holdTicks / (float) HOLD_TICKS, 0.0F, 1.0F);
        }

        void update(boolean isDown, IntConsumer sender) {
            if (isDown && !wasDown) {
                if (pendingTapTimer > 0) {
                    pendingTapTimer = 0;
                    consumed = true;
                    holdTicks = 0;
                    sender.accept(doubleTapSkill);
                } else {
                    consumed = false;
                    holdTicks = 1;
                }
            } else if (isDown) {
                if (!consumed) {
                    holdTicks++;
                    if (holdTicks >= HOLD_TICKS) {
                        consumed = true;
                        pendingTapTimer = 0;
                        sender.accept(holdSkill);
                    }
                }
            } else if (wasDown) {
                if (!consumed) {
                    pendingTapTimer = DOUBLE_TAP_WINDOW;
                }
                holdTicks = 0;
                consumed = false;
            } else {
                if (pendingTapTimer > 0) {
                    pendingTapTimer--;
                    if (pendingTapTimer == 0) {
                        sender.accept(tapSkill);
                    }
                }
            }
            wasDown = isDown;
        }
    }
}
