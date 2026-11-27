package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Anemo Gale Boots crafted by flanking Diamond Boots with two surplus Anemo Prism Cubes.
 * Grants a mid-air double jump with fall-distance reset and wind burst effects.
 */
public class AnemoBootsItem extends ArmorItem {

    private static final Set<UUID> USED_AIR_JUMP = ConcurrentHashMap.newKeySet();

    public AnemoBootsItem(Holder<ArmorMaterial> material, Properties properties) {
        super(material, Type.BOOTS, properties.durability(Type.BOOTS.getDurability(35)).rarity(Rarity.EPIC));
    }

    public static ArmorMaterial createArmorMaterial() {
        EnumMap<Type, Integer> defense = Util.make(new EnumMap<>(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 6);
            map.put(Type.HELMET, 2);
            map.put(Type.BODY, 5);
        });
        return new ArmorMaterial(
                defense,
                18,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                () -> {
                    var cube = TeyvatBossesItems.anemoPrismCube();
                    return cube != null ? Ingredient.of(cube) : Ingredient.of(Items.BREEZE_ROD);
                },
                List.of(new ArmorMaterial.Layer(TeyvatBossesConstants.id("anemo"))),
                2.0F,
                0.0F
        );
    }

    public static boolean isWearing(LivingEntity entity) {
        return entity != null && entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof AnemoBootsItem;
    }

    public static void resetAirJump(UUID playerId) {
        USED_AIR_JUMP.remove(playerId);
    }

    public static boolean tryConsumeAirJump(UUID playerId) {
        return USED_AIR_JUMP.add(playerId);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player)) return;
        if (player.getItemBySlot(EquipmentSlot.FEET) == stack) {
            if (player.onGround() || player.isInWater() || player.isInLava() || player.onClimbable() || player.getAbilities().flying) {
                resetAirJump(player.getUUID());
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.anemo_boots").withStyle(ChatFormatting.AQUA));
    }
}
