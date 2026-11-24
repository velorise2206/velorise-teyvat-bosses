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
 * BlockEntity for {@code statue_of_the_seven_mondstadt_v3.bbmodel}
 * ({@code dormant}, {@code unlock}, {@code idle}, {@code heal_blessing}, {@code resonate_anemo}).
 */
public class StatueOfTheSevenBlockEntity extends BlockEntity {
    public enum AnimState { DORMANT, UNLOCK, IDLE, HEAL_BLESSING, RESONATE_ANEMO }

    private static Supplier<BlockEntityType<StatueOfTheSevenBlockEntity>> typeSupplier;
    private String statueId = "statue_mondstadt";
    private String statueName = "Statue of The Seven";
    private AnimState animState = AnimState.DORMANT;

    public StatueOfTheSevenBlockEntity(BlockPos pos, BlockState state) {
        super(typeSupplier.get(), pos, state);
    }

    public static void bindType(Supplier<BlockEntityType<StatueOfTheSevenBlockEntity>> supplier) {
        typeSupplier = supplier;
    }

    public void configure(String id, String name) {
        this.statueId = id;
        this.statueName = name;
        setChanged();
    }

    public String getStatueId() { return statueId; }
    public String getStatueName() { return statueName; }
    public AnimState getAnimState() { return animState; }

    public void setAnimState(AnimState next) {
        this.animState = next;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("StatueId", statueId);
        tag.putString("StatueName", statueName);
        tag.putString("AnimState", animState.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("StatueId")) statueId = tag.getString("StatueId");
        if (tag.contains("StatueName")) statueName = tag.getString("StatueName");
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
