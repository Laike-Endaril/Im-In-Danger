package com.fantasticsource.imindanger;

import net.minecraftforge.common.config.Config;

@Config(modid = ImInDanger.MODID)
public class DangerConfig
{
    @Config.Name("010 Alert Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.alertVolume")
    @Config.Comment(
            {
                    "How loud the alert sound is",
                    "Won't take effect until after the next time the sound stops"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public static double alertVolume = 1;

    @Config.Name("020 Heartbeat Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.heartbeatVolume")
    @Config.Comment(
            {
                    "How loud the heartbeat sound is",
                    "Won't take effect until after the next time the sound stops"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public static double heartbeatVolume = 1;
}
