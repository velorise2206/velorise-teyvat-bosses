// Auto-compiled from fixed purple trounce_blossom.bbmodel (41 cubes, 256x256 Box-UV)
package com.velorise.teyvatbosses.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Native Mojang 3D model for Genshin Impact's purple World Boss Trounce Blossom,
 * compiled from the axis-corrected trounce_blossom.bbmodel (twisted ivory-tan roots,
 * emerald calyx sepals, pointed plum-purple lotus bud with cyan veins, and orbiting motes).
 */
public class TrounceBlossomModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(TeyvatBossesConstants.id("trounce_blossom"), "main");

    public final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public TrounceBlossomModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        registerBone("root", rootPart.getChild("root"));
        registerBone("stem_base", rootPart.getChild("root").getChild("stem_base"));
        registerBone("stem_upper", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper"));
        registerBone("flower_head", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head"));
        registerBone("calyx_top", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_top"));
        registerBone("ct_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_top").getChild("ct_base_piv"));
        registerBone("ct_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_top").getChild("ct_tip_piv"));
        registerBone("calyx_bottom", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_bottom"));
        registerBone("cb_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_bottom").getChild("cb_base_piv"));
        registerBone("cb_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_bottom").getChild("cb_tip_piv"));
        registerBone("calyx_left", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_left"));
        registerBone("cl_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_left").getChild("cl_base_piv"));
        registerBone("cl_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_left").getChild("cl_tip_piv"));
        registerBone("calyx_right", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_right"));
        registerBone("cr_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_right").getChild("cr_base_piv"));
        registerBone("cr_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("calyx_right").getChild("cr_tip_piv"));
        registerBone("petal_top", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_top"));
        registerBone("pt_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_top").getChild("pt_base_piv"));
        registerBone("pt_mid_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_top").getChild("pt_mid_piv"));
        registerBone("pt_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_top").getChild("pt_tip_piv"));
        registerBone("petal_bottom", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_bottom"));
        registerBone("pb_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_bottom").getChild("pb_base_piv"));
        registerBone("pb_mid_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_bottom").getChild("pb_mid_piv"));
        registerBone("pb_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_bottom").getChild("pb_tip_piv"));
        registerBone("petal_left", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_left"));
        registerBone("pl_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_left").getChild("pl_base_piv"));
        registerBone("pl_mid_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_left").getChild("pl_mid_piv"));
        registerBone("pl_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_left").getChild("pl_tip_piv"));
        registerBone("petal_right", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_right"));
        registerBone("pr_base_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_right").getChild("pr_base_piv"));
        registerBone("pr_mid_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_right").getChild("pr_mid_piv"));
        registerBone("pr_tip_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("petal_right").getChild("pr_tip_piv"));
        registerBone("blossom_core", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("blossom_core"));
        registerBone("core_center_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("blossom_core").getChild("core_center_piv"));
        registerBone("core_glow_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("blossom_core").getChild("core_glow_piv"));
        registerBone("fx_anchor", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("flower_head").getChild("fx_anchor"));
        registerBone("stem_bend_mid_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("stem_bend_mid_piv"));
        registerBone("stem_twist_l_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("stem_twist_l_piv"));
        registerBone("stem_twist_r_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("stem_twist_r_piv"));
        registerBone("stem_neck_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("stem_neck_piv"));
        registerBone("stem_collar_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("stem_collar_piv"));
        registerBone("upper_vein_1_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("upper_vein_1_piv"));
        registerBone("upper_vein_2_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("upper_vein_2_piv"));
        registerBone("upper_vein_3_piv", rootPart.getChild("root").getChild("stem_base").getChild("stem_upper").getChild("upper_vein_3_piv"));
        registerBone("root_trunk_main_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_trunk_main_piv"));
        registerBone("root_strand_l_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_strand_l_piv"));
        registerBone("root_strand_r_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_strand_r_piv"));
        registerBone("root_strand_f_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_strand_f_piv"));
        registerBone("root_vein_1_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_vein_1_piv"));
        registerBone("root_vein_2_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_vein_2_piv"));
        registerBone("root_vein_3_piv", rootPart.getChild("root").getChild("stem_base").getChild("root_vein_3_piv"));
        registerBone("energy_motes", rootPart.getChild("root").getChild("energy_motes"));
        registerBone("mote_1", rootPart.getChild("root").getChild("energy_motes").getChild("mote_1"));
        registerBone("mote_2", rootPart.getChild("root").getChild("energy_motes").getChild("mote_2"));
        registerBone("mote_3", rootPart.getChild("root").getChild("energy_motes").getChild("mote_3"));
        registerBone("mote_4", rootPart.getChild("root").getChild("energy_motes").getChild("mote_4"));
    }

    private void registerBone(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
    }

    public ModelPart getBone(String name) {
        return bones.get(name);
    }

    public void resetPose() {
        for (Map.Entry<String, PartPose> e : initialPoses.entrySet()) {
            ModelPart p = bones.get(e.getKey());
            if (p != null) {
                p.loadPose(e.getValue());
                p.xScale = 1.0F;
                p.yScale = 1.0F;
                p.zScale = 1.0F;
            }
        }
    }

    public void applyBlossomPose(float ageSec, float emergeProgress, float bloomProgress) {
        resetPose();
        float e = Mth.clamp(emergeProgress, 0.0F, 1.0F);
        float easeOut = 1.0F - (1.0F - e) * (1.0F - e) * (1.0F - e);
        this.root.xScale = easeOut;
        this.root.yScale = easeOut;
        this.root.zScale = easeOut;

        // Gentle organic wind sway on upper stem & flower head
        ModelPart stemUpper = getBone("stem_upper");
        if (stemUpper != null) {
            stemUpper.zRot += Mth.sin(ageSec * 1.35F) * 0.055F;
            stemUpper.xRot += Mth.cos(ageSec * 1.05F) * 0.045F;
        }
        ModelPart head = getBone("flower_head");
        if (head != null) {
            head.zRot += Mth.sin(ageSec * 1.7F) * 0.04F;
        }

        // Smooth outward bloom opening when player touches/claims Trounce Blossom
        float b = Mth.clamp(bloomProgress, 0.0F, 1.0F);
        float bs = b * b * (3.0F - 2.0F * b);
        float openRad = bs * 0.92F;
        float calyxRad = bs * 0.34F;

        ModelPart pt = getBone("petal_top");
        ModelPart pb = getBone("petal_bottom");
        ModelPart pl = getBone("petal_left");
        ModelPart pr = getBone("petal_right");
        if (pt != null) pt.xRot -= openRad;
        if (pb != null) pb.xRot += openRad;
        if (pl != null) pl.yRot -= openRad;
        if (pr != null) pr.yRot += openRad;

        ModelPart ct = getBone("calyx_top");
        ModelPart cb = getBone("calyx_bottom");
        ModelPart cl = getBone("calyx_left");
        ModelPart cr = getBone("calyx_right");
        if (ct != null) ct.xRot -= calyxRad;
        if (cb != null) cb.xRot += calyxRad;
        if (cl != null) cl.yRot -= calyxRad;
        if (cr != null) cr.yRot += calyxRad;

        // Glowing heart pulses & scales up as petals open
        ModelPart core = getBone("blossom_core");
        if (core != null) {
            float sc = 0.85F + bs * 0.55F + 0.08F * Mth.sin(ageSec * 4.5F);
            core.xScale = sc; core.yScale = sc; core.zScale = sc;
        }

        // Orbiting Ley Line energy motes
        for (int i = 1; i <= 4; i++) {
            ModelPart m = getBone("mote_" + i);
            if (m != null) {
                m.yRot += ageSec * (0.9F + i * 0.25F);
                m.y += Mth.sin(ageSec * 2.2F + i * 1.5F) * 1.4F;
            }
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition b_root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create()
            , PartPose.offset(0.00F, 24.00F, 0.00F));
        PartDefinition b_stem_base = b_root.addOrReplaceChild("stem_base", CubeListBuilder.create()
            , PartPose.offset(0.00F, -0.00F, 0.00F));
        PartDefinition b_stem_upper = b_stem_base.addOrReplaceChild("stem_upper", CubeListBuilder.create()
            , PartPose.offset(0.00F, -12.00F, 3.50F));
        PartDefinition b_flower_head = b_stem_upper.addOrReplaceChild("flower_head", CubeListBuilder.create()
            , PartPose.offsetAndRotation(0.00F, -7.50F, -3.00F, -0.20944F, 0.00000F, 0.00000F));
        PartDefinition b_calyx_top = b_flower_head.addOrReplaceChild("calyx_top", CubeListBuilder.create()
            , PartPose.offset(0.00F, -1.50F, 0.00F));
        PartDefinition b_ct_base_piv = b_calyx_top.addOrReplaceChild("ct_base_piv", CubeListBuilder.create()
            .texOffs(172, 0).addBox(-2.50F, -3.50F, -1.00F, 5.00F, 7.00F, 2.00F)
            , PartPose.offsetAndRotation(0.00F, -3.50F, 0.70F, -0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_ct_tip_piv = b_calyx_top.addOrReplaceChild("ct_tip_piv", CubeListBuilder.create()
            .texOffs(186, 0).addBox(-1.50F, -2.50F, -0.50F, 3.00F, 5.00F, 1.00F)
            , PartPose.offsetAndRotation(0.00F, -8.00F, 2.00F, -0.38397F, 0.00000F, 0.00000F));
        PartDefinition b_calyx_bottom = b_flower_head.addOrReplaceChild("calyx_bottom", CubeListBuilder.create()
            , PartPose.offset(0.00F, 1.50F, 0.00F));
        PartDefinition b_cb_base_piv = b_calyx_bottom.addOrReplaceChild("cb_base_piv", CubeListBuilder.create()
            .texOffs(194, 0).addBox(-2.50F, -3.50F, -1.00F, 5.00F, 7.00F, 2.00F)
            , PartPose.offsetAndRotation(0.00F, 3.50F, 0.70F, 0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_cb_tip_piv = b_calyx_bottom.addOrReplaceChild("cb_tip_piv", CubeListBuilder.create()
            .texOffs(208, 0).addBox(-1.50F, -2.50F, -0.50F, 3.00F, 5.00F, 1.00F)
            , PartPose.offsetAndRotation(0.00F, 8.00F, 2.00F, 0.38397F, 0.00000F, 0.00000F));
        PartDefinition b_calyx_left = b_flower_head.addOrReplaceChild("calyx_left", CubeListBuilder.create()
            , PartPose.offset(1.50F, -0.00F, 0.00F));
        PartDefinition b_cl_base_piv = b_calyx_left.addOrReplaceChild("cl_base_piv", CubeListBuilder.create()
            .texOffs(216, 0).addBox(-3.50F, -2.50F, -1.00F, 7.00F, 5.00F, 2.00F)
            , PartPose.offsetAndRotation(3.50F, -0.00F, 0.70F, 0.00000F, -0.31416F, 0.00000F));
        PartDefinition b_cl_tip_piv = b_calyx_left.addOrReplaceChild("cl_tip_piv", CubeListBuilder.create()
            .texOffs(234, 0).addBox(-2.50F, -1.50F, -0.50F, 5.00F, 3.00F, 1.00F)
            , PartPose.offsetAndRotation(8.00F, -0.00F, 2.00F, 0.00000F, -0.38397F, 0.00000F));
        PartDefinition b_calyx_right = b_flower_head.addOrReplaceChild("calyx_right", CubeListBuilder.create()
            , PartPose.offset(-1.50F, -0.00F, 0.00F));
        PartDefinition b_cr_base_piv = b_calyx_right.addOrReplaceChild("cr_base_piv", CubeListBuilder.create()
            .texOffs(0, 15).addBox(-3.50F, -2.50F, -1.00F, 7.00F, 5.00F, 2.00F)
            , PartPose.offsetAndRotation(-3.50F, -0.00F, 0.70F, 0.00000F, 0.31416F, 0.00000F));
        PartDefinition b_cr_tip_piv = b_calyx_right.addOrReplaceChild("cr_tip_piv", CubeListBuilder.create()
            .texOffs(18, 15).addBox(-2.50F, -1.50F, -0.50F, 5.00F, 3.00F, 1.00F)
            , PartPose.offsetAndRotation(-8.00F, -0.00F, 2.00F, 0.00000F, 0.38397F, 0.00000F));
        PartDefinition b_petal_top = b_flower_head.addOrReplaceChild("petal_top", CubeListBuilder.create()
            , PartPose.offset(0.00F, -1.50F, -1.50F));
        PartDefinition b_pt_base_piv = b_petal_top.addOrReplaceChild("pt_base_piv", CubeListBuilder.create()
            .texOffs(30, 15).addBox(-3.00F, -1.50F, -3.50F, 6.00F, 3.00F, 7.00F)
            , PartPose.offsetAndRotation(0.00F, -1.80F, -3.00F, -0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_pt_mid_piv = b_petal_top.addOrReplaceChild("pt_mid_piv", CubeListBuilder.create()
            .texOffs(56, 15).addBox(-2.50F, -1.50F, -3.00F, 5.00F, 3.00F, 6.00F)
            , PartPose.offsetAndRotation(0.00F, -1.50F, -7.50F, 0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_pt_tip_piv = b_petal_top.addOrReplaceChild("pt_tip_piv", CubeListBuilder.create()
            .texOffs(78, 15).addBox(-1.50F, -1.00F, -2.50F, 3.00F, 2.00F, 5.00F)
            , PartPose.offsetAndRotation(0.00F, 0.60F, -11.80F, 0.45379F, 0.00000F, 0.00000F));
        PartDefinition b_petal_bottom = b_flower_head.addOrReplaceChild("petal_bottom", CubeListBuilder.create()
            , PartPose.offset(0.00F, 1.50F, -1.50F));
        PartDefinition b_pb_base_piv = b_petal_bottom.addOrReplaceChild("pb_base_piv", CubeListBuilder.create()
            .texOffs(94, 15).addBox(-3.00F, -1.50F, -3.50F, 6.00F, 3.00F, 7.00F)
            , PartPose.offsetAndRotation(0.00F, 1.80F, -3.00F, 0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_pb_mid_piv = b_petal_bottom.addOrReplaceChild("pb_mid_piv", CubeListBuilder.create()
            .texOffs(120, 15).addBox(-2.50F, -1.50F, -3.00F, 5.00F, 3.00F, 6.00F)
            , PartPose.offsetAndRotation(0.00F, 1.50F, -7.50F, -0.31416F, 0.00000F, 0.00000F));
        PartDefinition b_pb_tip_piv = b_petal_bottom.addOrReplaceChild("pb_tip_piv", CubeListBuilder.create()
            .texOffs(142, 15).addBox(-1.50F, -1.00F, -2.50F, 3.00F, 2.00F, 5.00F)
            , PartPose.offsetAndRotation(0.00F, -0.60F, -11.80F, -0.45379F, 0.00000F, 0.00000F));
        PartDefinition b_petal_left = b_flower_head.addOrReplaceChild("petal_left", CubeListBuilder.create()
            , PartPose.offset(1.50F, -0.00F, -1.50F));
        PartDefinition b_pl_base_piv = b_petal_left.addOrReplaceChild("pl_base_piv", CubeListBuilder.create()
            .texOffs(158, 15).addBox(-1.50F, -3.00F, -3.50F, 3.00F, 6.00F, 7.00F)
            , PartPose.offsetAndRotation(1.80F, -0.00F, -3.00F, 0.00000F, -0.31416F, 0.00000F));
        PartDefinition b_pl_mid_piv = b_petal_left.addOrReplaceChild("pl_mid_piv", CubeListBuilder.create()
            .texOffs(178, 15).addBox(-1.50F, -2.50F, -3.00F, 3.00F, 5.00F, 6.00F)
            , PartPose.offsetAndRotation(1.50F, -0.00F, -7.50F, 0.00000F, 0.31416F, 0.00000F));
        PartDefinition b_pl_tip_piv = b_petal_left.addOrReplaceChild("pl_tip_piv", CubeListBuilder.create()
            .texOffs(196, 15).addBox(-1.00F, -1.50F, -2.50F, 2.00F, 3.00F, 5.00F)
            , PartPose.offsetAndRotation(-0.40F, -0.00F, -11.80F, 0.00000F, 0.45379F, 0.00000F));
        PartDefinition b_petal_right = b_flower_head.addOrReplaceChild("petal_right", CubeListBuilder.create()
            , PartPose.offset(-1.50F, -0.00F, -1.50F));
        PartDefinition b_pr_base_piv = b_petal_right.addOrReplaceChild("pr_base_piv", CubeListBuilder.create()
            .texOffs(210, 15).addBox(-1.50F, -3.00F, -3.50F, 3.00F, 6.00F, 7.00F)
            , PartPose.offsetAndRotation(-1.80F, -0.00F, -3.00F, 0.00000F, 0.31416F, 0.00000F));
        PartDefinition b_pr_mid_piv = b_petal_right.addOrReplaceChild("pr_mid_piv", CubeListBuilder.create()
            .texOffs(230, 15).addBox(-1.50F, -2.50F, -3.00F, 3.00F, 5.00F, 6.00F)
            , PartPose.offsetAndRotation(-1.50F, -0.00F, -7.50F, 0.00000F, -0.31416F, 0.00000F));
        PartDefinition b_pr_tip_piv = b_petal_right.addOrReplaceChild("pr_tip_piv", CubeListBuilder.create()
            .texOffs(0, 28).addBox(-1.00F, -1.50F, -2.50F, 2.00F, 3.00F, 5.00F)
            , PartPose.offsetAndRotation(0.40F, -0.00F, -11.80F, 0.00000F, -0.45379F, 0.00000F));
        PartDefinition b_blossom_core = b_flower_head.addOrReplaceChild("blossom_core", CubeListBuilder.create()
            , PartPose.offset(0.00F, -0.00F, -5.00F));
        PartDefinition b_core_center_piv = b_blossom_core.addOrReplaceChild("core_center_piv", CubeListBuilder.create()
            .texOffs(14, 28).addBox(-1.50F, -1.50F, -2.50F, 3.00F, 3.00F, 5.00F)
            , PartPose.offsetAndRotation(0.00F, -0.00F, 0.00F, 0.00000F, 0.00000F, 0.78540F));
        PartDefinition b_core_glow_piv = b_blossom_core.addOrReplaceChild("core_glow_piv", CubeListBuilder.create()
            .texOffs(30, 28).addBox(-2.00F, -2.00F, -2.00F, 4.00F, 4.00F, 4.00F, new CubeDeformation(-0.02F))
            , PartPose.offsetAndRotation(0.00F, -0.00F, -1.00F, 0.78540F, 0.78540F, 0.00000F));
        PartDefinition b_fx_anchor = b_flower_head.addOrReplaceChild("fx_anchor", CubeListBuilder.create()
            , PartPose.offset(0.00F, -0.00F, -6.00F));
        PartDefinition b_stem_bend_mid_piv = b_stem_upper.addOrReplaceChild("stem_bend_mid_piv", CubeListBuilder.create()
            .texOffs(80, 0).addBox(-3.00F, -3.50F, -2.50F, 6.00F, 7.00F, 5.00F)
            , PartPose.offsetAndRotation(0.00F, 0.50F, 0.30F, -0.38397F, 0.00000F, -0.06981F));
        PartDefinition b_stem_twist_l_piv = b_stem_upper.addOrReplaceChild("stem_twist_l_piv", CubeListBuilder.create()
            .texOffs(102, 0).addBox(-1.00F, -3.50F, -1.50F, 2.00F, 7.00F, 3.00F)
            , PartPose.offsetAndRotation(2.40F, -0.00F, -0.30F, -0.27925F, -0.20944F, -0.17453F));
        PartDefinition b_stem_twist_r_piv = b_stem_upper.addOrReplaceChild("stem_twist_r_piv", CubeListBuilder.create()
            .texOffs(112, 0).addBox(-1.00F, -3.50F, -1.50F, 2.00F, 7.00F, 3.00F)
            , PartPose.offsetAndRotation(-2.40F, -0.20F, 0.50F, -0.41888F, 0.20944F, 0.13963F));
        PartDefinition b_stem_neck_piv = b_stem_upper.addOrReplaceChild("stem_neck_piv", CubeListBuilder.create()
            .texOffs(122, 0).addBox(-2.50F, -3.50F, -2.50F, 5.00F, 7.00F, 5.00F)
            , PartPose.offsetAndRotation(0.00F, -4.50F, -0.70F, 0.48869F, 0.00000F, 0.00000F));
        PartDefinition b_stem_collar_piv = b_stem_upper.addOrReplaceChild("stem_collar_piv", CubeListBuilder.create()
            .texOffs(142, 0).addBox(-2.50F, -2.00F, -2.00F, 5.00F, 4.00F, 4.00F)
            , PartPose.offsetAndRotation(0.00F, -7.20F, -2.50F, 1.01229F, 0.00000F, 0.00000F));
        PartDefinition b_upper_vein_1_piv = b_stem_upper.addOrReplaceChild("upper_vein_1_piv", CubeListBuilder.create()
            .texOffs(160, 0).addBox(-0.50F, -3.50F, -0.50F, 1.00F, 7.00F, 1.00F)
            , PartPose.offsetAndRotation(1.80F, -0.50F, -1.50F, -0.31416F, 0.00000F, -0.13963F));
        PartDefinition b_upper_vein_2_piv = b_stem_upper.addOrReplaceChild("upper_vein_2_piv", CubeListBuilder.create()
            .texOffs(164, 0).addBox(-0.50F, -3.00F, -0.50F, 1.00F, 6.00F, 1.00F)
            , PartPose.offsetAndRotation(-1.50F, -4.50F, -2.30F, 0.48869F, 0.00000F, 0.17453F));
        PartDefinition b_upper_vein_3_piv = b_stem_upper.addOrReplaceChild("upper_vein_3_piv", CubeListBuilder.create()
            .texOffs(168, 0).addBox(-0.50F, -3.00F, -0.50F, 1.00F, 6.00F, 1.00F)
            , PartPose.offsetAndRotation(1.40F, -5.00F, 0.70F, 0.48869F, 0.00000F, -0.13963F));
        PartDefinition b_root_trunk_main_piv = b_stem_base.addOrReplaceChild("root_trunk_main_piv", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-3.50F, -4.50F, -3.00F, 7.00F, 9.00F, 6.00F)
            , PartPose.offsetAndRotation(0.00F, -4.50F, 1.50F, -0.24435F, 0.00000F, 0.06981F));
        PartDefinition b_root_strand_l_piv = b_stem_base.addOrReplaceChild("root_strand_l_piv", CubeListBuilder.create()
            .texOffs(26, 0).addBox(-1.50F, -4.00F, -2.00F, 3.00F, 8.00F, 4.00F)
            , PartPose.offsetAndRotation(3.20F, -4.00F, 0.00F, -0.17453F, -0.31416F, -0.24435F));
        PartDefinition b_root_strand_r_piv = b_stem_base.addOrReplaceChild("root_strand_r_piv", CubeListBuilder.create()
            .texOffs(40, 0).addBox(-1.50F, -4.00F, -2.00F, 3.00F, 8.00F, 4.00F)
            , PartPose.offsetAndRotation(-3.20F, -3.80F, 2.20F, -0.31416F, 0.27925F, 0.20944F));
        PartDefinition b_root_strand_f_piv = b_stem_base.addOrReplaceChild("root_strand_f_piv", CubeListBuilder.create()
            .texOffs(54, 0).addBox(-2.00F, -3.00F, -1.50F, 4.00F, 6.00F, 3.00F)
            , PartPose.offsetAndRotation(0.50F, -3.00F, -1.80F, -0.41888F, 0.00000F, 0.00000F));
        PartDefinition b_root_vein_1_piv = b_stem_base.addOrReplaceChild("root_vein_1_piv", CubeListBuilder.create()
            .texOffs(68, 0).addBox(-0.50F, -3.50F, -0.50F, 1.00F, 7.00F, 1.00F)
            , PartPose.offsetAndRotation(1.80F, -4.50F, -0.80F, -0.31416F, 0.00000F, -0.17453F));
        PartDefinition b_root_vein_2_piv = b_stem_base.addOrReplaceChild("root_vein_2_piv", CubeListBuilder.create()
            .texOffs(72, 0).addBox(-0.50F, -3.50F, -0.50F, 1.00F, 7.00F, 1.00F)
            , PartPose.offsetAndRotation(-2.00F, -5.00F, 1.80F, -0.24435F, 0.00000F, 0.20944F));
        PartDefinition b_root_vein_3_piv = b_stem_base.addOrReplaceChild("root_vein_3_piv", CubeListBuilder.create()
            .texOffs(76, 0).addBox(-0.50F, -3.50F, -0.50F, 1.00F, 7.00F, 1.00F)
            , PartPose.offsetAndRotation(0.00F, -4.00F, 4.20F, -0.24435F, 0.00000F, 0.00000F));
        PartDefinition b_energy_motes = b_root.addOrReplaceChild("energy_motes", CubeListBuilder.create()
            , PartPose.offset(0.00F, -16.00F, -2.00F));
        PartDefinition b_mote_1 = b_energy_motes.addOrReplaceChild("mote_1", CubeListBuilder.create()
            .texOffs(46, 28).addBox(7.00F, -0.50F, -5.50F, 1.00F, 1.00F, 1.00F)
            , PartPose.offset(0.00F, -0.00F, 0.00F));
        PartDefinition b_mote_2 = b_energy_motes.addOrReplaceChild("mote_2", CubeListBuilder.create()
            .texOffs(50, 28).addBox(-7.50F, -6.50F, -4.50F, 1.00F, 1.00F, 1.00F)
            , PartPose.offset(0.00F, -0.00F, 0.00F));
        PartDefinition b_mote_3 = b_energy_motes.addOrReplaceChild("mote_3", CubeListBuilder.create()
            .texOffs(54, 28).addBox(6.00F, -9.50F, 3.50F, 1.00F, 1.00F, 1.00F)
            , PartPose.offset(0.00F, -0.00F, 0.00F));
        PartDefinition b_mote_4 = b_energy_motes.addOrReplaceChild("mote_4", CubeListBuilder.create()
            .texOffs(58, 28).addBox(-7.00F, 3.50F, 4.50F, 1.00F, 1.00F, 1.00F)
            , PartPose.offset(0.00F, -0.00F, 0.00F));
        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}
