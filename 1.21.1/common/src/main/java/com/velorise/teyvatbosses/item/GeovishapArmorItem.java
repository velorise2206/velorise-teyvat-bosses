package com.velorise.teyvatbosses.item;

import com.velorise.teyvatbosses.TeyvatBossesConstants;
import com.velorise.teyvatbosses.registry.TeyvatBossesItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

/**
 * Primo Geovishap Dragon-Scale Armor set forged from Primordial Jade &amp; Geo Scale Ingots.
 * <ul>
 *   <li><b>2-Piece (Geo Scales):</b> Grants Resistance I and counts as an active Geo Shield to
 *       reflect Primo Geovishap's {@code Primordial Shower}.</li>
 *   <li><b>4-Piece (Primordial Aegis):</b> Grants Fire Resistance and auto-recharges a +8 HP
 *       Absorption Geo Shield every 15 seconds.</li>
 * </ul>
 */
public class GeovishapArmorItem extends ArmorItem {

    private static final int SHIELD_RECHARGE_TICKS = 300; // 15 seconds

    public GeovishapArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties.durability(type.getDurability(42)).fireResistant().rarity(Rarity.EPIC));
    }

    public static ArmorMaterial createArmorMaterial() {
        EnumMap<Type, Integer> defense = Util.make(new EnumMap<>(Type.class), map -> {
            map.put(Type.BOOTS, 3);
            map.put(Type.LEGGINGS, 6);
            map.put(Type.CHESTPLATE, 8);
            map.put(Type.HELMET, 3);
            map.put(Type.BODY, 11);
        });
        return new ArmorMaterial(
                defense,
                18,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> {
                    var ingot = TeyvatBossesItems.primordialGeoIngot();
                    var jade = TeyvatBossesItems.primordialJade();
                    if (ingot != null && jade != null) return Ingredient.of(ingot, jade);
                    if (ingot != null) return Ingredient.of(ingot);
                    return Ingredient.of(Items.NETHERITE_INGOT);
                },
                List.of(new ArmorMaterial.Layer(TeyvatBossesConstants.id("geovishap"))),
                3.5F,
                0.15F
        );
    }

    public static int countEquippedPieces(LivingEntity entity) {
        int count = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (entity.getItemBySlot(slot).getItem() instanceof GeovishapArmorItem) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player)) return;
        // Run set bonus logic once per player tick from the chestplate (or helmet if no chestplate)
        if (this.type == Type.CHESTPLATE && player.getItemBySlot(EquipmentSlot.CHEST) == stack) {
            applySetBonuses(player);
        } else if (this.type == Type.HELMET && player.getItemBySlot(EquipmentSlot.HEAD) == stack
                && !(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GeovishapArmorItem)) {
            applySetBonuses(player);
        } else if (this.type == Type.LEGGINGS && player.getItemBySlot(EquipmentSlot.LEGS) == stack
                && !(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof GeovishapArmorItem)
                && !(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof GeovishapArmorItem)) {
            applySetBonuses(player);
        }
    }

    private static void applySetBonuses(Player player) {
        int pieces = countEquippedPieces(player);
        if (pieces < 2) return;
        if (player.tickCount % 20 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, 0, true, false, true));
            if (pieces >= 4) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 45, 0, true, false, true));
            }
        }
        if (pieces >= 4 && player.tickCount % SHIELD_RECHARGE_TICKS == 0) {
            if (player.getAbsorptionAmount() < 8.0F) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 340, 1, true, false, true));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.geovishap_armor.set_2pc").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.velorise_teyvat_bosses.geovishap_armor.set_4pc").withStyle(ChatFormatting.YELLOW));
    }
}
