package com.velorise.teyvatbosses.block;

import com.velorise.teyvatbosses.registry.TeyvatBossesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Stores toggleable animated/static display state for BossTrophyBlock.
 */
public class BossTrophyBlockEntity extends BlockEntity {
    private boolean animated = false;

    public BossTrophyBlockEntity(BlockPos pos, BlockState state) {
        super(TeyvatBossesBlocks.bossTrophyBe(), pos, state);
    }

    public boolean isAnimated() {
        return animated;
    }

    public boolean toggleAnimated() {
        this.animated = !this.animated;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return this.animated;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Animated", animated);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Animated")) {
            this.animated = tag.getBoolean("Animated");
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
