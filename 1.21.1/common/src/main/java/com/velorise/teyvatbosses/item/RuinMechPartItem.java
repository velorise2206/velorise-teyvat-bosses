package com.velorise.teyvatbosses.item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import com.velorise.teyvatbosses.entity.RuinMechEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * One of the 5 modular Ruin Guard salvage parts used to assemble a pilotable Ruin Mech outdoors.
 * Right-clicking the ground with the Core places the outdoor assembly chassis;
 * the remaining 4 limb parts are right-clicked directly onto the chassis to weld them on.
 */
public class RuinMechPartItem extends Item {
    private final int partBit;

    public RuinMechPartItem(int partBit, Properties properties) {
        super(properties);
        this.partBit = partBit;
    }

    public int getPartBit() {
        return partBit;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (partBit != RuinMechEntity.PART_CORE) {
            return InteractionResult.PASS;
        }
        Level level = ctx.getLevel();
        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            Vec3 spawnPos = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            RuinMechEntity mech = TeyvatBossesEntities.ruinMech().create(sl);
            if (mech != null) {
                float yaw = ctx.getPlayer() != null ? ctx.getPlayer().getYRot() + 180.0F : 0.0F;
                mech.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0F);
                mech.setYBodyRot(yaw);
                mech.setYHeadRot(yaw);
                mech.initAssemblyChassis();
                sl.addFreshEntity(mech);
                sl.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1.3F, 0.72F);
                sl.sendParticles(ParticleTypes.FLAME, spawnPos.x, spawnPos.y + 1.2D, spawnPos.z, 18, 0.6D, 0.5D, 0.6D, 0.05D);
                if (ctx.getPlayer() != null) {
                    if (!ctx.getPlayer().getAbilities().instabuild) {
                        ctx.getItemInHand().shrink(1);
                    }
                    ctx.getPlayer().displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.mech.chassis_placed"), true);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.velorise_teyvat_bosses.ruin_mech_part").withStyle(ChatFormatting.GRAY));
    }
}
