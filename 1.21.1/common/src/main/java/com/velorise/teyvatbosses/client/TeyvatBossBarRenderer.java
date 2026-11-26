package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Custom Cataclysm-inspired layered boss bar renderer for all 5 Teyvat Bosses.
 * Uses 214x23 ornamental background + overlay bezels centered cleanly around
 * vanilla's 182x5 progress bar slot, preserving crisp readability and row spacing.
 */
public final class TeyvatBossBarRenderer {

    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int FRAME_WIDTH = 214;
    private static final int FRAME_HEIGHT = 23;
    private static final int FRAME_PAD_X = 16; // (214 - 182) / 2
    private static final int FRAME_PAD_Y = 9;  // (23 - 5) / 2
    private static final int TOP_CLEARANCE = 5;
    private static final int ROW_INCREMENT = 28;

    private static final String KEY_CRYO_REGISVINE = "entity.velorise_teyvat_bosses.cryo_regisvine";
    private static final String KEY_ANEMO_HYPOSTASIS = "entity.velorise_teyvat_bosses.anemo_hypostasis";
    private static final String KEY_RUIN_GUARD = "entity.velorise_teyvat_bosses.ruin_guard";
    private static final String KEY_RUIN_NAUTILUS = "entity.velorise_teyvat_bosses.ruin_nautilus";
    private static final String KEY_PRIMO_GEOVISHAP = "entity.velorise_teyvat_bosses.primo_geovishap";

    private static final ResourceLocation PRIMO_HYDRO_FILL = tex("primo_geovishap_hydro_fill.png");
    private static final ResourceLocation PRIMO_PYRO_FILL = tex("primo_geovishap_pyro_fill.png");
    private static final ResourceLocation PRIMO_CRYO_FILL = tex("primo_geovishap_cryo_fill.png");
    private static final ResourceLocation PRIMO_ELECTRO_FILL = tex("primo_geovishap_electro_fill.png");

    private static final Map<String, BossBarStyle> STYLES = Map.of(
            KEY_CRYO_REGISVINE, new BossBarStyle(
                    tex("cryo_regisvine_bg.png"),
                    tex("cryo_regisvine_fill.png"),
                    tex("cryo_regisvine_overlay.png"),
                    0xFFE4F6FF
            ),
            KEY_ANEMO_HYPOSTASIS, new BossBarStyle(
                    tex("anemo_hypostasis_bg.png"),
                    tex("anemo_hypostasis_fill.png"),
                    tex("anemo_hypostasis_overlay.png"),
                    0xFFE0FFF1
            ),
            KEY_RUIN_GUARD, new BossBarStyle(
                    tex("ruin_guard_bg.png"),
                    tex("ruin_guard_fill.png"),
                    tex("ruin_guard_overlay.png"),
                    0xFFFFECC4
            ),
            KEY_RUIN_NAUTILUS, new BossBarStyle(
                    tex("ruin_nautilus_bg.png"),
                    tex("ruin_nautilus_fill.png"),
                    tex("ruin_nautilus_overlay.png"),
                    0xFFD8EEFF
            ),
            KEY_PRIMO_GEOVISHAP, new BossBarStyle(
                    tex("primo_geovishap_bg.png"),
                    tex("primo_geovishap_fill.png"),
                    tex("primo_geovishap_overlay.png"),
                    0xFFFFF0D4
            )
    );

    private TeyvatBossBarRenderer() {}

    private static ResourceLocation tex(String file) {
        return TeyvatBossesConstants.id("textures/gui/bossbars/" + file);
    }

    public static boolean isTeyvatBoss(BossEvent event) {
        return findBossKey(event.getName()) != null;
    }

    /**
     * Renders the custom boss bar if {@code event} belongs to one of the 5 Teyvat Bosses.
     *
     * @return the vertical Y increment to advance to the next bar (28), or {@code 0} if not a Teyvat Boss.
     */
    public static int tryRender(GuiGraphics gui, LerpingBossEvent event, int vanillaBarX, int vanillaBarY) {
        String key = findBossKey(event.getName());
        if (key == null) return 0;

        BossBarStyle style = STYLES.get(key);
        if (style == null) return 0;

        ResourceLocation fillTex = resolveFillTexture(key, style, event.getColor());
        int titleColor = resolveTitleColor(key, style, event.getColor());

        int titleY = vanillaBarY - 9;
        int barY = vanillaBarY + TOP_CLEARANCE;
        int frameX = vanillaBarX - FRAME_PAD_X;
        int frameY = barY - FRAME_PAD_Y;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gui.blit(style.bgTex(), frameX, frameY, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);

        int fillW = Mth.clamp(Mth.lerpDiscrete(event.getProgress(), 0, BAR_WIDTH), 0, BAR_WIDTH);
        if (fillW > 0) {
            gui.blit(fillTex, vanillaBarX, barY, 0, 0, fillW, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        }

        gui.blit(style.overlayTex(), frameX, frameY, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT);
        RenderSystem.disableBlend();

        Font font = Minecraft.getInstance().font;
        Component name = event.getName();
        int textX = gui.guiWidth() / 2 - font.width(name) / 2;
        gui.drawString(font, name, textX, titleY, titleColor);

        return ROW_INCREMENT;
    }

    private static ResourceLocation resolveFillTexture(String key, BossBarStyle style, BossEvent.BossBarColor color) {
        if (!KEY_PRIMO_GEOVISHAP.equals(key)) {
            return style.defaultFillTex();
        }
        return switch (color) {
            case BLUE -> PRIMO_HYDRO_FILL;
            case RED -> PRIMO_PYRO_FILL;
            case WHITE -> PRIMO_CRYO_FILL;
            case PURPLE -> PRIMO_ELECTRO_FILL;
            default -> style.defaultFillTex();
        };
    }

    private static int resolveTitleColor(String key, BossBarStyle style, BossEvent.BossBarColor color) {
        if (!KEY_PRIMO_GEOVISHAP.equals(key)) {
            return style.titleColor();
        }
        return switch (color) {
            case BLUE -> 0xFFD9F2FF;
            case RED -> 0xFFFFE0CE;
            case WHITE -> 0xFFE8FAFF;
            case PURPLE -> 0xFFF2DCFF;
            default -> style.titleColor();
        };
    }

    @Nullable
    private static String findBossKey(@Nullable Component component) {
        if (component == null) return null;
        if (component.getContents() instanceof TranslatableContents tc) {
            String k = tc.getKey();
            if (STYLES.containsKey(k)) {
                return k;
            }
        }
        for (Component sibling : component.getSiblings()) {
            String nested = findBossKey(sibling);
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private record BossBarStyle(
            ResourceLocation bgTex,
            ResourceLocation defaultFillTex,
            ResourceLocation overlayTex,
            int titleColor
    ) {}
}
