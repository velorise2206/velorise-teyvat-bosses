package com.velorise.teyvatbosses.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks newly attached modular device parts on the client and computes a smooth,
 * gradual push-in interpolation so freshly welded components glide into their sockets
 * instead of popping in abruptly.
 */
public final class PartInstallAnimator {
    private static final float INSTALL_DURATION_TICKS = 15.0F;

    private static final class EntityState {
        int lastMask = -1;
        int initTick = -1;
        long lastSeenFrame = 0L;
        final Map<Integer, Float> startTicks = new HashMap<>();
    }

    private final Map<Integer, EntityState> states = new HashMap<>();
    private long frameCounter = 0L;

    /**
     * Synchronizes observed part bitmask for an entity at the current render frame.
     * Chunk loads and initial spawns initialize silently without playing an install slide.
     */
    public void update(Entity entity, int currentMask, float partialTicks) {
        int id = entity.getId();
        float liveTick = entity.tickCount + partialTicks;
        frameCounter++;

        EntityState state = states.computeIfAbsent(id, k -> new EntityState());
        state.lastSeenFrame = frameCounter;

        if (state.lastMask == -1) {
            state.lastMask = currentMask;
            state.initTick = entity.tickCount;
            return;
        }

        if (currentMask != state.lastMask) {
            // Suppress animation during the first tick after entity creation/chunk pairing
            if (entity.tickCount - state.initTick <= 1) {
                state.lastMask = currentMask;
                return;
            }
            int added = currentMask & ~state.lastMask;
            int removed = state.lastMask & ~currentMask;
            for (int bit = 1; bit != 0 && added != 0; bit <<= 1) {
                if ((added & bit) != 0) {
                    state.startTicks.put(bit, liveTick);
                    added &= ~bit;
                }
            }
            if (removed != 0) {
                state.startTicks.keySet().removeIf(b -> (removed & b) != 0);
            }
            state.lastMask = currentMask;
        }

        if (states.size() > 64 && (frameCounter & 63L) == 0L) {
            states.entrySet().removeIf(e -> frameCounter - e.getValue().lastSeenFrame > 1200L);
        }
    }

    /**
     * Returns remaining push-in fraction in [0.0, 1.0] (1.0 at install start, 0.0 once seated).
     */
    public float getRemaining(Entity entity, int partBit, float partialTicks) {
        EntityState state = states.get(entity.getId());
        if (state == null) return 0.0F;
        Float start = state.startTicks.get(partBit);
        if (start == null) return 0.0F;

        float elapsed = (entity.tickCount + partialTicks) - start;
        if (elapsed >= INSTALL_DURATION_TICKS) {
            state.startTicks.remove(partBit);
            return 0.0F;
        }
        float p = Mth.clamp(elapsed / INSTALL_DURATION_TICKS, 0.0F, 1.0F);
        float smooth = p * p * (3.0F - 2.0F * p);
        return 1.0F - smooth;
    }

    /**
     * Applies a gradual linear slide plus subtle socket scale-in on a {@link ModelPart}.
     */
    public static void applyPush(ModelPart bone, float rem, float offsetX, float offsetY, float offsetZ) {
        if (bone == null || rem <= 0.0001F) return;
        bone.x += offsetX * rem;
        bone.y += offsetY * rem;
        bone.z += offsetZ * rem;
        float s = 1.0F - 0.10F * rem;
        bone.xScale *= s;
        bone.yScale *= s;
        bone.zScale *= s;
    }
}
