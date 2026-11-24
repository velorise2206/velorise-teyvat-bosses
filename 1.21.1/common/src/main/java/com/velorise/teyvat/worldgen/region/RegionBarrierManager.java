package com.velorise.teyvat.worldgen.region;

import com.velorise.teyvat.config.TeyvatWorldConfig;
import com.velorise.teyvat.worldgen.TeyvatDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * World-persistent state for {@code veloriseteyvat:teyvat}:
 * <ul>
 *   <li>Unlocked regions (defaults to {@link TeyvatRegion#MONDSTADT} only)</li>
 *   <li>Locked per-save {@code worldScale} (defaults to {@code 1.0} = 1m:1block)</li>
 *   <li>Placed structure IDs so NBT buildings never overwrite player edits on chunk reload</li>
 * </ul>
 */
public final class RegionBarrierManager extends SavedData {
    private static final String DATA_NAME = "veloriseteyvat_region_locks";
    private static final String TAG_UNLOCKED = "UnlockedRegions";
    private static final String TAG_WORLD_SCALE = "WorldScale";
    private static final String TAG_PLACED_STRUCTURES = "PlacedStructures";

    private final EnumSet<TeyvatRegion> unlocked = EnumSet.noneOf(TeyvatRegion.class);
    private final Set<String> placedStructures = new HashSet<>();
    private double worldScale;

    public RegionBarrierManager() {
        for (TeyvatRegion region : TeyvatRegion.values()) {
            if (region.isUnlockedByDefault()) {
                unlocked.add(region);
            }
        }
        this.worldScale = TeyvatWorldConfig.get().clampedScale();
        RegionMacroMap.setActiveWorldScale(this.worldScale);
    }

    public static RegionBarrierManager get(ServerLevel teyvatLevel) {
        RegionBarrierManager data = teyvatLevel.getDataStorage().computeIfAbsent(
                new Factory<>(RegionBarrierManager::new, RegionBarrierManager::load, null),
                DATA_NAME
        );
        RegionMacroMap.setActiveWorldScale(data.worldScale);
        return data;
    }

    public static RegionBarrierManager load(CompoundTag tag, HolderLookup.Provider registries) {
        RegionBarrierManager data = new RegionBarrierManager();
        if (tag.contains(TAG_WORLD_SCALE)) {
            data.worldScale = Math.max(0.25, Math.min(1.0, tag.getDouble(TAG_WORLD_SCALE)));
            RegionMacroMap.setActiveWorldScale(data.worldScale);
        }
        if (tag.contains(TAG_UNLOCKED, Tag.TAG_LIST)) {
            data.unlocked.clear();
            ListTag list = tag.getList(TAG_UNLOCKED, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                String name = list.getString(i);
                for (TeyvatRegion r : TeyvatRegion.values()) {
                    if (r.serializedName().equals(name)) {
                        data.unlocked.add(r);
                    }
                }
            }
            if (data.unlocked.isEmpty()) {
                data.unlocked.add(TeyvatRegion.MONDSTADT);
            }
        }
        if (tag.contains(TAG_PLACED_STRUCTURES, Tag.TAG_LIST)) {
            ListTag sList = tag.getList(TAG_PLACED_STRUCTURES, Tag.TAG_STRING);
            for (int i = 0; i < sList.size(); i++) {
                data.placedStructures.add(sList.getString(i));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putDouble(TAG_WORLD_SCALE, worldScale);

        ListTag list = new ListTag();
        for (TeyvatRegion region : unlocked) {
            list.add(StringTag.valueOf(region.serializedName()));
        }
        tag.put(TAG_UNLOCKED, list);

        ListTag sList = new ListTag();
        for (String id : placedStructures) {
            sList.add(StringTag.valueOf(id));
        }
        tag.put(TAG_PLACED_STRUCTURES, sList);
        return tag;
    }

    public double getWorldScale() {
        return worldScale;
    }

    public boolean isStructurePlaced(String structureKey) {
        return placedStructures.contains(structureKey);
    }

    public void markStructurePlaced(String structureKey) {
        if (placedStructures.add(structureKey)) {
            setDirty();
        }
    }

    public boolean isUnlocked(TeyvatRegion region) {
        return unlocked.contains(region);
    }

    public boolean unlock(TeyvatRegion region) {
        if (region == TeyvatRegion.DARK_SEA) {
            return false;
        }
        boolean added = unlocked.add(region);
        if (added) {
            setDirty();
        }
        return added;
    }

    public Set<TeyvatRegion> getUnlockedRegions() {
        return EnumSet.copyOf(unlocked);
    }

    public void enforceBarrier(ServerPlayer player) {
        if (!TeyvatWorldConfig.get().enableLockedRegionBarrier || player.isCreative() || player.isSpectator()) {
            return;
        }
        TeyvatRegion current = RegionMacroMap.getInstance()
                .sampleRegion(player.getBlockX(), player.getBlockZ());
        if (isUnlocked(current)) {
            return;
        }

        double anchorX = TeyvatDimensions.MONDSTADT_CENTER_X * worldScale;
        double anchorZ = TeyvatDimensions.MONDSTADT_CENTER_Z * worldScale;
        double dx = anchorX - player.getX();
        double dz = anchorZ - player.getZ();
        double len = Math.max(1.0, Math.hypot(dx, dz));
        Vec3 push = new Vec3((dx / len) * 0.85, 0.12, (dz / len) * 0.85);
        player.setDeltaMovement(push);
        player.hurtMarked = true;

        if (player.tickCount % 30 == 0) {
            player.displayClientMessage(
                    Component.translatable("message." + TeyvatDimensions.NAMESPACE + ".region_locked",
                            current.serializedName()),
                    true
            );
        }
    }
}
