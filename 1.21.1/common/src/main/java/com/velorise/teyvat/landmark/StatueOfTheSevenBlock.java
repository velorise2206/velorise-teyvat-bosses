package com.velorise.teyvat.landmark;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Mondstadt Statue of The Seven rendered via {@code statue_of_the_seven_mondstadt_v3.bbmodel}
 * (animations: {@code dormant}, {@code unlock}, {@code idle}, {@code heal_blessing}, {@code resonate_anemo}).
 */
public class StatueOfTheSevenBlock extends BaseEntityBlock {
    public static final BooleanProperty UNLOCKED = BooleanProperty.create("unlocked");
    public static final MapCodec<StatueOfTheSevenBlock> CODEC = simpleCodec(StatueOfTheSevenBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 48.0, 16.0);

    public StatueOfTheSevenBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNLOCKED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNLOCKED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer sp && level.getBlockEntity(pos) instanceof StatueOfTheSevenBlockEntity be) {
            if (!state.getValue(UNLOCKED)) {
                level.setBlock(pos, state.setValue(UNLOCKED, true), Block.UPDATE_ALL);
                be.setAnimState(StatueOfTheSevenBlockEntity.AnimState.UNLOCK);
                sp.displayClientMessage(Component.translatable("message.veloriseteyvat.statue_unlocked", be.getStatueName()), true);
            } else {
                sp.setHealth(sp.getMaxHealth());
                sp.getFoodData().setFoodLevel(20);
                be.setAnimState(StatueOfTheSevenBlockEntity.AnimState.HEAL_BLESSING);
                sp.displayClientMessage(Component.translatable("message.veloriseteyvat.statue_blessing"), true);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StatueOfTheSevenBlockEntity(pos, state);
    }
}
