package com.velorise.teyvat.portal.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.velorise.teyvat.portal.CelestiaTransitionPackets;
import com.velorise.teyvat.worldgen.TeyvatDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Client-side 4-phase Celestia Portal transition recreating the Genshin Impact opening sequence:
 * <ol>
 *   <li>{@link Phase#BRIDGE_GLIDE}: Glides down the Celestia bridge above clouds flanked by grand pillars
 *       while server preloads destination chunks ({@code Preparing to load data...}).</li>
 *   <li>{@link Phase#GATE_RISE_AND_OPEN}: Raises {@code celestia_portal_gate}, splits the glowing door seam
 *       ({@code portal_open}), and rushes the camera into the white flash.</li>
 *   <li>{@link Phase#WHITE_ELEMENT_LOADING}: Pure white screen with 7 Elemental icons lighting up left-to-right;
 *       commits the dimension swap invisibly behind the screen.</li>
 *   <li>{@link Phase#FADE_OUT}: Smoothly fades white to transparent revealing Starfell Beach.</li>
 * </ol>
 */
public class CelestiaTransitionScreen extends Screen {
    public enum Phase {
        BRIDGE_GLIDE,
        GATE_RISE_AND_OPEN,
        WHITE_ELEMENT_LOADING,
        FADE_OUT
    }

    // Textures extracted from 02_Special_Scenes.zip -> shared-resources/assets/veloriseteyvat/textures/celestia/
    public static final ResourceLocation TEX_BRIDGE = TeyvatDimensions.id("textures/celestia/celestia_bridge_segment.png");
    public static final ResourceLocation TEX_GATE = TeyvatDimensions.id("textures/celestia/celestia_portal_gate.png");
    public static final ResourceLocation TEX_PILLAR = TeyvatDimensions.id("textures/celestia/celestia_grand_pillar.png");
    public static final ResourceLocation TEX_ARCHWAY = TeyvatDimensions.id("textures/celestia/celestia_distant_archway.png");
    public static final ResourceLocation TEX_SPIRE = TeyvatDimensions.id("textures/celestia/celestia_slender_spire.png");

    // 7-element colors (Pyro, Hydro, Anemo, Electro, Dendro, Cryo, Geo) for loading bar
    private static final int[] ELEMENT_COLORS = {
            0xFFEF6C45, // Pyro
            0xFF42A5F5, // Hydro
            0xFF66D1B8, // Anemo
            0xFFAB6CE8, // Electro
            0xFF7CB342, // Dendro
            0xFF80DEEA, // Cryo
            0xFFF2B63D  // Geo
    };
    private static final String[] ELEMENT_LABELS = {"P", "H", "A", "E", "D", "C", "G"};

    private final ResourceLocation targetDimension;
    private final boolean fullBridgeCutscene;

    private Phase phase;
    private float phaseElapsedSec = 0.0f;
    private float totalElapsedSec = 0.0f;
    private float preloadProgress = 0.0f;
    private boolean serverReady = false;
    private boolean commitSent = false;
    private long lastFrameNanos = System.nanoTime();

    public CelestiaTransitionScreen(ResourceLocation targetDimension, boolean fullBridgeCutscene) {
        super(Component.translatable("screen.veloriseteyvat.celestia_transition"));
        this.targetDimension = targetDimension;
        this.fullBridgeCutscene = fullBridgeCutscene;
        this.phase = fullBridgeCutscene ? Phase.BRIDGE_GLIDE : Phase.WHITE_ELEMENT_LOADING;
    }

    public void updatePreloadProgress(float progress, boolean ready) {
        this.preloadProgress = Math.max(this.preloadProgress, Mth.clamp(progress, 0.0f, 1.0f));
        this.serverReady = this.serverReady || ready;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Click during BRIDGE_GLIDE once server is ready (or after 1.5s) advances directly to gate open
        if (phase == Phase.BRIDGE_GLIDE && (serverReady || phaseElapsedSec > 1.5f)) {
            enterPhase(Phase.GATE_RISE_AND_OPEN);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void enterPhase(Phase next) {
        this.phase = next;
        this.phaseElapsedSec = 0.0f;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (now - lastFrameNanos) / 1_000_000_000.0f);
        lastFrameNanos = now;
        phaseElapsedSec += dt;
        totalElapsedSec += dt;

        advanceStateMachine();

        int w = this.width;
        int h = this.height;

        switch (phase) {
            case BRIDGE_GLIDE -> renderBridgeScene(gui, w, h, 0.0f, 0.0f);
            case GATE_RISE_AND_OPEN -> {
                float rise = Mth.clamp(phaseElapsedSec / 0.9f, 0.0f, 1.0f);
                float open = Mth.clamp((phaseElapsedSec - 0.85f) / 1.15f, 0.0f, 1.0f);
                renderBridgeScene(gui, w, h, rise, open);
                if (open > 0.35f) {
                    float flashAlpha = Mth.clamp((open - 0.35f) / 0.65f, 0.0f, 1.0f);
                    fillAlpha(gui, w, h, 0xFFFFFF, flashAlpha);
                }
            }
            case WHITE_ELEMENT_LOADING -> renderWhiteElementLoading(gui, w, h);
            case FADE_OUT -> {
                float alpha = 1.0f - Mth.clamp(phaseElapsedSec / 0.85f, 0.0f, 1.0f);
                fillAlpha(gui, w, h, 0xFFFFFF, alpha);
            }
        }
    }

    private void advanceStateMachine() {
        Minecraft mc = Minecraft.getInstance();
        switch (phase) {
            case BRIDGE_GLIDE -> {
                if ((serverReady && phaseElapsedSec >= 3.2f) || phaseElapsedSec >= 5.5f) {
                    enterPhase(Phase.GATE_RISE_AND_OPEN);
                }
            }
            case GATE_RISE_AND_OPEN -> {
                if (phaseElapsedSec >= 2.15f) {
                    enterPhase(Phase.WHITE_ELEMENT_LOADING);
                }
            }
            case WHITE_ELEMENT_LOADING -> {
                if (!commitSent && phaseElapsedSec >= 0.15f) {
                    commitSent = true;
                    CelestiaTransitionPackets.bridge().sendToServer(
                            new CelestiaTransitionPackets.CommitTeleportPayload(targetDimension)
                    );
                }
                boolean arrived = mc.level != null && mc.level.dimension().location().equals(targetDimension);
                if ((arrived && phaseElapsedSec >= 1.8f) || phaseElapsedSec >= 6.0f) {
                    enterPhase(Phase.FADE_OUT);
                }
            }
            case FADE_OUT -> {
                if (phaseElapsedSec >= 0.85f) {
                    mc.setScreen(null);
                }
            }
        }
    }

    /**
     * Renders the stylized Celestia bridge & pillar horizon.
     * Hook your 3D PoseStack renderer for the 5 exported {@code celestia_*.bbmodel} meshes here
     * to replace/augment the perspective silhouette layer.
     */
    private void renderBridgeScene(GuiGraphics gui, int w, int h, float gateRise, float gateOpen) {
        // Night-sky Celestia gradient (matches open.mp4)
        gui.fillGradient(0, 0, w, h, 0xFF0B1C3D, 0xFF28559A);
        // Glowing cloud horizon band
        int horizonY = (int) (h * 0.56f);
        gui.fillGradient(0, horizonY - 40, w, h, 0x004B84D6, 0xCC9AD0FF);

        // Perspective pillars flanking the bridge (parallax scroll with totalElapsedSec)
        float scroll = (totalElapsedSec * 0.45f) % 1.0f;
        for (int i = 0; i < 6; i++) {
            float depth = Mth.clamp((i + (1.0f - scroll)) / 6.0f, 0.08f, 1.0f);
            int spread = (int) (w * 0.14f + (1.0f - depth) * w * 0.34f);
            int pillarW = Math.max(10, (int) ((1.0f - depth) * 44));
            int pillarH = (int) ((1.0f - depth) * h * 0.72f);
            int py = horizonY - pillarH / 2;
            int shade = 0xFF152A54;
            gui.fill(w / 2 - spread - pillarW, py, w / 2 - spread, py + pillarH, shade);
            gui.fill(w / 2 + spread, py, w / 2 + spread + pillarW, py + pillarH, shade);
        }

        // Perspective Celestia stone bridge deck extending toward horizon
        int bridgeTopW = (int) (w * 0.12f);
        int bridgeBotW = (int) (w * 0.46f);
        for (int y = horizonY + 12; y < h; y += 3) {
            float t = (float) (y - horizonY) / Math.max(1, h - horizonY);
            int halfW = (int) Mth.lerp(t, bridgeTopW / 2.0f, bridgeBotW / 2.0f);
            int col = ((int) ((y + totalElapsedSec * 55) / 18) % 2 == 0) ? 0xFF5A86C8 : 0xFF476FA8;
            gui.fill(w / 2 - halfW, y, w / 2 + halfW, y + 3, col);
        }

        // Celestia Portal Gate rising & splitting open at the end of the bridge
        if (gateRise > 0.01f) {
            int gateW = (int) (w * 0.14f * (1.0f + gateOpen * 0.9f));
            int gateH = (int) (h * 0.34f * (1.0f + gateOpen * 0.9f));
            int gx = w / 2 - gateW / 2;
            int gy = (int) Mth.lerp(gateRise, horizonY + 40, horizonY - gateH + 22);

            // Outer stone arch frame
            gui.fill(gx - 6, gy - 6, gx + gateW + 6, gy + gateH, 0xFF2B436E);
            // Glowing doorway seam widening as portal_open progresses
            int seamHalf = Math.max(2, (int) (gateW * 0.48f * gateOpen));
            gui.fill(w / 2 - seamHalf, gy, w / 2 + seamHalf, gy + gateH, 0xFFE8FAFF);
        }

        // Bottom Genshin-style progress bar during BRIDGE_GLIDE
        if (phase == Phase.BRIDGE_GLIDE) {
            float shownPct = Math.max(preloadProgress, Mth.clamp(phaseElapsedSec / 3.2f, 0.0f, 0.95f));
            String label = String.format("Preparing to load Teyvat... %.0f%%", shownPct * 100.0f);
            gui.drawCenteredString(this.font, label, w / 2, h - 38, 0xFFEAF4FF);

            int barW = Math.min(260, w - 80);
            int bx = w / 2 - barW / 2;
            int by = h - 22;
            gui.fill(bx, by, bx + barW, by + 4, 0x66FFFFFF);
            gui.fill(bx, by, bx + (int) (barW * shownPct), by + 4, 0xFFFFFFFF);
        }
    }

    /**
     * Pure white loading screen with 7 Elemental icons illuminating sequentially left-to-right.
     */
    private void renderWhiteElementLoading(GuiGraphics gui, int w, int h) {
        gui.fill(0, 0, w, h, 0xFFFFFFFF);

        float fillProgress = Mth.clamp(phaseElapsedSec / 1.7f, 0.0f, 1.0f) * 7.0f;
        int iconSize = 18;
        int gap = 10;
        int totalW = 7 * iconSize + 6 * gap;
        int startX = w / 2 - totalW / 2;
        int y = h / 2 - iconSize / 2;

        RenderSystem.enableBlend();
        for (int i = 0; i < 7; i++) {
            int ix = startX + i * (iconSize + gap);
            float lit = Mth.clamp(fillProgress - i, 0.0f, 1.0f);
            int bg = lit > 0.05f ? blendColor(0xFFE5E7EB, ELEMENT_COLORS[i], lit) : 0xFFE5E7EB;
            gui.fill(ix, y, ix + iconSize, y + iconSize, bg);
            gui.drawCenteredString(this.font, ELEMENT_LABELS[i], ix + iconSize / 2, y + 5,
                    lit > 0.5f ? 0xFFFFFFFF : 0xFFB0B7C3);
        }
    }

    private static void fillAlpha(GuiGraphics gui, int w, int h, int rgb, float alpha) {
        int a = Mth.clamp((int) (alpha * 255.0f), 0, 255);
        gui.fill(0, 0, w, h, (a << 24) | (rgb & 0xFFFFFF));
    }

    private static int blendColor(int c0, int c1, float t) {
        int r = (int) Mth.lerp(t, (c0 >> 16) & 0xFF, (c1 >> 16) & 0xFF);
        int g = (int) Mth.lerp(t, (c0 >> 8) & 0xFF, (c1 >> 8) & 0xFF);
        int b = (int) Mth.lerp(t, c0 & 0xFF, c1 & 0xFF);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
