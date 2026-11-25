package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Genshin-style Ley Line Respawn Blossom left behind when a World Boss is defeated.
 * Counts down (default 2 minutes / 2400 ticks) and automatically respawns the boss at its exact arena anchor.
 *
 * <p>Creative-mode convenience: Right-click to respawn immediately; Left-click to remove permanently.
 */
public class BossRespawnAnchorEntity extends Entity {

    public static final int BOSS_CRYO_REGISVINE = 0;
    public static final int BOSS_THE_OPEN_EYED  = 1;
    public static final int DEFAULT_RESPAWN_TICKS = 2400; // 2 minutes

    private static final EntityDataAccessor<Integer> DATA_BOSS_KIND =
            SynchedEntityData.defineId(BossRespawnAnchorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TICKS_LEFT =
            SynchedEntityData.defineId(BossRespawnAnchorEntity.class, EntityDataSerializers.INT);

    private float savedYaw = 0.0F;

    public BossRespawnAnchorEntity(EntityType<? extends BossRespawnAnchorEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static BossRespawnAnchorEntity spawn(ServerLevel level, int bossKind, Vec3 pos, float savedYaw, int respawnTicks) {
        BossRespawnAnchorEntity anchor = new BossRespawnAnchorEntity(TeyvatBossesEntities.bossRespawnAnchor(), level);
        anchor.setPos(pos.x, pos.y, pos.z);
        anchor.savedYaw = savedYaw;
        anchor.entityData.set(DATA_BOSS_KIND, bossKind);
        anchor.entityData.set(DATA_TICKS_LEFT, Math.max(40, respawnTicks));
        anchor.setCustomNameVisible(true);
        anchor.updateTimerName();
        level.addFreshEntity(anchor);
        return anchor;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BOSS_KIND, BOSS_CRYO_REGISVINE);
        builder.define(DATA_TICKS_LEFT, DEFAULT_RESPAWN_TICKS);
    }

    public int getBossKind() { return entityData.get(DATA_BOSS_KIND); }
    public int getTicksLeft() { return entityData.get(DATA_TICKS_LEFT); }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && source.getEntity() instanceof Player p && p.isCreative()) {
            discard();
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!level().isClientSide() && player.isCreative() && level() instanceof ServerLevel sl) {
            respawnBossNow(sl);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            if (tickCount % 4 == 0) {
                var p = (getBossKind() == BOSS_CRYO_REGISVINE) ? ParticleTypes.SNOWFLAKE : ParticleTypes.END_ROD;
                level().addParticle(p,
                        getX() + (random.nextDouble() - 0.5D) * 1.2D,
                        getY() + 0.6D + random.nextDouble() * 1.2D,
                        getZ() + (random.nextDouble() - 0.5D) * 1.2D,
                        0.0D, 0.04D, 0.0D);
            }
            return;
        }

        ServerLevel sl = (ServerLevel) level();
        int left = getTicksLeft() - 1;
        entityData.set(DATA_TICKS_LEFT, Math.max(0, left));

        if (left % 20 == 0) {
            updateTimerName();
        }
        if (left <= 0) {
            respawnBossNow(sl);
        }
    }

    private void updateTimerName() {
        int secs = Math.max(0, (getTicksLeft() + 19) / 20);
        int mins = secs / 60;
        int rem = secs % 60;
        String bossKey = (getBossKind() == BOSS_CRYO_REGISVINE)
                ? "entity.velorise_teyvat_bosses.cryo_regisvine"
                : "entity.velorise_teyvat_bosses.the_open_eyed";
        ChatFormatting col = (getBossKind() == BOSS_CRYO_REGISVINE) ? ChatFormatting.AQUA : ChatFormatting.GOLD;
        setCustomName(Component.translatable(bossKey)
                .append(Component.literal(String.format(" — %d:%02d", mins, rem)))
                .withStyle(col));
    }

    private void respawnBossNow(ServerLevel sl) {
        if (getBossKind() == BOSS_CRYO_REGISVINE) {
            CryoRegisvineEntity boss = TeyvatBossesEntities.cryoRegisvine().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.5D, getZ(), 48, 1.8D, 1.5D, 1.8D, 0.1D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.4F, 0.9F);
            }
        } else {
            TheOpenEyedEntity boss = TeyvatBossesEntities.theOpenEyed().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.5D, getZ(), 48, 1.6D, 2.0D, 1.6D, 0.1D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.4F, 1.1F);
            }
        }
        discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BossKind")) entityData.set(DATA_BOSS_KIND, tag.getInt("BossKind"));
        if (tag.contains("TicksLeft")) entityData.set(DATA_TICKS_LEFT, tag.getInt("TicksLeft"));
        savedYaw = tag.getFloat("SavedYaw");
        setCustomNameVisible(true);
        updateTimerName();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("BossKind", getBossKind());
        tag.putInt("TicksLeft", getTicksLeft());
        tag.putFloat("SavedYaw", savedYaw);
    }
}
