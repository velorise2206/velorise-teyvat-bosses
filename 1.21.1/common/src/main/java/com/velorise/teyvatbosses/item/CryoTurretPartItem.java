package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.entity.CryoTurretEntity;
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
 * One of the 3 modular Cryo Regisvine salvage parts (Root, Stem, Corolla) used to assemble
 * a 3x6x3 miniature defensive Cryo Turret outdoors.
 * Right-clicking the ground with the Root deploys the 1/3 chassis; Stem & Corolla weld onto it.
 */
public class CryoTurretPartItem extends Item {
    private final int partBit;

    public CryoTurretPartItem(int partBit, Properties properties) {
        super(properties);
        this.partBit = partBit;
    }

    public int getPartBit() {
        return partBit;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (partBit != CryoTurretEntity.PART_ROOT) {
            return InteractionResult.PASS;
        }
        Level level = ctx.getLevel();
        if (!level.isClientSide() && level instanceof ServerLevel sl) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            Vec3 spawnPos = new Vec3(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            CryoTurretEntity turret = TeyvatBossesEntities.cryoTurret().create(sl);
            if (turret != null) {
                float yaw = ctx.getPlayer() != null ? ctx.getPlayer().getYRot() + 180.0F : 0.0F;
                turret.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, yaw, 0.0F);
                turret.setYBodyRot(yaw);
                turret.setYHeadRot(yaw);
                turret.initAssemblyChassis();
                sl.addFreshEntity(turret);
                sl.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.GLASS_PLACE, SoundSource.PLAYERS, 1.2F, 0.90F);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, spawnPos.x, spawnPos.y + 0.8D, spawnPos.z, 22, 0.6D, 0.4D, 0.6D, 0.04D);
                if (ctx.getPlayer() != null) {
                    if (!ctx.getPlayer().getAbilities().instabuild) {
                        ctx.getItemInHand().shrink(1);
                    }
                    ctx.getPlayer().displayClientMessage(Component.translatable("message.velorise_teyvat_bosses.cryo_turret.root_placed"), true);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
