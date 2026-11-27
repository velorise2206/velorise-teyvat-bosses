package com.velorise.teyvatbosses.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.velorise.teyvatbosses.TeyvatBossesConstants;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Lightweight zero-dependency Blockbench / Bedrock animation loader for {@code Velorise: Teyvat Bosses}.
 *
 * <p>Scans {@code assets/velorise_teyvat_bosses/animations/} on every client resource reload
 * ({@code F3 + T}) and parses both standard Bedrock {@code .animation.json} files and raw
 * Blockbench {@code .bbmodel} files into cached clips and native Mojang {@link AnimationDefinition}s
 * with zero per-frame JSON or reflection overhead.</p>
 */
public final class BedrockAnimationLoader implements ResourceManagerReloadListener {

    public static final BedrockAnimationLoader INSTANCE = new BedrockAnimationLoader();
    public static final ResourceLocation RELOAD_ID = TeyvatBossesConstants.id("bedrock_animations");

    public static final int CH_ROT = 0;
    public static final int CH_POS = 1;
    public static final int CH_SCALE = 2;

    public static final int INTERP_SMOOTH = 0;
    public static final int INTERP_LINEAR = 1;
    public static final int INTERP_EASE_IN = 2;
    public static final int INTERP_EASE_OUT = 3;
    public static final int INTERP_EASE_IN_OUT = 4;

    private static final float DEG2RAD = (float) (Math.PI / 180.0D);

    private static final Map<String, Map<String, Clip>> LOADED_CLIPS = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, AnimationDefinition>> MOJANG_DEFS = new ConcurrentHashMap<>();

    private BedrockAnimationLoader() {}

    public static final class Track {
        public final String bone;
        public final int channel;
        public final float[] times;
        public final float[] xs;
        public final float[] ys;
        public final float[] zs;
        public final int[] interps;

        public Track(String bone, int channel, float[] times, float[] xs, float[] ys, float[] zs, int[] interps) {
            this.bone = bone;
            this.channel = channel;
            this.times = times;
            this.xs = xs;
            this.ys = ys;
            this.zs = zs;
            this.interps = interps;
        }
    }

    public static final class Clip {
        public final String name;
        public final float lengthSeconds;
        public final boolean loop;
        public final List<Track> tracks;

        public Clip(String name, float lengthSeconds, boolean loop, List<Track> tracks) {
            this.name = name;
            this.lengthSeconds = lengthSeconds;
            this.loop = loop;
            this.tracks = tracks;
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        LOADED_CLIPS.clear();
        MOJANG_DEFS.clear();

        Map<ResourceLocation, Resource> found = resourceManager.listResources(
                "animations",
                loc -> TeyvatBossesConstants.MOD_ID.equals(loc.getNamespace())
                        && (loc.getPath().endsWith(".animation.json") || loc.getPath().endsWith(".bbmodel"))
        );

        int totalClips = 0;
        for (Map.Entry<ResourceLocation, Resource> entry : found.entrySet()) {
            ResourceLocation loc = entry.getKey();
            String modelId = extractModelId(loc.getPath());
            if (modelId.isEmpty()) continue;

            try (Reader reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonElement rootEl = JsonParser.parseReader(reader);
                if (!rootEl.isJsonObject()) continue;
                JsonObject root = rootEl.getAsJsonObject();

                Map<String, Clip> clips = parseDocument(root);
                if (!clips.isEmpty()) {
                    LOADED_CLIPS.computeIfAbsent(modelId, k -> new ConcurrentHashMap<>()).putAll(clips);
                    totalClips += clips.size();
                }
            } catch (Throwable t) {
                TeyvatBossesConstants.LOGGER.error("Failed to load animation asset {}", loc, t);
            }
        }
        TeyvatBossesConstants.LOGGER.info("Loaded {} Blockbench/Bedrock animation clips across {} models.",
                totalClips, LOADED_CLIPS.size());
    }

    /** Returns a hot-reloaded {@link Clip} for {@code modelId} and {@code clipName}, or {@code null} if not present. */
    @Nullable
    public static Clip getClip(String modelId, String clipName) {
        if (modelId == null || clipName == null) return null;
        Map<String, Clip> byModel = LOADED_CLIPS.get(modelId);
        return byModel != null ? byModel.get(clipName) : null;
    }

    /** Returns all loaded clips for {@code modelId}. */
    public static Map<String, Clip> getClips(String modelId) {
        Map<String, Clip> byModel = LOADED_CLIPS.get(modelId);
        return byModel != null ? byModel : Map.of();
    }

    /**
     * Converts and caches a loaded clip as a native Mojang {@link AnimationDefinition}
     * compatible with {@code HierarchicalModel.animate(...)}.
     */
    @Nullable
    public static AnimationDefinition getMojangDefinition(String modelId, String clipName) {
        Map<String, AnimationDefinition> modelCache = MOJANG_DEFS.computeIfAbsent(modelId, k -> new ConcurrentHashMap<>());
        AnimationDefinition cached = modelCache.get(clipName);
        if (cached != null) return cached;

        Clip clip = getClip(modelId, clipName);
        if (clip == null) return null;

        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(Math.max(0.05F, clip.lengthSeconds));
        if (clip.loop) {
            builder.looping();
        }
        for (Track tr : clip.tracks) {
            if (tr.times.length == 0) continue;
            AnimationChannel.Target target = switch (tr.channel) {
                case CH_POS -> AnimationChannel.Targets.POSITION;
                case CH_SCALE -> AnimationChannel.Targets.SCALE;
                default -> AnimationChannel.Targets.ROTATION;
            };
            Keyframe[] kfs = new Keyframe[tr.times.length];
            for (int i = 0; i < tr.times.length; i++) {
                var interp = tr.interps[i] == INTERP_LINEAR
                        ? AnimationChannel.Interpolations.LINEAR
                        : AnimationChannel.Interpolations.CATMULLROM;
                var vec = switch (tr.channel) {
                    case CH_POS -> KeyframeAnimations.posVec(tr.xs[i], tr.ys[i], tr.zs[i]);
                    case CH_SCALE -> KeyframeAnimations.scaleVec(tr.xs[i], tr.ys[i], tr.zs[i]);
                    default -> KeyframeAnimations.degreeVec(tr.xs[i], tr.ys[i], tr.zs[i]);
                };
                kfs[i] = new Keyframe(tr.times[i], vec, interp);
            }
            builder.addAnimation(tr.bone, new AnimationChannel(target, kfs));
        }
        AnimationDefinition built = builder.build();
        modelCache.put(clipName, built);
        return built;
    }

    /** Applies a loaded clip directly onto {@link ModelPart} bones using slope-clamped Catmull-Rom / linear sampling. */
    public static void applyWeighted(Function<String, ModelPart> boneLookup, @Nullable Clip clip, float timeSeconds, float weight) {
        applyWeighted(boneLookup, clip, timeSeconds, weight, false);
    }

    /** Overload supporting inverted X/Y rotation convention (used by Primo Geovishap rig, except jaw X). */
    public static void applyWeighted(Function<String, ModelPart> boneLookup, @Nullable Clip clip, float timeSeconds, float weight, boolean invertXYExceptJaw) {
        if (clip == null || weight <= 1e-4F) return;
        float t = normalizeTime(clip, timeSeconds);
        float[] v = new float[3];
        for (Track tr : clip.tracks) {
            ModelPart part = boneLookup.apply(tr.bone);
            if (part == null || tr.times.length == 0) continue;
            sampleTrack(tr, t, clip.loop, v);
            if (tr.channel == CH_ROT) {
                if (invertXYExceptJaw) {
                    if ("jaw".equals(tr.bone)) {
                        part.xRot += v[0] * DEG2RAD * weight;
                    } else {
                        part.xRot -= v[0] * DEG2RAD * weight;
                    }
                    part.yRot -= v[1] * DEG2RAD * weight;
                    part.zRot += v[2] * DEG2RAD * weight;
                } else {
                    part.xRot += v[0] * DEG2RAD * weight;
                    part.yRot += v[1] * DEG2RAD * weight;
                    part.zRot += v[2] * DEG2RAD * weight;
                }
            } else if (tr.channel == CH_POS) {
                part.x += v[0] * weight;
                part.y -= v[1] * weight;
                part.z += v[2] * weight;
            } else if (tr.channel == CH_SCALE) {
                part.xScale = part.xScale + (v[0] - part.xScale) * weight;
                part.yScale = part.yScale + (v[1] - part.yScale) * weight;
                part.zScale = part.zScale + (v[2] - part.zScale) * weight;
            }
        }
    }

    private static float normalizeTime(Clip clip, float timeSeconds) {
        if (clip.lengthSeconds <= 1e-4F) return 0.0F;
        if (clip.loop) {
            float t = timeSeconds % clip.lengthSeconds;
            return t < 0.0F ? t + clip.lengthSeconds : t;
        }
        return Math.max(0.0F, Math.min(clip.lengthSeconds, timeSeconds));
    }

    private static void sampleTrack(Track tr, float t, boolean loop, float[] out) {
        int n = tr.times.length;
        if (n == 1 || t <= tr.times[0]) {
            out[0] = tr.xs[0];
            out[1] = tr.ys[0];
            out[2] = tr.zs[0];
            return;
        }
        if (t >= tr.times[n - 1]) {
            out[0] = tr.xs[n - 1];
            out[1] = tr.ys[n - 1];
            out[2] = tr.zs[n - 1];
            return;
        }
        int i = 0;
        while (i + 1 < n && tr.times[i + 1] < t) i++;
        float dt = Math.max(1e-5F, tr.times[i + 1] - tr.times[i]);
        float u = (t - tr.times[i]) / dt;
        int mode = tr.interps[i];
        if (mode == INTERP_SMOOTH && n >= 3) {
            out[0] = evalCatmullHermite(tr.xs, tr.times, i, u, dt, loop);
            out[1] = evalCatmullHermite(tr.ys, tr.times, i, u, dt, loop);
            out[2] = evalCatmullHermite(tr.zs, tr.times, i, u, dt, loop);
            return;
        }
        float e = switch (mode) {
            case INTERP_EASE_IN -> u * u * u;
            case INTERP_EASE_OUT -> 1.0F - (1.0F - u) * (1.0F - u) * (1.0F - u);
            case INTERP_EASE_IN_OUT -> u * u * (3.0F - 2.0F * u);
            default -> u;
        };
        out[0] = tr.xs[i] + (tr.xs[i + 1] - tr.xs[i]) * e;
        out[1] = tr.ys[i] + (tr.ys[i + 1] - tr.ys[i]) * e;
        out[2] = tr.zs[i] + (tr.zs[i + 1] - tr.zs[i]) * e;
    }

    private static float evalCatmullHermite(float[] vals, float[] times, int i, float u, float dt, boolean loop) {
        int n = vals.length;
        float p1 = vals[i];
        float p2 = vals[i + 1];
        float secant = (p2 - p1) / dt;
        if (Math.abs(secant) < 1e-4F) {
            return p1;
        }
        float m1, m2;
        if (i > 0) {
            float span = Math.max(1e-4F, times[i + 1] - times[i - 1]);
            m1 = (vals[i + 1] - vals[i - 1]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[1] - times[0]) + (times[n - 1] - times[n - 2]));
            m1 = (vals[1] - vals[n - 2]) / span;
        } else {
            m1 = secant;
        }
        if (i + 2 < n) {
            float span = Math.max(1e-4F, times[i + 2] - times[i]);
            m2 = (vals[i + 2] - vals[i]) / span;
        } else if (loop && n >= 3) {
            float span = Math.max(1e-4F, (times[n - 1] - times[n - 2]) + (times[1] - times[0]));
            m2 = (vals[1] - vals[n - 2]) / span;
        } else {
            m2 = secant;
        }
        if (m1 * secant <= 0.0F) m1 = 0.0F;
        if (m2 * secant <= 0.0F) m2 = 0.0F;
        float maxSlope = Math.abs(secant) * 2.2F;
        m1 = Math.max(-maxSlope, Math.min(maxSlope, m1)) * dt;
        m2 = Math.max(-maxSlope, Math.min(maxSlope, m2)) * dt;
        float u2 = u * u;
        float u3 = u2 * u;
        return (2.0F * u3 - 3.0F * u2 + 1.0F) * p1
                + (u3 - 2.0F * u2 + u) * m1
                + (-2.0F * u3 + 3.0F * u2) * p2
                + (u3 - u2) * m2;
    }

    private static String extractModelId(String path) {
        String file = path.substring(path.lastIndexOf('/') + 1);
        if (file.endsWith(".animation.json")) {
            return file.substring(0, file.length() - ".animation.json".length());
        }
        if (file.endsWith(".bbmodel")) {
            return file.substring(0, file.length() - ".bbmodel".length());
        }
        return "";
    }

    private static Map<String, Clip> parseDocument(JsonObject root) {
        Map<String, Clip> result = new HashMap<>();
        JsonElement animsEl = root.get("animations");
        if (animsEl == null) return result;

        if (animsEl.isJsonObject()) {
            // Standard Bedrock .animation.json format
            for (Map.Entry<String, JsonElement> e : animsEl.getAsJsonObject().entrySet()) {
                if (!e.getValue().isJsonObject()) continue;
                String rawName = e.getKey();
                String shortName = rawName.startsWith("animation.")
                        ? rawName.substring(rawName.lastIndexOf('.') + 1)
                        : rawName;
                Clip clip = parseBedrockClip(shortName, e.getValue().getAsJsonObject());
                result.put(shortName, clip);
                if (!shortName.equals(rawName)) {
                    result.put(rawName, clip);
                }
            }
        } else if (animsEl.isJsonArray()) {
            // Direct Blockbench .bbmodel "animations" array format
            for (JsonElement el : animsEl.getAsJsonArray()) {
                if (!el.isJsonObject()) continue;
                Clip clip = parseBbmodelClip(el.getAsJsonObject());
                if (clip != null) {
                    result.put(clip.name, clip);
                }
            }
        }
        return result;
    }

    private static Clip parseBedrockClip(String name, JsonObject obj) {
        float length = obj.has("animation_length") ? obj.get("animation_length").getAsFloat() : 1.0F;
        boolean loop = false;
        if (obj.has("loop")) {
            JsonElement l = obj.get("loop");
            if (l.isJsonPrimitive()) {
                loop = l.getAsJsonPrimitive().isBoolean()
                        ? l.getAsBoolean()
                        : "loop".equalsIgnoreCase(l.getAsString()) || "true".equalsIgnoreCase(l.getAsString());
            }
        }
        List<Track> tracks = new ArrayList<>();
        if (obj.has("bones") && obj.get("bones").isJsonObject()) {
            for (Map.Entry<String, JsonElement> boneEntry : obj.getAsJsonObject("bones").entrySet()) {
                if (!boneEntry.getValue().isJsonObject()) continue;
                String boneName = boneEntry.getKey();
                JsonObject boneObj = boneEntry.getValue().getAsJsonObject();
                parseBedrockChannel(boneName, CH_ROT, boneObj.get("rotation"), tracks);
                parseBedrockChannel(boneName, CH_POS, boneObj.get("position"), tracks);
                parseBedrockChannel(boneName, CH_SCALE, boneObj.get("scale"), tracks);
            }
        }
        return new Clip(name, length, loop, tracks);
    }

    private record RawKf(float time, float x, float y, float z, int interp) {}

    private static void parseBedrockChannel(String bone, int channel, @Nullable JsonElement el, List<Track> out) {
        if (el == null || el.isJsonNull()) return;
        List<RawKf> kfs = new ArrayList<>();
        if (el.isJsonArray()) {
            float[] v = readVec3(el.getAsJsonArray());
            kfs.add(new RawKf(0.0F, v[0], v[1], v[2], INTERP_LINEAR));
        } else if (el.isJsonObject()) {
            JsonObject map = el.getAsJsonObject();
            if (map.has("vector")) {
                float[] v = readVec3(map.get("vector"));
                int interp = parseInterp(map.get("lerp_mode"));
                kfs.add(new RawKf(0.0F, v[0], v[1], v[2], interp));
            } else {
                for (Map.Entry<String, JsonElement> e : map.entrySet()) {
                    float time;
                    try {
                        time = Float.parseFloat(e.getKey());
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    JsonElement val = e.getValue();
                    if (val.isJsonArray()) {
                        float[] v = readVec3(val.getAsJsonArray());
                        kfs.add(new RawKf(time, v[0], v[1], v[2], INTERP_LINEAR));
                    } else if (val.isJsonObject()) {
                        JsonObject ko = val.getAsJsonObject();
                        JsonElement vecEl = ko.has("vector") ? ko.get("vector")
                                : (ko.has("post") ? ko.get("post") : ko.get("pre"));
                        float[] v = readVec3(vecEl);
                        int interp = parseInterp(ko.get("lerp_mode"));
                        kfs.add(new RawKf(time, v[0], v[1], v[2], interp));
                    }
                }
            }
        }
        emitTrack(bone, channel, kfs, out);
    }

    @Nullable
    private static Clip parseBbmodelClip(JsonObject obj) {
        if (!obj.has("name")) return null;
        String name = obj.get("name").getAsString();
        float length = obj.has("length") ? obj.get("length").getAsFloat() : 1.0F;
        boolean loop = obj.has("loop") && "loop".equalsIgnoreCase(obj.get("loop").getAsString());
        List<Track> tracks = new ArrayList<>();
        if (obj.has("animators") && obj.get("animators").isJsonObject()) {
            for (Map.Entry<String, JsonElement> ae : obj.getAsJsonObject("animators").entrySet()) {
                if (!ae.getValue().isJsonObject()) continue;
                JsonObject animObj = ae.getValue().getAsJsonObject();
                String bone = animObj.has("name") ? animObj.get("name").getAsString() : ae.getKey();
                if (!animObj.has("keyframes") || !animObj.get("keyframes").isJsonArray()) continue;

                List<RawKf> rot = new ArrayList<>();
                List<RawKf> pos = new ArrayList<>();
                List<RawKf> scl = new ArrayList<>();
                for (JsonElement ke : animObj.getAsJsonArray("keyframes")) {
                    if (!ke.isJsonObject()) continue;
                    JsonObject ko = ke.getAsJsonObject();
                    String ch = ko.has("channel") ? ko.get("channel").getAsString() : "";
                    float time = ko.has("time") ? ko.get("time").getAsFloat() : 0.0F;
                    int interp = parseInterp(ko.get("interpolation"));
                    if (!ko.has("data_points") || !ko.get("data_points").isJsonArray()
                            || ko.getAsJsonArray("data_points").isEmpty()) continue;
                    JsonObject dp = ko.getAsJsonArray("data_points").get(0).getAsJsonObject();
                    float x = parseFloat(dp.get("x"));
                    float y = parseFloat(dp.get("y"));
                    float z = parseFloat(dp.get("z"));
                    RawKf kf = new RawKf(time, x, y, z, interp);
                    if ("rotation".equals(ch)) rot.add(kf);
                    else if ("position".equals(ch)) pos.add(kf);
                    else if ("scale".equals(ch)) scl.add(kf);
                }
                emitTrack(bone, CH_ROT, rot, tracks);
                emitTrack(bone, CH_POS, pos, tracks);
                emitTrack(bone, CH_SCALE, scl, tracks);
            }
        }
        return new Clip(name, length, loop, tracks);
    }

    private static void emitTrack(String bone, int channel, List<RawKf> kfs, List<Track> out) {
        if (kfs.isEmpty()) return;
        kfs.sort(Comparator.comparingDouble(k -> k.time));
        int n = kfs.size();
        float[] times = new float[n];
        float[] xs = new float[n];
        float[] ys = new float[n];
        float[] zs = new float[n];
        int[] interps = new int[n];
        for (int i = 0; i < n; i++) {
            RawKf k = kfs.get(i);
            times[i] = k.time;
            xs[i] = k.x;
            ys[i] = k.y;
            zs[i] = k.z;
            interps[i] = k.interp;
        }
        out.add(new Track(bone, channel, times, xs, ys, zs, interps));
    }

    private static float[] readVec3(@Nullable JsonElement el) {
        float[] v = new float[3];
        if (el != null && el.isJsonArray()) {
            JsonArray arr = el.getAsJsonArray();
            for (int i = 0; i < Math.min(3, arr.size()); i++) {
                v[i] = parseFloat(arr.get(i));
            }
        }
        return v;
    }

    private static float parseFloat(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) return 0.0F;
        try {
            return el.getAsFloat();
        } catch (Exception e) {
            try {
                return Float.parseFloat(el.getAsString().trim());
            } catch (Exception ignored) {
                return 0.0F;
            }
        }
    }

    private static int parseInterp(@Nullable JsonElement el) {
        if (el == null || !el.isJsonPrimitive()) return INTERP_LINEAR;
        String s = el.getAsString().toLowerCase();
        return switch (s) {
            case "catmullrom", "smooth" -> INTERP_SMOOTH;
            case "ease_in" -> INTERP_EASE_IN;
            case "ease_out" -> INTERP_EASE_OUT;
            case "ease_in_out" -> INTERP_EASE_IN_OUT;
            default -> INTERP_LINEAR;
        };
    }
}
