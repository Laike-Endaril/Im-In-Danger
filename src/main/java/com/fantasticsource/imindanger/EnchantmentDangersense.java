package com.fantasticsource.imindanger;

import com.fantasticsource.imindanger.config.DangerConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.imindanger.ImInDanger.MODID;

public class EnchantmentDangersense extends Enchantment
{
    public static EnchantmentDangersense enchantmentDangersense = null;

    protected EnchantmentDangersense()
    {
        super(Rarity.VERY_RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
        setRegistryName(new ResourceLocation(MODID, "dangersense"));
        setName(MODID + ".dangersense");

        enchantmentDangersense = this;
    }


    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return enchantmentLevel * 25;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel)
    {
        return getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }


    public static void init()
    {
        if (DangerConfig.dangersenseMode == 2 || DangerConfig.dangersenseMode == 3) MinecraftForge.EVENT_BUS.register(EnchantmentDangersense.class);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        event.getRegistry().register(new EnchantmentDangersense());
    }
}
