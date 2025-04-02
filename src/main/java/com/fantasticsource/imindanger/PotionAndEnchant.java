package com.fantasticsource.imindanger;

import com.fantasticsource.imindanger.config.DangerConfig;
import com.fantasticsource.mctools.potions.BetterPotion;
import com.fantasticsource.mctools.potions.BetterPotionType;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static com.fantasticsource.imindanger.ImInDanger.MODID;

public class PotionAndEnchant
{
    public static BetterPotion potionDangersense = null;
    public static BetterPotionType potionTypeDangersense = null, potionTypeLongDangersense = null;

    public static void init()
    {
        switch (DangerConfig.dangersenseMode)
        {
            case 1:
                potionDangersense = new BetterPotion(new ResourceLocation(MODID, "dangersense"), new ResourceLocation(MODID, "potions/dangersense.png"), false, false, 0xBB7755);
                potionTypeDangersense = new BetterPotionType(PotionTypes.THICK, new ItemStack(Items.SPIDER_EYE), new FantasticPotionEffect(potionDangersense, 3600));
                potionTypeLongDangersense = potionTypeDangersense.getLongDurationVersion();
                break;

            case 2:
                break;

            case 3:
                potionDangersense = new BetterPotion(new ResourceLocation(MODID, "dangersense"), new ResourceLocation(MODID, "potions/dangersense.png"), false, false, 0xBB7755);
                potionTypeDangersense = new BetterPotionType(PotionTypes.THICK, new ItemStack(Items.SPIDER_EYE), new FantasticPotionEffect(potionDangersense, 3600));
                potionTypeLongDangersense = potionTypeDangersense.getLongDurationVersion();
                break;
        }
    }
}
