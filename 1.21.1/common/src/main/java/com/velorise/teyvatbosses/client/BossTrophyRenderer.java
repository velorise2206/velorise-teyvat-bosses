package com.velorise.teyvatbosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.block.BossTrophyBlock;
import com.velorise.teyvatbosses.block.BossTrophyBlockEntity;
import com.velorise.teyvatbosses.client.model.AnemoHypostasisModel;
import com.velorise.teyvatbosses.client.model.CryoRegisvineAnimations;
import com.velorise.teyvatbosses.client.model.CryoRegisvineModel;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapAnimations;
import com.velorise.teyvatbosses.client.model.PrimoGeovishapModel;
import com.velorise.teyvatbosses.client.model.RuinGuardAnimations;
import com.velorise.teyvatbosses.client.model.RuinGuardModel;
import com.velorise.teyvatbosses.client.model.RuinNautilusAnimations;
import com.velorise.teyvatbosses.client.model.RuinNautilusModel;
import com.velorise.teyvatbosses.entity.BossRespawnAnchorEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

/**
 * Renders 3D Miniature Boss Trophies as zero-lag static statues on a gilded pedestal.
 */
public class BossTrophyRenderer implements BlockEntityRenderer<BossTrophyBlockEntity> {

    private static final ResourceLocation TEX_CRYO = TeyvatBossesConstants.id("textures/entity/cryo_regisvine_boss.png");
    private static final ResourceLocation TEX_ANEMO = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis.png");
    private static final ResourceLocation TEX_ANEMO_EM = TeyvatBossesConstants.id("textures/entity/anemo_hypostasis_emissive.png");
    private static final ResourceLocation TEX_RUIN = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss.png");
    private static final ResourceLocation TEX_RUIN_EM = TeyvatBossesConstants.id("textures/entity/ruin_guard_boss_emissive.png");
    private static final ResourceLocation TEX_NAUTILUS = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss.png");
    private static final ResourceLocation TEX_NAUTILUS_EM = TeyvatBossesConstants.id("textures/entity/ruin_nautilus_boss_emissive.png");
    private static final ResourceLocation TEX_PRIMO = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss.png");
    private static final ResourceLocation TEX_PRIMO_EM = TeyvatBossesConstants.id("textures/entity/primo_geovishap_boss_emissive.png");

    private final CryoRegisvineModel<Entity> cryoModel;
    private final AnemoHypostasisModel<Entity> anemoModel;
    private final RuinGuardModel<Entity> ruinModel;
    private final RuinNautilusModel<Entity> nautilusModel;
    private final PrimoGeovishapModel<Entity> primoModel;

    public BossTrophyRenderer(BlockEntityRendererProvider.Context ctx) {
        this.cryoModel = new CryoRegisvineModel<>(ctx.bakeLayer(CryoRegisvineModel.LAYER_LOCATION));
        this.anemoModel = new AnemoHypostasisModel<>(ctx.bakeLayer(AnemoHypostasisModel.LAYER_LOCATION));
        this.ruinModel = new RuinGuardModel<>(ctx.bakeLayer(RuinGuardModel.LAYER_LOCATION));
        this.nautilusModel = new RuinNautilusModel<>(ctx.bakeLayer(RuinNautilusModel.LAYER_LOCATION));
        this.primoModel = new PrimoGeovishapModel<>(ctx.bakeLayer(PrimoGeovishapModel.LAYER_LOCATION));

        CryoRegisvineAnimations.apply(cryoModel, "idle_combat", 0.0F);
        anemoModel.resetPose();
        RuinGuardAnimations.apply(ruinModel, "idle_combat", 0.0F);
        RuinNautilusAnimations.apply(nautilusModel, "idle_hover", 0.0F, false);
        if (nautilusModel.getBone("sub_cockpit_glass") != null) nautilusModel.getBone("sub_cockpit_glass").visible = false;
        PrimoGeovishapAnimations.apply(primoModel, "idle", 0.0F);
    }

    @Override
    public void render(BossTrophyBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buf, int packedLight, int packedOverlay) {
        if (!(be.getBlockState().getBlock() instanceof BossTrophyBlock block)) return;
        int kind = block.getBossKind();
        Direction facing = be.getBlockState().getValue(BossTrophyBlock.FACING);
        float rotY = -facing.toYRot();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        drawPedestal(poseStack, buf, kind);

        poseStack.translate(0.0D, 0.24D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));

        switch (kind) {
            case BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE -> {
                poseStack.scale(-0.16F, -0.16F, 0.16F);
                poseStack.translate(0.0D, -1.501D, 0.0D);
                cryoModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEX_CRYO)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
            case BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS -> {
                poseStack.translate(0.0D, 0.34D, 0.0D);
                poseStack.scale(-0.28F, -0.28F, 0.28F);
                poseStack.translate(0.0D, -1.501D, 0.0D);
                anemoModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEX_ANEMO)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                anemoModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(TEX_ANEMO_EM)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
            case BossRespawnAnchorEntity.BOSS_RUIN_GUARD -> {
                poseStack.scale(-0.24F, -0.24F, 0.24F);
                poseStack.translate(0.0D, -1.501D, 0.0D);
                ruinModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEX_RUIN)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                ruinModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(TEX_RUIN_EM)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
            case BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS -> {
                poseStack.translate(0.0D, 0.24D, 0.0D);
                poseStack.scale(-0.24F, -0.24F, 0.24F);
                poseStack.translate(0.0D, -1.501D, 0.0D);
                nautilusModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEX_NAUTILUS)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                nautilusModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(TEX_NAUTILUS_EM)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
            default -> { // Primo Geovishap
                poseStack.scale(-0.18F, -0.18F, 0.18F);
                poseStack.translate(0.0D, -1.501D, 0.0D);
                primoModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.entityCutoutNoCull(TEX_PRIMO)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
                primoModel.renderToBuffer(poseStack, buf.getBuffer(RenderType.eyes(TEX_PRIMO_EM)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            }
        }
        poseStack.popPose();
    }

    private static void drawPedestal(PoseStack poseStack, MultiBufferSource buf, int kind) {
        VertexConsumer vc = buf.getBuffer(RenderType.lightning());
        Matrix4f m = poseStack.last().pose();
        int[] accent = switch (kind) {
            case BossRespawnAnchorEntity.BOSS_CRYO_REGISVINE -> new int[]{110, 225, 255};
            case BossRespawnAnchorEntity.BOSS_ANEMO_HYPOSTASIS -> new int[]{105, 250, 210};
            case BossRespawnAnchorEntity.BOSS_RUIN_GUARD -> new int[]{255, 175, 55};
            case BossRespawnAnchorEntity.BOSS_RUIN_NAUTILUS -> new int[]{70, 195, 255};
            default -> new int[]{245, 195, 65};
        };
        drawBox(vc, m, -0.36F, 0.00F, -0.36F, 0.36F, 0.14F, 0.36F, 48, 44, 52, 235);
        drawBox(vc, m, -0.30F, 0.14F, -0.30F, 0.30F, 0.22F, 0.30F, 68, 62, 74, 235);
        drawBox(vc, m, -0.32F, 0.21F, -0.32F, 0.32F, 0.24F, 0.32F, accent[0], accent[1], accent[2], 220);
    }

    private static void drawBox(VertexConsumer vc, Matrix4f m, float x0, float y0, float z0, float x1, float y1, float z1, int r, int g, int b, int a) {
        vc.addVertex(m, x0, y1, z0).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z0).setColor(r, g, b, a);
        vc.addVertex(m, x0, y0, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y0, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y0, z0).setColor(r, g, b, a);
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z0).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z0).setColor(r, g, b, a);
        vc.addVertex(m, x1, y0, z1).setColor(r, g, b, a);
        vc.addVertex(m, x1, y0, z0).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z0).setColor(r, g, b, a);
        vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a);
        vc.addVertex(m, x0, y0, z1).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z1).setColor(r, g, b, a);
        vc.addVertex(m, x0, y1, z0).setColor(r, g, b, a);
    }
}
