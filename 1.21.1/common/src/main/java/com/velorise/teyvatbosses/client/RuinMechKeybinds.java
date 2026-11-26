package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.velorise.teyvatbosses.entity.RuinMechEntity;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.function.IntConsumer;

/**
 * Dedicated client keybindings for triggering all 6 Ruin Guard Boss skills while piloting the Ruin Mech.
 */
public final class RuinMechKeybinds {
    public static final String CATEGORY = "key.categories.velorise_teyvat_bosses";

    public static final KeyMapping SKILL_DOUBLE_CLAP = new KeyMapping("key.velorise_teyvat_bosses.mech_clap",    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping SKILL_MELEE_3HIT  = new KeyMapping("key.velorise_teyvat_bosses.mech_combo",   InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping SKILL_STOMP       = new KeyMapping("key.velorise_teyvat_bosses.mech_stomp",   InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, CATEGORY);
    public static final KeyMapping SKILL_CYCLONE     = new KeyMapping("key.velorise_teyvat_bosses.mech_cyclone", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping SKILL_LEAP_SLAM   = new KeyMapping("key.velorise_teyvat_bosses.mech_leap",    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping SKILL_MISSILES    = new KeyMapping("key.velorise_teyvat_bosses.mech_missile", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);

    public static final KeyMapping[] ALL = {
            SKILL_DOUBLE_CLAP, SKILL_MELEE_3HIT, SKILL_STOMP, SKILL_CYCLONE, SKILL_LEAP_SLAM, SKILL_MISSILES
    };

    private static final int[] SKILL_IDS = {
            RuinMechEntity.SKILL_DOUBLE_CLAP,
            RuinMechEntity.SKILL_MELEE_3HIT,
            RuinMechEntity.SKILL_STOMP,
            RuinMechEntity.SKILL_CYCLONE,
            RuinMechEntity.SKILL_LEAP_SLAM,
            RuinMechEntity.SKILL_MISSILES
    };

    private RuinMechKeybinds() {}

    public static void pollAndDispatch(IntConsumer skillSender) {
        if (RuinMechCameraController.getPilotedMech() == null) {
            // Drain queued clicks when not piloting
            for (KeyMapping km : ALL) {
                while (km.consumeClick()) {}
            }
            return;
        }
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].consumeClick()) {
                skillSender.accept(SKILL_IDS[i]);
                break;
            }
        }
    }

    public static String[] getShortLabels() {
        String[] out = new String[ALL.length];
        for (int i = 0; i < ALL.length; i++) {
            String s = ALL[i].getTranslatedKeyMessage().getString().toUpperCase();
            out[i] = s.length() > 3 ? s.substring(0, 3) : s;
        }
        return out;
    }
}
