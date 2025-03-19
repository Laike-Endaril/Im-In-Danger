package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

@Config(modid = ImInDanger.MODID)
public class DangerConfig
{
    @Config.Name("Server")
    @Config.LangKey(ImInDanger.MODID + ".config.server")
    public static ServerConfig serverSettings = new ServerConfig();

    @Config.Name("Sound")
    @Config.LangKey(ImInDanger.MODID + ".config.sound")
    public static SoundConfig soundSettings = new SoundConfig();

    @Config.Name("Visuals")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals")
    public static VisualsConfig visualSettings = new VisualsConfig();
}
