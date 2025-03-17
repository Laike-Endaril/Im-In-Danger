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
}
