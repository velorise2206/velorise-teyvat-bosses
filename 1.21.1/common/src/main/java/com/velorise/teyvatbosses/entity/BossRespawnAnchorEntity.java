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
 */
public class BossRespawnAnchorEntity extends Entity {

    public static final int BOSS_CRYO_REGISVINE   = 0;
    public static final int BOSS_THE_OPEN_EYED    = 1;
    public static final int BOSS_ANEMO_HYPOSTASIS = 2;
    public static final int BOSS_RUIN_GUARD       = 3;
    public static final int BOSS_ASTRALION        = 4;
    public static final int BOSS_RUIN_NAUTILUS    = 5;
    public static final int DEFAULT_RESPAWN_TICKS = 2400;

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
                var p = switch (getBossKind()) {
                    case BOSS_CRYO_REGISVINE -> ParticleTypes.SNOWFLAKE;
                    case BOSS_ANEMO_HYPOSTASIS -> ParticleTypes.GLOW;
                    case BOSS_RUIN_GUARD -> ParticleTypes.FLAME;
                    case BOSS_RUIN_NAUTILUS -> ParticleTypes.BUBBLE;
                    default -> ParticleTypes.END_ROD;
                };
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
        if (left % 20 == 0) updateTimerName();
        if (left <= 0) respawnBossNow(sl);
    }

    private void updateTimerName() {
        int secs = Math.max(0, (getTicksLeft() + 19) / 20);
        int mins = secs / 60;
        int rem = secs % 60;
        String bossKey = switch (getBossKind()) {
            case BOSS_CRYO_REGISVINE -> "entity.velorise_teyvat_bosses.cryo_regisvine";
            case BOSS_ANEMO_HYPOSTASIS -> "entity.velorise_teyvat_bosses.anemo_hypostasis";
            case BOSS_RUIN_GUARD -> "entity.velorise_teyvat_bosses.ruin_guard";
            case BOSS_ASTRALION -> "entity.velorise_teyvat_bosses.astralion";
            case BOSS_RUIN_NAUTILUS -> "entity.velorise_teyvat_bosses.ruin_nautilus";
            default -> "entity.velorise_teyvat_bosses.the_open_eyed";
        };
        ChatFormatting col = switch (getBossKind()) {
            case BOSS_CRYO_REGISVINE -> ChatFormatting.AQUA;
            case BOSS_ANEMO_HYPOSTASIS -> ChatFormatting.GREEN;
            case BOSS_RUIN_GUARD -> ChatFormatting.GOLD;
            case BOSS_ASTRALION -> ChatFormatting.LIGHT_PURPLE;
            case BOSS_RUIN_NAUTILUS -> ChatFormatting.AQUA;
            default -> ChatFormatting.YELLOW;
        };
        setCustomName(Component.translatable(bossKey)
                .append(Component.literal(String.format(" — %d:%02d", mins, rem)))
                .withStyle(col));
    }

    private void respawnBossNow(ServerLevel sl) {
        int kind = getBossKind();
        if (kind == BOSS_CRYO_REGISVINE) {
            CryoRegisvineEntity boss = TeyvatBossesEntities.cryoRegisvine().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 1.5D, getZ(), 48, 1.8D, 1.5D, 1.8D, 0.1D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.4F, 0.9F);
            }
        } else if (kind == BOSS_ANEMO_HYPOSTASIS) {
            AnemoHypostasisEntity boss = TeyvatBossesEntities.anemoHypostasis().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 1.8D, getZ(), 48, 1.6D, 1.6D, 1.6D, 0.1D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.HOSTILE, 1.4F, 1.15F);
            }
        } else if (kind == BOSS_RUIN_GUARD) {
            RuinGuardEntity boss = TeyvatBossesEntities.ruinGuard().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.FLAME, getX(), getY() + 2.0D, getZ(), 48, 1.6D, 1.8D, 1.6D, 0.09D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.5F, 0.72F);
            }
        } else if (kind == BOSS_RUIN_NAUTILUS) {
            RuinNautilusEntity boss = TeyvatBossesEntities.ruinNautilus().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), getYRot(), 0.0F);
                sl.addFreshEntity(boss);
            }
        } else if (kind == BOSS_ASTRALION) {
            AstralionEntity boss = TeyvatBossesEntities.astralion().create(sl);
            if (boss != null) {
                boss.moveTo(getX(), getY(), getZ(), savedYaw, 0.0F);
                sl.addFreshEntity(boss);
                sl.sendParticles(ParticleTypes.END_ROD, getX(), getY() + 2.6D, getZ(), 56, 2.0D, 2.2D, 2.0D, 0.12D);
                sl.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.4F, 1.15F);
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
