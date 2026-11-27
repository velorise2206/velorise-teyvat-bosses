package com.velorise.teyvatbosses.entity;

import com.velorise.teyvatbosses.registry.TeyvatBossesEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Cross-loader (Fabric + NeoForge) multipart hitbox child entity for {@link PrimoGeovishapEntity}.
 *
 * <p>Provides dedicated pickable/attackable hitboxes for the Primo Geovishap's head and tail so melee
 * strikes, arrows, and elemental spells register accurately across its full ~9-block length without
 * relying on loader-specific multipart hooks or Mixins. Never saved to chunk NBT.
 */
public class PrimoGeovishapPartEntity extends Entity {
    public static final int PART_HEAD = 0;
    public static final int PART_TAIL = 1;

    private static final EntityDimensions HEAD_SIZE = EntityDimensions.scalable(2.4F, 2.3F);
    private static final EntityDimensions TAIL_SIZE = EntityDimensions.scalable(2.3F, 2.0F);

    private static final EntityDataAccessor<Integer> DATA_PART_KIND =
            SynchedEntityData.defineId(PrimoGeovishapPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PARENT_ID =
            SynchedEntityData.defineId(PrimoGeovishapPartEntity.class, EntityDataSerializers.INT);

    @Nullable
    private PrimoGeovishapEntity parent;
    @Nullable
    private UUID parentUuid;

    public PrimoGeovishapPartEntity(EntityType<? extends PrimoGeovishapPartEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static PrimoGeovishapPartEntity spawn(ServerLevel level, PrimoGeovishapEntity owner, int partKind) {
        PrimoGeovishapPartEntity part = new PrimoGeovishapPartEntity(TeyvatBossesEntities.primoGeovishapPart(), level);
        part.parent = owner;
        part.parentUuid = owner.getUUID();
        part.entityData.set(DATA_PART_KIND, partKind);
        part.entityData.set(DATA_PARENT_ID, owner.getId());
        part.setPos(owner.getX(), owner.getY(), owner.getZ());
        part.refreshDimensions();
        level.addFreshEntity(part);
        return part;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_PART_KIND, PART_HEAD);
        builder.define(DATA_PARENT_ID, -1);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_PART_KIND.equals(key)) {
            refreshDimensions();
        }
    }

    public int getPartKind() {
        return entityData.get(DATA_PART_KIND);
    }

    @Nullable
    public PrimoGeovishapEntity getParentBoss() {
        if (parent != null && !parent.isRemoved()) return parent;
        if (level().isClientSide()) {
            int pid = entityData.get(DATA_PARENT_ID);
            if (pid >= 0 && level().getEntity(pid) instanceof PrimoGeovishapEntity boss) {
                parent = boss;
                return boss;
            }
        } else if (parentUuid != null && level() instanceof ServerLevel sl) {
            if (sl.getEntity(parentUuid) instanceof PrimoGeovishapEntity boss) {
                parent = boss;
                return boss;
            }
        }
        return null;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getPartKind() == PART_TAIL ? TAIL_SIZE : HEAD_SIZE;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            PrimoGeovishapEntity boss = getParentBoss();
            if (boss == null || !boss.isAlive() || boss.isRemoved() || boss.getBossState() == PrimoGeovishapEntity.STATE_DEATH) {
                discard();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        PrimoGeovishapEntity boss = getParentBoss();
        if (boss == null || !boss.isAlive()) return false;
        return boss.hurtFromPart(this, source, amount);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || (parent != null && parent == entity);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public Component getName() {
        String suffix = (getPartKind() == PART_TAIL) ? " (Tail)" : " (Head)";
        return Component.literal("Primo Geovishap" + suffix);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}
}
