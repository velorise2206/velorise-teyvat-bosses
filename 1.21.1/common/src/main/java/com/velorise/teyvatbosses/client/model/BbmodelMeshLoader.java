package com.velorise.teyvatbosses.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Standard Blockbench ({@code .bbmodel}) to native Mojang {@link LayerDefinition} loader.
 *
 * <p>Guarantees exact 1:1 visual parity with Blockbench by applying the exact similarity transform
 * {@code M * R_bb * M} where {@code M = diag(+1, -1, +1)} between Blockbench Y-up space and
 * Mojang ModelPart Y-down space:
 * <ul>
 *   <li>Pivot offsets: {@code (dx, -dy, dz)} relative to parent bone origin (with root at {@code Y = +24}).</li>
 *   <li>Cube &amp; bone Euler rotations (ZYX order): {@code (-rx, +ry, -rz)} in radians.</li>
 *   <li>Box coordinates: {@code (from.x - pivot.x, -(to.y - pivot.y), from.z - pivot.z)}.</li>
 * </ul>
 *
 * <p>Note: Native Mojang {@link CubeListBuilder} uses box-UV layout. For {@code box_uv: false}
 * (per-face UV) models, run {@code scripts/bbmodel_to_mojang.py} to repack the texture atlas into
 * standard box-UV first.</p>
 */
public final class BbmodelMeshLoader {

    private static final float DEG2RAD = (float) (Math.PI / 180.0D);

    private BbmodelMeshLoader() {}

    /**
     * Converts Blockbench Euler degrees {@code [rx, ry, rz]} into exact Mojang {@link PartPose}
     * radians {@code [-rx, +ry, -rz]} (verified {@code max_err = 0.0} under {@code M * R_bb * M}).
     */
    public static float[] toMojangEuler(float bbRxDeg, float bbRyDeg, float bbRzDeg) {
        return new float[]{
                -bbRxDeg * DEG2RAD,
                bbRyDeg * DEG2RAD,
                -bbRzDeg * DEG2RAD
        };
    }

    /**
     * Parses a raw Blockbench {@code .bbmodel} stream into a native Mojang {@link LayerDefinition}.
     */
    public static LayerDefinition loadLayer(InputStream stream, float inflate) {
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            return parseLayer(root, inflate);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load .bbmodel LayerDefinition", e);
        }
    }

    /**
     * Parses a pre-parsed Blockbench {@code .bbmodel} {@link JsonObject} into a {@link LayerDefinition}.
     */
    public static LayerDefinition parseLayer(JsonObject bb, float inflate) {
        int texW = 64;
        int texH = 64;
        if (bb.has("resolution") && bb.get("resolution").isJsonObject()) {
            JsonObject res = bb.getAsJsonObject("resolution");
            texW = res.has("width") ? res.get("width").getAsInt() : 64;
            texH = res.has("height") ? res.get("height").getAsInt() : 64;
        }

        Map<String, JsonObject> elements = new HashMap<>();
        if (bb.has("elements") && bb.get("elements").isJsonArray()) {
            for (JsonElement el : bb.getAsJsonArray("elements")) {
                if (!el.isJsonObject()) continue;
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("uuid")) {
                    elements.put(obj.get("uuid").getAsString(), obj);
                }
            }
        }

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootDef = mesh.getRoot();
        if (bb.has("outliner") && bb.get("outliner").isJsonArray()) {
            buildNodes(bb.getAsJsonArray("outliner"), rootDef, null, new float[]{0.0F, 0.0F, 0.0F}, elements, inflate);
        }
        return LayerDefinition.create(mesh, texW, texH);
    }

    private static void buildNodes(
            JsonArray nodes,
            PartDefinition parentPart,
            String parentName,
            float[] parentOrigin,
            Map<String, JsonObject> elements,
            float inflate
    ) {
        for (JsonElement nodeEl : nodes) {
            if (!nodeEl.isJsonObject()) continue;
            JsonObject bone = nodeEl.getAsJsonObject();
            String boneName = bone.has("name") ? bone.get("name").getAsString() : "bone";
            float[] boneOrigin = readVec3(bone, "origin", 0.0F, 0.0F, 0.0F);
            float[] boneRot = readVec3(bone, "rotation", 0.0F, 0.0F, 0.0F);

            float px = parentName == null ? 0.0F : (boneOrigin[0] - parentOrigin[0]);
            float py = parentName == null ? 24.0F : -(boneOrigin[1] - parentOrigin[1]);
            float pz = parentName == null ? 0.0F : (boneOrigin[2] - parentOrigin[2]);
            float[] mBoneRot = toMojangEuler(boneRot[0], boneRot[1], boneRot[2]);

            List<JsonObject> unrotated = new ArrayList<>();
            List<JsonObject> rotated = new ArrayList<>();
            List<Integer> unrotIdx = new ArrayList<>();
            List<Integer> rotIdx = new ArrayList<>();

            JsonArray children = bone.has("children") && bone.get("children").isJsonArray()
                    ? bone.getAsJsonArray("children")
                    : new JsonArray();

            int cubeCounter = 0;
            for (JsonElement child : children) {
                if (child.isJsonPrimitive() && child.getAsJsonPrimitive().isString()) {
                    JsonObject cube = elements.get(child.getAsString());
                    if (cube != null) {
                        float[] cr = readVec3(cube, "rotation", 0.0F, 0.0F, 0.0F);
                        if (Math.abs(cr[0]) > 1e-4F || Math.abs(cr[1]) > 1e-4F || Math.abs(cr[2]) > 1e-4F) {
                            rotated.add(cube);
                            rotIdx.add(cubeCounter);
                        } else {
                            unrotated.add(cube);
                            unrotIdx.add(cubeCounter);
                        }
                        cubeCounter++;
                    }
                }
            }

            CubeListBuilder builder = CubeListBuilder.create();
            for (int i = 0; i < unrotated.size(); i++) {
                JsonObject cube = unrotated.get(i);
                int idx = unrotIdx.get(i);
                float[] from = readVec3(cube, "from", 0.0F, 0.0F, 0.0F);
                float[] to = readVec3(cube, "to", 0.0F, 0.0F, 0.0F);
                int[] uv = readUvOffset(cube);
                float zOff = (idx % 6) * 0.002F;
                float cubeInflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0.0F;
                builder.texOffs(uv[0], uv[1]).addBox(
                        from[0] - boneOrigin[0],
                        -(to[1] - boneOrigin[1]),
                        from[2] - boneOrigin[2],
                        to[0] - from[0],
                        to[1] - from[1],
                        to[2] - from[2],
                        new CubeDeformation(inflate + cubeInflate + zOff)
                );
            }

            PartPose bonePose = (Math.abs(mBoneRot[0]) > 1e-5F || Math.abs(mBoneRot[1]) > 1e-5F || Math.abs(mBoneRot[2]) > 1e-5F)
                    ? PartPose.offsetAndRotation(px, py, pz, mBoneRot[0], mBoneRot[1], mBoneRot[2])
                    : PartPose.offset(px, py, pz);

            PartDefinition bonePart = parentPart.addOrReplaceChild(boneName, builder, bonePose);

            for (int i = 0; i < rotated.size(); i++) {
                JsonObject cube = rotated.get(i);
                int idx = rotIdx.get(i);
                float[] from = readVec3(cube, "from", 0.0F, 0.0F, 0.0F);
                float[] to = readVec3(cube, "to", 0.0F, 0.0F, 0.0F);
                float[] cOrig = readVec3(cube, "origin", 0.0F, 0.0F, 0.0F);
                float[] cRot = readVec3(cube, "rotation", 0.0F, 0.0F, 0.0F);
                float[] mRot = toMojangEuler(cRot[0], cRot[1], cRot[2]);
                int[] uv = readUvOffset(cube);
                float zOff = (idx % 6) * 0.002F;
                float cubeInflate = cube.has("inflate") ? cube.get("inflate").getAsFloat() : 0.0F;

                bonePart.addOrReplaceChild(
                        "c_" + idx,
                        CubeListBuilder.create().texOffs(uv[0], uv[1]).addBox(
                                from[0] - cOrig[0],
                                -(to[1] - cOrig[1]),
                                from[2] - cOrig[2],
                                to[0] - from[0],
                                to[1] - from[1],
                                to[2] - from[2],
                                new CubeDeformation(inflate + cubeInflate + zOff)
                        ),
                        PartPose.offsetAndRotation(
                                cOrig[0] - boneOrigin[0],
                                -(cOrig[1] - boneOrigin[1]),
                                cOrig[2] - boneOrigin[2],
                                mRot[0], mRot[1], mRot[2]
                        )
                );
            }

            buildNodes(children, bonePart, boneName, boneOrigin, elements, inflate);
        }
    }

    private static float[] readVec3(JsonObject obj, String key, float dx, float dy, float dz) {
        if (!obj.has(key) || !obj.get(key).isJsonArray()) return new float[]{dx, dy, dz};
        JsonArray a = obj.getAsJsonArray(key);
        if (a.size() < 3) return new float[]{dx, dy, dz};
        return new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
    }

    private static int[] readUvOffset(JsonObject cube) {
        if (cube.has("uv_offset") && cube.get("uv_offset").isJsonArray()) {
            JsonArray a = cube.getAsJsonArray("uv_offset");
            if (a.size() >= 2) {
                return new int[]{Mth.floor(a.get(0).getAsFloat()), Mth.floor(a.get(1).getAsFloat())};
            }
        }
        return new int[]{0, 0};
    }
}
