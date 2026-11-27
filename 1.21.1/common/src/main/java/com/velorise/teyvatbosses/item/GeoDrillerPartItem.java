package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.DeviceEnergyHelper;
import com.velorise.teyvatbosses.entity.GeoDrillerEntity;
import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * One of the 3 modular Geo Driller salvage parts dropped by Primo Geovishap (Chassis, Bore Head, Reactor Engine).
 * Right-click the ground with the Chassis to deploy (1/3), then weld Bore Head & Reactor Engine onto it.
 */
public class GeoDrillerPartItem extends Item {
    private final int partBit;

    public GeoDrillerPartItem(int partBit, Properties properties) {
        super(properties);
        this.partBit = partBit;
    }

    public int getPartBit() {
        return partBit;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (partBit != GeoDrillerEntity.PART_CHASSIS) {
            return InteractionResult.PASS;
        }
        Level level = ctx.getLevel();
        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            Vec3 spawnPos = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            GeoDrillerEntity driller = TeyvatBossesEntities.geoDriller().create(sl);
            if (driller != null) {
                float yaw = ctx.getPlayer() != null ? ctx.getPlayer().getYRot() : 0.0F;
                driller.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0F);
                driller.setYBodyRot(yaw);
                driller.setYHeadRot(yaw);
                driller.initAssemblyChassis();
                driller.setCoreEnchants(DeviceEnergyHelper.getCapacityLevel(sl, ctx.getItemInHand()), DeviceEnergyHelper.getConservationLevel(sl, ctx.getItemInHand()));
                sl.addFreshEntity(driller);
                sl.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 1.1F, 0.85F);
                sl.sendParticles(ParticleTypes.CRIT, spawnPos.x, spawnPos.y + 0.8D, spawnPos.z, 20, 0.7D, 0.4D, 0.7D, 0.05D);
                if (ctx.getPlayer() != null) {
                    if (!ctx.getPlayer().getAbilities().instabuild) {
                        ctx.getItemInHand().shrink(1);
                    }
                    ctx.getPlayer().displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.geo_driller.chassis_placed")
                            .withStyle(ChatFormatting.YELLOW), true);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.velorise_teyvat_bosses.geo_driller_part").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return partBit == GeoDrillerEntity.PART_CHASSIS;
    }

    @Override
    public int getEnchantmentValue() {
        return partBit == GeoDrillerEntity.PART_CHASSIS ? 15 : 0;
    }
}
