package com.velorise.teyvat.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * BlockEntity driving the 4 animation states in {@code celestia_portal_gate.bbmodel}:
 * {@code idle_closed}, {@code portal_open}, {@code idle_open}, {@code portal_close}.
 */
public class CelestiaPortalBlockEntity extends BlockEntity {
    public enum PortalAnimState {
        IDLE_CLOSED("idle_closed", -1),
        PORTAL_OPEN("portal_open", 44),
        IDLE_OPEN("idle_open", 200),
        PORTAL_CLOSE("portal_close", 36);

        private final String animationName;
        private final int durationTicks;

        PortalAnimState(String animationName, int durationTicks) {
            this.animationName = animationName;
            this.durationTicks = durationTicks;
        }

        public String animationName() {
            return animationName;
        }

        public int durationTicks() {
            return durationTicks;
        }
    }

    private static Supplier<BlockEntityType<CelestiaPortalBlockEntity>> typeSupplier;

    private PortalAnimState animState = PortalAnimState.IDLE_CLOSED;
    private int stateTicks = 0;

    public CelestiaPortalBlockEntity(BlockPos pos, BlockState state) {
        super(requireType(), pos, state);
    }

    public static void bindType(Supplier<BlockEntityType<CelestiaPortalBlockEntity>> supplier) {
        typeSupplier = supplier;
    }

    private static BlockEntityType<CelestiaPortalBlockEntity> requireType() {
        if (typeSupplier == null) {
            throw new IllegalStateException("CelestiaPortalBlockEntity type not bound by loader yet");
        }
        return typeSupplier.get();
    }

    public PortalAnimState getAnimState() {
        return animState;
    }

    public int getStateTicks() {
        return stateTicks;
    }

    public void triggerOpen() {
        if (animState == PortalAnimState.IDLE_CLOSED || animState == PortalAnimState.PORTAL_CLOSE) {
            setAnimState(PortalAnimState.PORTAL_OPEN);
        } else if (animState == PortalAnimState.IDLE_OPEN) {
            stateTicks = 0; // reset auto-close timer
        }
    }

    private void setAnimState(PortalAnimState next) {
        this.animState = next;
        this.stateTicks = 0;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CelestiaPortalBlockEntity be) {
        be.stateTicks++;
        if (level.isClientSide) {
            return;
        }
        int dur = be.animState.durationTicks();
        if (dur > 0 && be.stateTicks >= dur) {
            switch (be.animState) {
                case PORTAL_OPEN -> be.setAnimState(PortalAnimState.IDLE_OPEN);
                case IDLE_OPEN -> {
                    be.setAnimState(PortalAnimState.PORTAL_CLOSE);
                    if (state.hasProperty(CelestiaPortalBlock.OPEN)) {
                        level.setBlock(pos, state.setValue(CelestiaPortalBlock.OPEN, false), Block.UPDATE_ALL);
                    }
                }
                case PORTAL_CLOSE -> be.setAnimState(PortalAnimState.IDLE_CLOSED);
                case IDLE_CLOSED -> {}
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("AnimState", animState.name());
        tag.putInt("StateTicks", stateTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("AnimState")) {
            try {
                animState = PortalAnimState.valueOf(tag.getString("AnimState"));
            } catch (IllegalArgumentException ignored) {
                animState = PortalAnimState.IDLE_CLOSED;
            }
        }
        stateTicks = tag.getInt("StateTicks");
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
