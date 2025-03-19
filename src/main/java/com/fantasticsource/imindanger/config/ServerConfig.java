package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("050 Sneaky Entities")
    @Config.LangKey(ImInDanger.MODID + ".config.server.sneakyEntities")
    @Config.Comment(
            {
                    "Entities that won't trigger the danger mode (good for surprise attack entities), eg...",
                    "minecraft:skeleton"
            })
    public String[] sneakyEntities = new String[]{};

    @Config.Name("070 Sneaky Potions")
    @Config.LangKey(ImInDanger.MODID + ".config.server.sneakyPotions")
    @Config.Comment(
            {
                    "If an entity has one of these potions active, they will not trigger the danger mode, eg...",
                    "minecraft:invisibility"
            })
    public String[] sneakyPotions = {};
}
