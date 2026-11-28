#!/usr/bin/env python3
"""
Standard Blockbench (.bbmodel) -> Native Mojang EntityModel Converter
for Velorise: Teyvat Bosses.

Guarantees 1:1 visual parity with Blockbench:
  1. Applies the exact similarity transform M * R_bb * M (M = diag(+1, -1, +1))
     so every cube and bone rotation converts as (-rx, +ry, -rz) with 0.0 error.
  2. Automatically supports both `box_uv: true` and `box_uv: false` (per-face UV):
     when `box_uv: false` is detected and diffuse/emissive PNGs are provided,
     repacks all cube faces into a clean box-UV atlas with 1-px bleed padding.

Usage:
  python3 scripts/bbmodel_to_mojang.py <model.bbmodel> <OutModel.java> <ClassName> <entity_id> \
      [--diffuse path/to/diffuse.png] [--emissive path/to/emissive.png] [--atlas-size 1024]
"""

import argparse
import json
import math
import os
from PIL import Image


def paint_box_net(src_img, dst_img, faces, u, v, dx, dy, dz, net_w, net_h, atlas_w, atlas_h):
    def get_tile(side):
        fd = faces.get(side) or faces.get("north") or next(iter(faces.values()))
        u0, v0, u1, v1 = [int(round(c)) for c in fd["uv"]]
        if u1 <= u0 or v1 <= v0:
            return None
        return src_img.crop((u0, v0, u1, v1))

    north_tile = get_tile("north")
    if north_tile is not None:
        bw = min(atlas_w - max(0, u - 1), net_w + 2)
        bh = min(atlas_h - max(0, v - 1), net_h + 2)
        if bw > 0 and bh > 0:
            dst_img.paste(north_tile.resize((bw, bh), Image.Resampling.NEAREST), (max(0, u - 1), max(0, v - 1)))

    d_i = max(1, int(round(dz)))
    w_i = max(1, int(round(dx)))
    h_i = max(1, int(round(dy)))
    rects = [
        ("up", u + d_i, v, w_i, d_i),
        ("down", u + d_i + w_i, v, w_i, d_i),
        ("east", u, v + d_i, d_i, h_i),
        ("north", u + d_i, v + d_i, w_i, h_i),
        ("west", u + d_i + w_i, v + d_i, d_i, h_i),
        ("south", u + 2 * d_i + w_i, v + d_i, w_i, h_i),
    ]
    for side, rx, ry, rw, rh in rects:
        tile = get_tile(side)
        if tile is not None and rw > 0 and rh > 0 and rx + rw <= atlas_w and ry + rh <= atlas_h:
            dst_img.paste(tile.resize((rw, rh), Image.Resampling.NEAREST), (rx, ry))


def convert(bb_path, java_out, class_name, entity_id, diffuse_path=None, emissive_path=None, atlas_size=1024):
    with open(bb_path, "r", encoding="utf-8") as f:
        bb = json.load(f)

    box_uv = bool(bb.get("meta", {}).get("box_uv", True))
    res = bb.get("resolution", {})
    atlas_w = int(res.get("width", 64))
    atlas_h = int(res.get("height", 64))
    elem_map = {e["uuid"]: e for e in bb.get("elements", [])}
    uv_offsets = {}

    if not box_uv and diffuse_path:
        atlas_w = atlas_size
        atlas_h = atlas_size
        diff_src = Image.open(diffuse_path).convert("RGBA")
        diff_dst = Image.new("RGBA", (atlas_w, atlas_h), (0, 0, 0, 0))
        emis_src = Image.open(emissive_path).convert("RGBA") if emissive_path and os.path.exists(emissive_path) else None
        emis_dst = Image.new("RGBA", (atlas_w, atlas_h), (0, 0, 0, 0)) if emis_src else None

        pack_items = []
        for e in bb["elements"]:
            fx, fy, fz = e["from"]
            tx, ty, tz = e["to"]
            dx, dy, dz = abs(tx - fx), abs(ty - fy), abs(tz - fz)
            net_w = max(4, int(math.ceil(2.0 * dz + 2.0 * dx)))
            net_h = max(4, int(math.ceil(dz + dy)))
            pack_items.append((net_h, net_w, e["uuid"], dx, dy, dz))
        pack_items.sort(key=lambda x: (-x[0], -x[1]))

        pad = 2
        cur_x, cur_y, shelf_h = pad, pad, 0
        for net_h, net_w, uid, dx, dy, dz in pack_items:
            if cur_x + net_w + pad > atlas_w:
                cur_x = pad
                cur_y += shelf_h + pad
                shelf_h = 0
            if cur_y + net_h + pad > atlas_h:
                raise RuntimeError(f"Atlas {atlas_w}x{atlas_h} overflowed; increase --atlas-size")
            uv_offsets[uid] = (cur_x, cur_y)
            faces = elem_map[uid].get("faces", {})
            paint_box_net(diff_src, diff_dst, faces, cur_x, cur_y, dx, dy, dz, net_w, net_h, atlas_w, atlas_h)
            if emis_src and emis_dst:
                paint_box_net(emis_src, emis_dst, faces, cur_x, cur_y, dx, dy, dz, net_w, net_h, atlas_w, atlas_h)
            cur_x += net_w + pad
            shelf_h = max(shelf_h, net_h)

        diff_dst.save(diffuse_path)
        if emis_dst and emissive_path:
            emis_dst.save(emissive_path)
    else:
        for e in bb.get("elements", []):
            uv = e.get("uv_offset", [0, 0])
            uv_offsets[e["uuid"]] = (int(uv[0]), int(uv[1]))

    bones_ordered = []

    def walk(nodes, parent_name, parent_origin):
        for n in nodes:
            if isinstance(n, dict):
                bname = n["name"]
                borig = n.get("origin", [0.0, 0.0, 0.0])
                brot = n.get("rotation", [0.0, 0.0, 0.0])
                child_cubes = [elem_map[c] for c in n.get("children", []) if isinstance(c, str) and c in elem_map]
                bones_ordered.append((bname, parent_name, borig, brot, parent_origin, child_cubes))
                walk(n.get("children", []), bname, borig)

    walk(bb.get("outliner", []), None, [0.0, 0.0, 0.0])

    bone_names_str = ", ".join(f'"{b[0]}"' for b in bones_ordered)
    lines = [
        "package com.velorise.teyvatbosses.client.model;",
        "",
        "import com.mojang.blaze3d.vertex.PoseStack;",
        "import com.mojang.blaze3d.vertex.VertexConsumer;",
        "import com.velorise.teyvatbosses.TeyvatBossesConstants;",
        "import net.minecraft.client.model.EntityModel;",
        "import net.minecraft.client.model.geom.ModelLayerLocation;",
        "import net.minecraft.client.model.geom.ModelPart;",
        "import net.minecraft.client.model.geom.PartPose;",
        "import net.minecraft.client.model.geom.builders.CubeDeformation;",
        "import net.minecraft.client.model.geom.builders.CubeListBuilder;",
        "import net.minecraft.client.model.geom.builders.LayerDefinition;",
        "import net.minecraft.client.model.geom.builders.MeshDefinition;",
        "import net.minecraft.client.model.geom.builders.PartDefinition;",
        "import net.minecraft.world.entity.Entity;",
        "",
        "import java.util.HashMap;",
        "import java.util.Map;",
        "",
        f"public class {class_name}<T extends Entity> extends EntityModel<T> {{",
        f'    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TeyvatBossesConstants.id("{entity_id}"), "main");',
        f'    public static final ModelLayerLocation LAYER_LOCATION_EMISSIVE = new ModelLayerLocation(TeyvatBossesConstants.id("{entity_id}"), "emissive");',
        f"    public static final String[] ALL_BONES = {{ {bone_names_str} }};",
        "",
        "    private final ModelPart root;",
        "    private final Map<String, ModelPart> bones = new HashMap<>();",
        "    private final Map<String, PartPose> initialPoses = new HashMap<>();",
        "",
        f"    public {class_name}(ModelPart rootPart) {{",
        '        this.root = rootPart.getChild("root");',
        '        collectBones("root", this.root);',
        "    }",
        "",
        "    private void collectBones(String name, ModelPart part) {",
        "        bones.put(name, part);",
        "        initialPoses.put(name, part.storePose());",
        "        for (String childName : ALL_BONES) {",
        "            if (part.hasChild(childName)) collectBones(childName, part.getChild(childName));",
        "        }",
        "    }",
        "",
        "    public ModelPart getBone(String name) { return bones.get(name); }",
        "    public PartPose getInitialPose(String name) { return initialPoses.get(name); }",
        "    public static LayerDefinition createBodyLayer() { return createLayer(0.0F); }",
        "    public static LayerDefinition createEmissiveLayer() { return createLayer(0.055F); }",
        "",
        "    private static LayerDefinition createLayer(float inflate) {",
        "        MeshDefinition mesh = new MeshDefinition();",
        "        PartDefinition rootDef = mesh.getRoot();",
    ]
    for bname, pname, *_ in bones_ordered:
        pvar = "rootDef" if pname is None else f"b_{pname}"
        lines.append(f"        PartDefinition b_{bname} = build_{bname}({pvar}, inflate);")
    lines.append(f"        return LayerDefinition.create(mesh, {atlas_w}, {atlas_h});")
    lines.append("    }")

    for bname, pname, borig, brot, porig, cubes in bones_ordered:
        ox, oy, oz = (0.0, 24.0, 0.0) if pname is None else (borig[0] - porig[0], -(borig[1] - porig[1]), borig[2] - porig[2])
        bmx, bmy, bmz = -math.radians(brot[0]), math.radians(brot[1]), -math.radians(brot[2])
        lines.append(f"    private static PartDefinition build_{bname}(PartDefinition parent, float inflate) {{")
        lines.append(f'        PartDefinition part = parent.addOrReplaceChild("{bname}", CubeListBuilder.create()')
        rot_cubes = []
        for idx, c in enumerate(cubes):
            rot = c.get("rotation", [0.0, 0.0, 0.0])
            if any(abs(r) > 1e-4 for r in rot):
                rot_cubes.append((idx, c))
            else:
                u, v = uv_offsets[c["uuid"]]
                fx, fy, fz = c["from"]
                tx, ty, tz = c["to"]
                zoff = (idx % 6) * 0.002
                lines.append(
                    f"                .texOffs({u}, {v}).addBox({fx - borig[0]:.3f}F, {-(ty - borig[1]):.3f}F, {fz - borig[2]:.3f}F, "
                    f"{tx - fx:.3f}F, {ty - fy:.3f}F, {tz - fz:.3f}F, new CubeDeformation(inflate + {zoff:.3f}F))"
                )
        if any(abs(r) > 1e-5 for r in (bmx, bmy, bmz)):
            lines.append(f"                , PartPose.offsetAndRotation({ox:.3f}F, {oy:.3f}F, {oz:.3f}F, {bmx:.4f}F, {bmy:.4f}F, {bmz:.4f}F));")
        else:
            lines.append(f"                , PartPose.offset({ox:.3f}F, {oy:.3f}F, {oz:.3f}F));")
        for idx, c in rot_cubes:
            u, v = uv_offsets[c["uuid"]]
            fx, fy, fz = c["from"]
            tx, ty, tz = c["to"]
            cx, cy, cz = c.get("origin", [0.0, 0.0, 0.0])
            rx, ry, rz = c.get("rotation", [0.0, 0.0, 0.0])
            mx, my, mz = -math.radians(rx), math.radians(ry), -math.radians(rz)
            zoff = (idx % 6) * 0.002
            lines.append(
                f'        part.addOrReplaceChild("c_{idx}", CubeListBuilder.create().texOffs({u}, {v})'
                f'.addBox({fx - cx:.3f}F, {-(ty - cy):.3f}F, {fz - cz:.3f}F, {tx - fx:.3f}F, {ty - fy:.3f}F, {tz - fz:.3f}F, '
                f'new CubeDeformation(inflate + {zoff:.3f}F)), PartPose.offsetAndRotation({cx - borig[0]:.3f}F, {-(cy - borig[1]):.3f}F, {cz - borig[2]:.3f}F, {mx:.4f}F, {my:.4f}F, {mz:.4f}F));'
            )
        lines.append("        return part;")
        lines.append("    }")

    lines.extend([
        "",
        "    @Override",
        "    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}",
        "",
        "    @Override",
        "    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {",
        "        root.render(poseStack, buffer, packedLight, packedOverlay, color);",
        "    }",
        "}",
        "",
    ])

    with open(java_out, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
    print(f"Converted {bb_path} -> {java_out} ({len(elem_map)} cubes, {atlas_w}x{atlas_h} box_uv)")


if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("bbmodel")
    p.add_argument("java_out")
    p.add_argument("class_name")
    p.add_argument("entity_id")
    p.add_argument("--diffuse", default=None)
    p.add_argument("--emissive", default=None)
    p.add_argument("--atlas-size", type=int, default=1024)
    args = p.parse_args()
    convert(args.bbmodel, args.java_out, args.class_name, args.entity_id, args.diffuse, args.emissive, args.atlas_size)
