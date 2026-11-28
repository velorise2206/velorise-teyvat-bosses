// Optimized Spiky Cataclysm-style PrimoGeovishapModel (101 cubes, 256x256 Box-UV)
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
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class PrimoGeovishapModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TeyvatBossesConstants.id("primo_geovishap"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_EMISSIVE = new ModelLayerLocation(TeyvatBossesConstants.id("primo_geovishap"), "emissive");

    public static final String[] ALL_BONES = {
            "root", "body", "torso", "neck", "head", "jaw", "arm_left", "forearm_left", "arm_right", "forearm_right", "leg_left", "shin_left", "leg_right", "shin_right", "tail_1", "tail_2", "tail_3", "tail_4", "fx_anchor"
    };

    private final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    private final Map<String, PartPose> initialPoses = new HashMap<>();

    public PrimoGeovishapModel(ModelPart rootPart) {
        this.root = rootPart.getChild("root");
        collectBones("root", this.root);
    }

    private void collectBones(String name, ModelPart part) {
        bones.put(name, part);
        initialPoses.put(name, part.storePose());
        for (String childName : ALL_BONES) {
            if (part.hasChild(childName)) {
                collectBones(childName, part.getChild(childName));
            }
        }
    }

    public ModelPart getBone(String name) { return bones.get(name); }
    public PartPose getInitialPose(String name) { return initialPoses.get(name); }

    public void resetPose() {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            PartPose pose = initialPoses.get(e.getKey());
            if (pose != null) e.getValue().loadPose(pose);
            e.getValue().visible = true;
        }
    }

    public void copyPoseFrom(PrimoGeovishapModel<?> other) {
        for (String name : ALL_BONES) {
            ModelPart dst = bones.get(name);
            ModelPart src = other.getBone(name);
            if (dst != null && src != null) {
                dst.copyFrom(src);
                dst.visible = src.visible;
            }
        }
    }

    public static LayerDefinition createBodyLayer() { return createLayer(0.0F); }
    public static LayerDefinition createEmissiveLayer() { return createLayer(0.055F); }

    private static LayerDefinition createLayer(float inflate) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        PartDefinition b_root = rootDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.000F, 24.000F, 0.000F));
        build_body(b_root, inflate);
        return LayerDefinition.create(mesh, 256, 256);
    }


    /** Adds a Hybrid 3D-base + V-angled 2D alpha-cutout dorsal rock-crystal blade (sharp silhouette from all 360 angles). */
    private static void addDorsalSpike3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                         float rx, float ry, float rz, float sx, float sy, float sz, float inflate) {
        CubeListBuilder base = CubeListBuilder.create();
        base.texOffs(92, 190).addBox(-2.0F * sx, -4.0F * sy, -3.0F * sz, 4.0F * sx, 5.5F * sy, 6.0F * sz, new CubeDeformation(inflate));
        PartDefinition root = parent.addOrReplaceChild(name, base, PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        float w = 11.5F * sx;
        float h = 17.3F * sy;
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(2, 190).addBox(-w * 0.5F, -h, 0.0F, 16.0F, 24.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.55F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(2, 190).addBox(-w * 0.5F, -h, 0.0F, 16.0F, 24.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, -0.55F, 0.0F));
    }

    /** Adds a Hybrid 3D-root + V-angled 2D dark-brown & gold-swirl lion mane blade. */
    private static void addManeSpike3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                       float rx, float ry, float rz, float sx, float sy, float sz, float inflate) {
        CubeListBuilder base = CubeListBuilder.create();
        base.texOffs(116, 190).addBox(-1.5F * sx, -3.5F * sy, -2.0F * sz, 3.0F * sx, 4.5F * sy, 4.0F * sz, new CubeDeformation(inflate));
        PartDefinition root = parent.addOrReplaceChild(name, base, PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        float w = 10.5F * sx;
        float h = 15.8F * sy;
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(22, 190).addBox(-w * 0.5F, -h, 0.0F, 14.0F, 22.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.52F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(22, 190).addBox(-w * 0.5F, -h, 0.0F, 14.0F, 22.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.52F, 0.0F));
    }

    /** Adds a crossed 2D gnarled dark-brown dorsal branch/antler fork. */
    private static void addBranchHorn3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                        float rx, float ry, float rz, float scale, float inflate) {
        PartDefinition root = parent.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(40, 190).addBox(-6.0F, -16.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.55F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(40, 190).addBox(-6.0F, -16.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, -0.55F, 0.0F));
    }

    /** Adds a V-angled 2D sharp rock elbow or knee spur blade. */
    private static void addLimbSpike3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                       float rx, float ry, float rz, float sx, float sy, float sz, float inflate) {
        PartDefinition root = parent.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(56, 190).addBox(-6.0F, -15.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.55F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(56, 190).addBox(-6.0F, -15.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, -0.55F, 0.0F));
    }

    /** Adds a V-angled 2D serrated tail spine blade. */
    private static void addTailSpike3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                       float rx, float ry, float rz, float sx, float sy, float sz, float inflate) {
        PartDefinition root = parent.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(56, 190).addBox(-4.5F, -10.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.55F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(56, 190).addBox(-4.5F, -10.5F, 0.0F, 12.0F, 18.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, -0.55F, 0.0F));
    }

    /** Adds a V-angled 2D dual-tone sculpted lotus tail-fan blade. */
    private static void addTailLotusBlade3D(PartDefinition parent, String name, float ox, float oy, float oz,
                                            float rx, float ry, float rz, float sx, float sy, float sz, float inflate) {
        PartDefinition root = parent.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.offsetAndRotation(ox, oy, oz, rx, ry, rz));
        root.addOrReplaceChild(name + "_a", CubeListBuilder.create().texOffs(72, 190).addBox(-7.0F, -18.0F, 0.0F, 14.0F, 20.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, 0.55F, 0.0F));
        root.addOrReplaceChild(name + "_b", CubeListBuilder.create().texOffs(72, 190).addBox(-7.0F, -18.0F, 0.0F, 14.0F, 20.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 1.5F, 0.0F, 0.0F, -0.55F, 0.0F));
    }

    private static PartDefinition build_jaw(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        // Tapered V-shaped lower jaw (wide at hinge -> narrow mid-jaw -> sharp pointed chin tip!)
        cubes.texOffs(100, 220).addBox(-3.2F, -0.5F, -7.5F, 6.4F, 3.6F, 6.5F, new CubeDeformation(inflate));
        cubes.texOffs(128, 220).addBox(-2.0F, -0.2F, -12.0F, 4.0F, 3.2F, 5.0F, new CubeDeformation(inflate));
        PartDefinition part = parent.addOrReplaceChild("jaw", cubes, PartPose.offset(0.0000F, 4.0000F, -1.0000F));
        // Prominent forward-jutting pointed pale-stone chin blade (matches Genshin Primo Geovishap chin keel)
        part.addOrReplaceChild("rc_0", CubeListBuilder.create().texOffs(128, 220).addBox(-1.6F, -1.6F, -2.8F, 3.2F, 4.2F, 4.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 2.8F, -10.2F, 0.56F, 0.0F, 0.0F));
        part.addOrReplaceChild("jaw_chin_tip", CubeListBuilder.create().texOffs(128, 220).addBox(-1.0F, -1.2F, -2.2F, 2.0F, 3.2F, 3.0F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 4.6F, -11.4F, 0.72F, 0.0F, 0.0F));
        return part;
    }

    private static PartDefinition build_head(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        // Skull base (keeps original UV for eyes & dark eye sockets)
        cubes.texOffs(194, 72).addBox(-5.2F, -5.8F, -7.5F, 10.4F, 8.6F, 10.5F, new CubeDeformation(inflate));
        PartDefinition part = parent.addOrReplaceChild("head", cubes, PartPose.offset(0.0000F, -3.0000F, -8.0000F));
        // Sculpted downward-sloping tapered dragon muzzle (mid bridge -> narrow hooked beak tip!)
        part.addOrReplaceChild("snout_mid", CubeListBuilder.create().texOffs(2, 220).addBox(-3.4F, -2.4F, -5.8F, 6.8F, 4.8F, 6.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, -0.4F, -6.8F, 0.22F, 0.0F, 0.0F));
        part.addOrReplaceChild("snout_tip", CubeListBuilder.create().texOffs(30, 220).addBox(-2.1F, -1.8F, -4.8F, 4.2F, 3.8F, 5.0F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 1.0F, -11.4F, 0.38F, 0.0F, 0.0F));
        part.addOrReplaceChild("snout_hook", CubeListBuilder.create().texOffs(30, 220).addBox(-1.3F, -0.8F, -2.2F, 2.6F, 2.6F, 2.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 2.6F, -14.8F, 0.62F, 0.0F, 0.0F));
        // Iconic pale-grey chiseled V-shaped forehead & nose-bridge stone crest
        part.addOrReplaceChild("forehead_crest", CubeListBuilder.create().texOffs(52, 220).addBox(-2.2F, -4.5F, -2.0F, 4.4F, 6.8F, 3.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, -3.8F, -7.8F, 0.54F, 0.0F, 0.0F));
        // Swept-back golden-brown fierce V-shaped eyebrow horns framing the amber eyes
        part.addOrReplaceChild("brow_horn_r", CubeListBuilder.create().texOffs(72, 220).addBox(-1.4F, -1.6F, -2.5F, 2.8F, 2.8F, 8.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(3.6F, -4.0F, -6.2F, 0.42F, 0.34F, 0.28F));
        part.addOrReplaceChild("brow_horn_l", CubeListBuilder.create().texOffs(72, 220).addBox(-1.4F, -1.6F, -2.5F, 2.8F, 2.8F, 8.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-3.6F, -4.0F, -6.2F, 0.42F, -0.34F, -0.28F));
        // Volumetric 3D dark-brown & gold-carved lion mane crown spikes
        addManeSpike3D(part, "rc_1",  0.0F, -4.5F, -5.5F, -0.52F,  0.00F,  0.00F, 0.85F, 0.78F, 0.85F, inflate);
        addManeSpike3D(part, "rc_2",  4.2F, -5.2F,  0.5F, -0.45F,  0.22F,  0.34F, 1.15F, 1.18F, 1.10F, inflate);
        addManeSpike3D(part, "rc_3", -4.2F, -5.2F,  0.5F, -0.45F, -0.22F, -0.34F, 1.15F, 1.18F, 1.10F, inflate);
        addManeSpike3D(part, "rc_4",  5.5F, -2.5F, -0.5F, -0.28F,  0.35F,  0.96F, 1.05F, 1.00F, 1.00F, inflate);
        addManeSpike3D(part, "rc_5", -5.5F, -2.5F, -0.5F, -0.28F, -0.35F, -0.96F, 1.05F, 1.00F, 1.00F, inflate);
        addManeSpike3D(part, "rc_6",  5.2F,  1.2F, -3.2F, -0.18F,  0.25F,  1.45F, 0.85F, 0.72F, 0.85F, inflate);
        addManeSpike3D(part, "rc_7", -5.2F,  1.2F, -3.2F, -0.18F, -0.25F, -1.45F, 0.85F, 0.72F, 0.85F, inflate);
        addManeSpike3D(part, "rc_8",  5.0F, -4.0F,  4.2F, -0.62F,  0.42F,  0.58F, 1.05F, 1.08F, 1.00F, inflate);
        addManeSpike3D(part, "rc_9", -5.0F, -4.0F,  4.2F, -0.62F, -0.42F, -0.58F, 1.05F, 1.08F, 1.00F, inflate);
        build_jaw(part, inflate);
        return part;
    }

    private static PartDefinition build_neck(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("neck", cubes, PartPose.offset(0.0000F, -5.0000F, -16.0000F));
        part.addOrReplaceChild("rc_10", CubeListBuilder.create().texOffs(2, 72).addBox(-6.5000F, -6.0000F, -5.0000F, 13.0000F, 12.0000F, 10.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -1.0000F, -2.5000F, -0.1745F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_11", CubeListBuilder.create().texOffs(192, 138).addBox(-5.5000F, -2.5000F, -4.5000F, 11.0000F, 5.0000F, 9.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -7.0000F, -1.5000F, 0.2094F, 0.0000F, -0.0000F));
        build_head(part, inflate);
        return part;
    }

    private static PartDefinition build_forearm_left(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("forearm_left", cubes, PartPose.offset(-2.5000F, 9.8000F, -0.5000F));
        // Flared pale-limestone vambrace shield tapering down to an obsidian wrist joint
        part.addOrReplaceChild("rc_12", CubeListBuilder.create().texOffs(154, 190).addBox(-4.2F, -4.6F, -4.2F, 8.4F, 8.2F, 8.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.6F, 2.2F, -1.2F, 0.24F, 0.08F, -0.14F));
        part.addOrReplaceChild("rc_13", CubeListBuilder.create().texOffs(154, 190).addBox(-2.2F, -4.4F, -3.6F, 4.4F, 7.6F, 7.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-4.2F, 1.4F, -1.5F, 0.16F, 0.12F, -0.34F));
        // Narrow obsidian-black wrist joint + chunky dark-brown faceted rock knuckles
        part.addOrReplaceChild("wrist_l", CubeListBuilder.create().texOffs(220, 190).addBox(-2.8F, -1.8F, -2.8F, 5.6F, 4.2F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.4F, 6.4F, -3.2F, 0.18F, 0.0F, 0.0F));
        part.addOrReplaceChild("fist_l", CubeListBuilder.create().texOffs(190, 190).addBox(-3.8F, -2.5F, -4.0F, 7.6F, 5.2F, 7.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.4F, 9.2F, -5.2F, 0.12F, 0.08F, -0.06F));
        // 3 splayed & tapered curved dragon claws (outer, center, inner thumb)
        part.addOrReplaceChild("rc_14", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -4.8F, 2.2F, 2.6F, 5.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(2.8F, 10.6F, -8.4F, 0.22F, -0.34F, 0.08F));
        part.addOrReplaceChild("rc_15", CubeListBuilder.create().texOffs(154, 212).addBox(-1.2F, -1.3F, -5.8F, 2.4F, 2.8F, 6.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.2F, 10.6F, -8.8F, 0.18F, 0.04F, 0.0F));
        part.addOrReplaceChild("rc_16", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -4.8F, 2.2F, 2.6F, 5.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-3.0F, 10.6F, -8.2F, 0.22F, 0.42F, -0.08F));
        return part;
    }

    private static PartDefinition build_arm_left(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("arm_left", cubes, PartPose.offset(-14.0000F, -0.0000F, -11.0000F));
        // Obsidian shoulder socket + muscular angled pale-limestone upper arm
        part.addOrReplaceChild("shld_joint_l", CubeListBuilder.create().texOffs(220, 190).addBox(-3.4F, -3.2F, -3.4F, 6.8F, 5.5F, 6.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.6F, 0.2F, 0.4F, 0.0F, 0.0F, -0.14F));
        part.addOrReplaceChild("rc_17", CubeListBuilder.create().texOffs(154, 190).addBox(-3.8F, -4.8F, -3.8F, 7.6F, 10.4F, 7.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-1.8F, 4.6F, 0.4F, -0.16F, 0.06F, -0.16F));
        part.addOrReplaceChild("rc_18", CubeListBuilder.create().texOffs(190, 190).addBox(-1.8F, -3.8F, -3.2F, 3.6F, 7.4F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-5.0F, 1.8F, 0.8F, -0.06F, 0.0F, -0.32F));
        addLimbSpike3D(part, "rc_19", -4.8F, 6.2F, 2.2F, -0.58F, -0.35F, -0.52F, 1.05F, 1.15F, 1.05F, inflate);
        addLimbSpike3D(part, "rc_20", -5.5F, 10.0F, 0.5F, -0.42F, -0.32F, -0.86F, 0.90F, 0.95F, 0.90F, inflate);
        build_forearm_left(part, inflate);
        return part;
    }

    private static PartDefinition build_forearm_right(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("forearm_right", cubes, PartPose.offset(2.5000F, 9.8000F, -0.5000F));
        // Flared pale-limestone vambrace shield tapering down to an obsidian wrist joint
        part.addOrReplaceChild("rc_21", CubeListBuilder.create().texOffs(154, 190).addBox(-4.2F, -4.6F, -4.2F, 8.4F, 8.2F, 8.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.6F, 2.2F, -1.2F, 0.24F, -0.08F, 0.14F));
        part.addOrReplaceChild("rc_22", CubeListBuilder.create().texOffs(154, 190).addBox(-2.2F, -4.4F, -3.6F, 4.4F, 7.6F, 7.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(4.2F, 1.4F, -1.5F, 0.16F, -0.12F, 0.34F));
        // Narrow obsidian-black wrist joint + chunky dark-brown faceted rock knuckles
        part.addOrReplaceChild("wrist_r", CubeListBuilder.create().texOffs(220, 190).addBox(-2.8F, -1.8F, -2.8F, 5.6F, 4.2F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.4F, 6.4F, -3.2F, 0.18F, 0.0F, 0.0F));
        part.addOrReplaceChild("fist_r", CubeListBuilder.create().texOffs(190, 190).addBox(-3.8F, -2.5F, -4.0F, 7.6F, 5.2F, 7.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.4F, 9.2F, -5.2F, 0.12F, -0.08F, 0.06F));
        // 3 splayed & tapered curved dragon claws (inner thumb, center, outer)
        part.addOrReplaceChild("rc_23", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -4.8F, 2.2F, 2.6F, 5.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-2.8F, 10.6F, -8.4F, 0.22F, 0.34F, -0.08F));
        part.addOrReplaceChild("rc_24", CubeListBuilder.create().texOffs(154, 212).addBox(-1.2F, -1.3F, -5.8F, 2.4F, 2.8F, 6.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.2F, 10.6F, -8.8F, 0.18F, -0.04F, 0.0F));
        part.addOrReplaceChild("rc_25", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -4.8F, 2.2F, 2.6F, 5.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(3.0F, 10.6F, -8.2F, 0.22F, -0.42F, 0.08F));
        return part;
    }

    private static PartDefinition build_arm_right(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("arm_right", cubes, PartPose.offset(14.0000F, -0.0000F, -11.0000F));
        // Obsidian shoulder socket + muscular angled pale-limestone upper arm
        part.addOrReplaceChild("shld_joint_r", CubeListBuilder.create().texOffs(220, 190).addBox(-3.4F, -3.2F, -3.4F, 6.8F, 5.5F, 6.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.6F, 0.2F, 0.4F, 0.0F, 0.0F, 0.14F));
        part.addOrReplaceChild("rc_26", CubeListBuilder.create().texOffs(154, 190).addBox(-3.8F, -4.8F, -3.8F, 7.6F, 10.4F, 7.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(1.8F, 4.6F, 0.4F, -0.16F, -0.06F, 0.16F));
        part.addOrReplaceChild("rc_27", CubeListBuilder.create().texOffs(190, 190).addBox(-1.8F, -3.8F, -3.2F, 3.6F, 7.4F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(5.0F, 1.8F, 0.8F, -0.06F, 0.0F, 0.32F));
        addLimbSpike3D(part, "rc_28",  4.8F, 6.2F, 2.2F, -0.58F,  0.35F,  0.52F, 1.05F, 1.15F, 1.05F, inflate);
        addLimbSpike3D(part, "rc_29",  5.5F, 10.0F, 0.5F, -0.42F,  0.32F,  0.86F, 0.90F, 0.95F, 0.90F, inflate);
        build_forearm_right(part, inflate);
        return part;
    }

    private static PartDefinition build_shin_left(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("shin_left", cubes, PartPose.offset(-1.0000F, 8.2000F, 1.5000F));
        // Tapered dark-brown shin armor narrowing down into a sleek obsidian-black ankle
        part.addOrReplaceChild("rc_30", CubeListBuilder.create().texOffs(190, 190).addBox(-3.2F, -3.8F, -3.2F, 6.4F, 7.2F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.2F, 2.4F, 0.2F, -0.28F, 0.0F, -0.08F));
        part.addOrReplaceChild("ankle_l", CubeListBuilder.create().texOffs(220, 190).addBox(-2.4F, -1.8F, -2.4F, 4.8F, 5.4F, 4.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.2F, 6.8F, -0.8F, 0.14F, 0.0F, 0.0F));
        part.addOrReplaceChild("foot_pad_l", CubeListBuilder.create().texOffs(220, 190).addBox(-3.2F, -1.4F, -3.8F, 6.4F, 3.0F, 6.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.2F, 10.4F, -2.6F, 0.0F, 0.08F, 0.0F));
        // 3 widely splayed tapered dragon talons (matches Genshin fanned 3-claw hind foot!)
        part.addOrReplaceChild("toe_l_in", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -5.2F, 2.2F, 2.6F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(1.8F, 10.6F, -4.8F, 0.14F, -0.46F, 0.0F));
        part.addOrReplaceChild("toe_l_mid", CubeListBuilder.create().texOffs(154, 212).addBox(-1.2F, -1.3F, -6.2F, 2.4F, 2.8F, 6.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.2F, 10.6F, -5.2F, 0.12F, 0.06F, 0.0F));
        part.addOrReplaceChild("toe_l_out", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -5.2F, 2.2F, 2.6F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-2.2F, 10.6F, -4.6F, 0.14F, 0.56F, 0.0F));
        return part;
    }

    private static PartDefinition build_leg_left(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("leg_left", cubes, PartPose.offset(-12.0000F, 2.0000F, 12.0000F));
        // Muscular pale-limestone thigh angled forward + obsidian knee crease + angled dark-brown kneecap shield
        part.addOrReplaceChild("rc_31", CubeListBuilder.create().texOffs(154, 190).addBox(-4.2F, -5.2F, -4.8F, 8.4F, 10.4F, 9.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.8F, 2.8F, -0.6F, 0.24F, 0.10F, -0.10F));
        part.addOrReplaceChild("knee_joint_l", CubeListBuilder.create().texOffs(220, 190).addBox(-3.0F, -2.0F, -3.2F, 6.0F, 4.0F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.8F, 7.2F, -1.2F, 0.12F, 0.0F, 0.0F));
        part.addOrReplaceChild("rc_32", CubeListBuilder.create().texOffs(190, 190).addBox(-3.6F, -3.8F, -2.4F, 7.2F, 7.2F, 4.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-0.8F, 6.4F, -4.4F, -0.34F, 0.12F, -0.08F));
        addLimbSpike3D(part, "rc_33", -3.2F, 5.0F, -2.2F, -0.35F, -0.42F, -0.68F, 0.80F, 0.82F, 0.80F, inflate);
        build_shin_left(part, inflate);
        return part;
    }

    private static PartDefinition build_shin_right(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("shin_right", cubes, PartPose.offset(1.0000F, 8.2000F, 1.5000F));
        // Tapered dark-brown shin armor narrowing down into a sleek obsidian-black ankle
        part.addOrReplaceChild("rc_34", CubeListBuilder.create().texOffs(190, 190).addBox(-3.2F, -3.8F, -3.2F, 6.4F, 7.2F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.2F, 2.4F, 0.2F, -0.28F, 0.0F, 0.08F));
        part.addOrReplaceChild("ankle_r", CubeListBuilder.create().texOffs(220, 190).addBox(-2.4F, -1.8F, -2.4F, 4.8F, 5.4F, 4.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.2F, 6.8F, -0.8F, 0.14F, 0.0F, 0.0F));
        part.addOrReplaceChild("foot_pad_r", CubeListBuilder.create().texOffs(220, 190).addBox(-3.2F, -1.4F, -3.8F, 6.4F, 3.0F, 6.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.2F, 10.4F, -2.6F, 0.0F, -0.08F, 0.0F));
        // 3 widely splayed tapered dragon talons (matches Genshin fanned 3-claw hind foot!)
        part.addOrReplaceChild("toe_r_in", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -5.2F, 2.2F, 2.6F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-1.8F, 10.6F, -4.8F, 0.14F, 0.46F, 0.0F));
        part.addOrReplaceChild("toe_r_mid", CubeListBuilder.create().texOffs(154, 212).addBox(-1.2F, -1.3F, -6.2F, 2.4F, 2.8F, 6.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.2F, 10.6F, -5.2F, 0.12F, -0.06F, 0.0F));
        part.addOrReplaceChild("toe_r_out", CubeListBuilder.create().texOffs(154, 212).addBox(-1.1F, -1.2F, -5.2F, 2.2F, 2.6F, 5.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(2.2F, 10.6F, -4.6F, 0.14F, -0.56F, 0.0F));
        return part;
    }

    private static PartDefinition build_leg_right(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("leg_right", cubes, PartPose.offset(12.0000F, 2.0000F, 12.0000F));
        // Muscular pale-limestone thigh angled forward + obsidian knee crease + angled dark-brown kneecap shield
        part.addOrReplaceChild("rc_35", CubeListBuilder.create().texOffs(154, 190).addBox(-4.2F, -5.2F, -4.8F, 8.4F, 10.4F, 9.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.8F, 2.8F, -0.6F, 0.24F, -0.10F, 0.10F));
        part.addOrReplaceChild("knee_joint_r", CubeListBuilder.create().texOffs(220, 190).addBox(-3.0F, -2.0F, -3.2F, 6.0F, 4.0F, 6.4F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.8F, 7.2F, -1.2F, 0.12F, 0.0F, 0.0F));
        part.addOrReplaceChild("rc_36", CubeListBuilder.create().texOffs(190, 190).addBox(-3.6F, -3.8F, -2.4F, 7.2F, 7.2F, 4.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.8F, 6.4F, -4.4F, -0.34F, -0.12F, 0.08F));
        addLimbSpike3D(part, "rc_37",  3.2F, 5.0F, -2.2F, -0.35F,  0.42F,  0.68F, 0.80F, 0.82F, 0.80F, inflate);
        build_shin_right(part, inflate);
        return part;
    }

    private static PartDefinition build_tail_4(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        // Sleek tapered tail tip & nested faceted silver-obsidian orb core (no blocky protruding cube!)
        cubes.texOffs(138, 138).addBox(-2.1F, -2.0F, -1.0F, 4.2F, 4.0F, 7.5F, new CubeDeformation(inflate));
        cubes.texOffs(40, 156).addBox(-1.6F, -1.6F, 5.8F, 3.2F, 3.2F, 5.6F, new CubeDeformation(inflate));
        cubes.texOffs(166, 172).addBox(-1.2F, -1.2F, 10.6F, 2.4F, 2.4F, 3.2F, new CubeDeformation(inflate));
        PartDefinition part = parent.addOrReplaceChild("tail_4", cubes, PartPose.offset(0.0000F, 1.0000F, 10.0000F));
        // Ventral tapered pale-stone belly plate
        part.addOrReplaceChild("tail4_belly", CubeListBuilder.create().texOffs(154, 190).addBox(-1.8F, 1.2F, -0.5F, 3.6F, 1.6F, 7.0F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 0.2F, 0.0F, 0.14F, 0.0F, 0.0F));
        // Volumetric 6-blade 2D angled-plane lotus tail-fan flaring smoothly around the silver orb
        addTailLotusBlade3D(part, "rc_38",  2.2F, -1.8F, 7.2F, -1.10F,  0.30F,  0.72F, 0.95F, 1.05F, 0.95F, inflate);
        addTailLotusBlade3D(part, "rc_39", -2.2F, -1.8F, 7.2F, -1.10F, -0.30F, -0.72F, 0.95F, 1.05F, 0.95F, inflate);
        addTailLotusBlade3D(part, "rc_40",  2.2F,  1.8F, 7.2F, -1.10F,  0.30F,  2.42F, 0.95F, 1.05F, 0.95F, inflate);
        addTailLotusBlade3D(part, "rc_41", -2.2F,  1.8F, 7.2F, -1.10F, -0.30F, -2.42F, 0.95F, 1.05F, 0.95F, inflate);
        addTailLotusBlade3D(part, "rc_42",  0.0F, -2.2F, 7.2F, -1.16F,  0.00F,  0.00F, 0.90F, 1.02F, 0.90F, inflate);
        addTailLotusBlade3D(part, "rc_43",  0.0F,  2.2F, 7.2F, -1.16F,  0.00F,  3.1416F, 0.90F, 1.02F, 0.90F, inflate);
        return part;
    }

    private static PartDefinition build_tail_3(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tail_3", cubes, PartPose.offset(0.0000F, 1.5000F, 10.0000F));
        // Tapered segment 3 + overlapping ventral limestone belly plate + swept serrated dorsal fins
        part.addOrReplaceChild("rc_44", CubeListBuilder.create().texOffs(92, 96).addBox(-2.8F, -2.4F, -5.5F, 5.6F, 4.8F, 11.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, 0.2000F, 5.2000F, 0.08F, 0.0000F, 0.0000F));
        part.addOrReplaceChild("tail3_belly", CubeListBuilder.create().texOffs(154, 190).addBox(-2.4F, 1.6F, -4.5F, 4.8F, 1.8F, 9.5F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 0.4F, 5.2F, 0.16F, 0.0F, 0.0F));
        addTailSpike3D(part, "rc_45",  2.4F, -1.6F, 5.2F, -0.92F,  0.28F,  0.76F, 0.85F, 0.90F, 0.85F, inflate);
        addTailSpike3D(part, "rc_46", -2.4F, -1.6F, 5.2F, -0.92F, -0.28F, -0.76F, 0.85F, 0.90F, 0.85F, inflate);
        addTailSpike3D(part, "rc_46c", 0.0F, -2.2F, 5.2F, -1.02F,  0.00F,  0.00F, 0.82F, 0.88F, 0.85F, inflate);
        build_tail_4(part, inflate);
        return part;
    }

    private static PartDefinition build_tail_2(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tail_2", cubes, PartPose.offset(0.0000F, 2.0000F, 10.0000F));
        // Tapered segment 2 + overlapping ventral limestone belly plate + swept serrated dorsal fins
        part.addOrReplaceChild("rc_47", CubeListBuilder.create().texOffs(50, 72).addBox(-3.6F, -3.0F, -6.0F, 7.2F, 6.0F, 12.2F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, 0.3000F, 5.4000F, 0.11F, 0.0000F, 0.0000F));
        part.addOrReplaceChild("tail2_belly", CubeListBuilder.create().texOffs(154, 190).addBox(-3.0F, 2.1F, -5.0F, 6.0F, 2.0F, 10.5F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 0.5F, 5.4F, 0.18F, 0.0F, 0.0F));
        addTailSpike3D(part, "rc_48",  3.0F, -2.0F, 5.4F, -0.88F,  0.30F,  0.74F, 0.98F, 1.02F, 0.98F, inflate);
        addTailSpike3D(part, "rc_49", -3.0F, -2.0F, 5.4F, -0.88F, -0.30F, -0.74F, 0.98F, 1.02F, 0.98F, inflate);
        addTailSpike3D(part, "rc_49c", 0.0F, -2.8F, 5.4F, -0.96F,  0.00F,  0.00F, 0.94F, 0.98F, 0.98F, inflate);
        build_tail_3(part, inflate);
        return part;
    }

    private static PartDefinition build_tail_1(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("tail_1", cubes, PartPose.offset(0.0000F, 2.5000F, 18.0000F));
        // Tapered segment 1 + overlapping ventral limestone belly plate + swept serrated dorsal fins
        part.addOrReplaceChild("rc_50", CubeListBuilder.create().texOffs(114, 44).addBox(-4.6F, -3.7F, -6.2F, 9.2F, 7.4F, 12.6F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, 0.5000F, 5.0000F, 0.14F, 0.0000F, 0.0000F));
        part.addOrReplaceChild("tail1_belly", CubeListBuilder.create().texOffs(154, 190).addBox(-3.8F, 2.7F, -5.2F, 7.6F, 2.2F, 10.8F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0F, 0.6F, 5.0F, 0.20F, 0.0F, 0.0F));
        addTailSpike3D(part, "rc_51",  3.8F, -2.4F, 5.0F, -0.84F,  0.30F,  0.70F, 1.12F, 1.15F, 1.08F, inflate);
        addTailSpike3D(part, "rc_52", -3.8F, -2.4F, 5.0F, -0.84F, -0.30F, -0.70F, 1.12F, 1.15F, 1.08F, inflate);
        addTailSpike3D(part, "rc_52c", 0.0F, -3.4F, 5.0F, -0.92F,  0.00F,  0.00F, 1.08F, 1.12F, 1.08F, inflate);
        build_tail_2(part, inflate);
        return part;
    }

    private static PartDefinition build_torso(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        cubes.texOffs(2, 2).addBox(-11.0000F, -12.0000F, -16.0000F, 22.0000F, 18.0000F, 22.0000F, new CubeDeformation(inflate));
        cubes.texOffs(150, 2).addBox(-9.0000F, -6.5000F, 2.5000F, 18.0000F, 14.0000F, 15.0000F, new CubeDeformation(inflate));

        PartDefinition part = parent.addOrReplaceChild("torso", cubes, PartPose.offset(0.0000F, -0.0000F, 0.0000F));
        part.addOrReplaceChild("rc_53", CubeListBuilder.create().texOffs(2, 44).addBox(-9.0000F, -3.0000F, -10.0000F, 18.0000F, 6.0000F, 20.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -12.5000F, -4.0000F, 0.1745F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_54", CubeListBuilder.create().texOffs(136, 72).addBox(-7.0000F, -3.0000F, -7.0000F, 14.0000F, 6.0000F, 14.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -8.5000F, 10.5000F, 0.3491F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_55", CubeListBuilder.create().texOffs(166, 44).addBox(-3.0000F, -3.5000F, -8.0000F, 6.0000F, 7.0000F, 16.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -17.0000F, -2.5000F, 0.2443F, 0.0000F, -0.0000F));
        part.addOrReplaceChild("rc_56", CubeListBuilder.create().texOffs(198, 96).addBox(-2.5000F, -3.0000F, -6.0000F, 5.0000F, 6.0000F, 12.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(0.0000F, -12.5000F, 11.0000F, 0.4189F, 0.0000F, -0.0000F));
        // Volumetric 3D Central Dorsal Spine Rock-Crystal Spikes (4 towering backward-swept blades)
        addDorsalSpike3D(part, "rc_57c", 0.0F, -16.5F, -10.0F, -0.44F, 0.00F, 0.00F, 1.00F, 1.05F, 1.05F, inflate);
        addDorsalSpike3D(part, "rc_58c", 0.0F, -18.5F,  -1.0F, -0.58F, 0.00F, 0.00F, 1.15F, 1.28F, 1.20F, inflate);
        addDorsalSpike3D(part, "rc_59c", 0.0F, -15.5F,   8.0F, -0.72F, 0.00F, 0.00F, 1.05F, 1.14F, 1.10F, inflate);
        addDorsalSpike3D(part, "rc_60c", 0.0F, -10.5F,  15.5F, -0.88F, 0.00F, 0.00F, 0.90F, 0.92F, 0.95F, inflate);
        // Volumetric 3D Paired Left & Right Dorsal Rock-Plate Spikes (4 pairs = 8 massive angled blades)
        addDorsalSpike3D(part, "rc_59",  6.8F, -14.5F, -10.0F, -0.48F,  0.34F,  0.42F, 0.95F, 1.05F, 0.95F, inflate);
        addDorsalSpike3D(part, "rc_60", -6.8F, -14.5F, -10.0F, -0.48F, -0.34F, -0.42F, 0.95F, 1.05F, 0.95F, inflate);
        addDorsalSpike3D(part, "rc_63",  8.5F, -16.0F,  -0.5F, -0.62F,  0.42F,  0.56F, 1.12F, 1.24F, 1.10F, inflate);
        addDorsalSpike3D(part, "rc_64", -8.5F, -16.0F,  -0.5F, -0.62F, -0.42F, -0.56F, 1.12F, 1.24F, 1.10F, inflate);
        addDorsalSpike3D(part, "rc_67",  8.2F, -13.0F,   9.0F, -0.76F,  0.44F,  0.64F, 1.00F, 1.10F, 1.00F, inflate);
        addDorsalSpike3D(part, "rc_68", -8.2F, -13.0F,   9.0F, -0.76F, -0.44F, -0.64F, 1.00F, 1.10F, 1.00F, inflate);
        addDorsalSpike3D(part, "rc_71",  6.2F,  -8.0F,  16.5F, -0.90F,  0.38F,  0.58F, 0.85F, 0.90F, 0.85F, inflate);
        addDorsalSpike3D(part, "rc_72", -6.2F,  -8.0F,  16.5F, -0.90F, -0.38F, -0.58F, 0.85F, 0.90F, 0.85F, inflate);
        // Volumetric 3D Gnarled Dark-Brown Dorsal Branch/Antler Forks
        addBranchHorn3D(part, "rc_73",  5.5F, -18.5F, -4.5F, -0.48F,  0.35F,  0.32F, 1.05F, inflate);
        addBranchHorn3D(part, "rc_75", -5.5F, -18.5F, -4.5F, -0.48F, -0.35F, -0.32F, 1.05F, inflate);
        addBranchHorn3D(part, "rc_74",  6.0F, -15.0F,  6.5F, -0.72F,  0.40F,  0.45F, 0.95F, inflate);
        addBranchHorn3D(part, "rc_76", -6.0F, -15.0F,  6.5F, -0.72F, -0.40F, -0.45F, 0.95F, inflate);
        part.addOrReplaceChild("rc_77", CubeListBuilder.create().texOffs(2, 118).addBox(-3.5000F, -4.0000F, -5.0000F, 7.0000F, 8.0000F, 10.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(13.8000F, -7.5000F, -10.0000F, -0.0000F, 0.0000F, 0.2793F));
        part.addOrReplaceChild("rc_78", CubeListBuilder.create().texOffs(2, 118).addBox(-3.5000F, -4.0000F, -5.0000F, 7.0000F, 8.0000F, 10.0000F, new CubeDeformation(inflate)), PartPose.offsetAndRotation(-13.8000F, -7.5000F, -10.0000F, -0.0000F, 0.0000F, -0.2793F));
        build_neck(part, inflate);
        build_arm_left(part, inflate);
        build_arm_right(part, inflate);
        build_leg_left(part, inflate);
        build_leg_right(part, inflate);
        build_tail_1(part, inflate);
        return part;
    }

    private static PartDefinition build_fx_anchor(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("fx_anchor", cubes, PartPose.offset(0.0000F, -6.0000F, -41.2500F));
        return part;
    }

    private static PartDefinition build_body(PartDefinition parent, float inflate) {
        CubeListBuilder cubes = CubeListBuilder.create();
        PartDefinition part = parent.addOrReplaceChild("body", cubes, PartPose.offset(0.0000F, -22.0000F, 0.0000F));
        build_torso(part, inflate);
        build_fx_anchor(part, inflate);
        return part;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
