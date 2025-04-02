package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

@Config(modid = ImInDanger.MODID)
public class DangerConfig
{
    @Config.Name("010 Dangersense Mode")
    @Config.LangKey(ImInDanger.MODID + ".config.dangersenseMode")
    @Config.Comment(
            {
                    "How players can obtain dangersense",
                    "Clients must have the same setting as the server in order to function correctly!",
                    "",
                    "0 = Players always have dangersense",
                    "",
                    "1 = Players have dangersense when using a dangersense potion (adds potion items to the world!)",
                    "",
                    "2 = Players have dangersense when using a dangersense enchantment (adds enchantment to the world!)",
                    "",
                    "3 = Players have dangersense when using a dangersense potion or enchantment (adds potion items and an enchantment to the world!)"
            })
    @Config.RangeInt(min = 0, max = 3)
    @Config.RequiresMcRestart
    public static int dangersenseMode = 0;

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
