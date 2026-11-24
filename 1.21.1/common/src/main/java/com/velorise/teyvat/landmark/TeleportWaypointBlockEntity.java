package com.velorise.teyvat.landmark;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * BlockEntity for {@code teleport_waypoint_v3.bbmodel}
 * ({@code dormant}, {@code unlock}, {@code idle}, {@code teleport_out}, {@code teleport_in}).
 */
public class TeleportWaypointBlockEntity extends BlockEntity {
    public enum AnimState { DORMANT, UNLOCK, IDLE, TELEPORT_OUT, TELEPORT_IN }

    private static Supplier<BlockEntityType<TeleportWaypointBlockEntity>> typeSupplier;
    private String waypointId = "unknown";
    private String waypointName = "Teleport Waypoint";
    private AnimState animState = AnimState.DORMANT;

    public TeleportWaypointBlockEntity(BlockPos pos, BlockState state) {
        super(typeSupplier.get(), pos, state);
    }

    public static void bindType(Supplier<BlockEntityType<TeleportWaypointBlockEntity>> supplier) {
        typeSupplier = supplier;
    }

    public void configure(String id, String name) {
        this.waypointId = id;
        this.waypointName = name;
        setChanged();
    }

    public String getWaypointId() { return waypointId; }
    public String getWaypointName() { return waypointName; }
    public AnimState getAnimState() { return animState; }

    public void playUnlock() {
        this.animState = AnimState.UNLOCK;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("WaypointId", waypointId);
        tag.putString("WaypointName", waypointName);
        tag.putString("AnimState", animState.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("WaypointId")) waypointId = tag.getString("WaypointId");
        if (tag.contains("WaypointName")) waypointName = tag.getString("WaypointName");
        if (tag.contains("AnimState")) {
            try { animState = AnimState.valueOf(tag.getString("AnimState")); }
            catch (IllegalArgumentException ignored) { animState = AnimState.DORMANT; }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
