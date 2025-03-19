package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

@Config(modid = ImInDanger.MODID)
public class DangerConfig
{
    @Config.Name("030 Danger Fade-In Time")
    @Config.LangKey(ImInDanger.MODID + ".config.dangerFadeInTime")
    @Config.Comment(
            {
                    "How long the danger indicator and heartbeat take to fade in when danger is detected, in milliseconds (1000 = 1 second)"
            })
    @Config.RangeInt(min = 0)
    public static int dangerIndicatorFadeInTime = 0;

    @Config.Name("040 Danger Fade-Out Time")
    @Config.LangKey(ImInDanger.MODID + ".config.dangerFadeOutTime")
    @Config.Comment(
            {
                    "How long the danger indicator and heartbeat take to fade out when no danger is detected, in milliseconds (1000 = 1 second)"
            })
    @Config.RangeInt(min = 0)
    public static int dangerIndicatorFadeOutTime = 1000;

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
