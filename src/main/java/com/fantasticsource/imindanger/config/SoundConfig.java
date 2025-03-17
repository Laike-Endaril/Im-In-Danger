package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

public class SoundConfig
{
    @Config.Name("010 Alert Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.alertVolume")
    @Config.Comment(
            {
                    "How loud the alert sound is",
                    "Won't take effect until after the next time the sound stops"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double alertVolume = 1;

    @Config.Name("020 Heartbeat Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.heartbeatVolume")
    @Config.Comment(
            {
                    "How loud the heartbeat sound is",
                    "Won't take effect until after the next time the sound stops"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double heartbeatVolume = 1;
}
