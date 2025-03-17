package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

public class SoundConfig
{
    @Config.Name("010 Alert Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.alertVolume")
    @Config.Comment(
            {
                    "How loud the alert sound is"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double alertVolume = 1;


    @Config.Name("020 Heartbeat Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.heartbeatVolume")
    @Config.Comment(
            {
                    "How loud the heartbeat sound is"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double heartbeatVolume = 1;

    @Config.Name("025 Quiet Heartbeat Delay")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.quietHeartbeatDelay")
    @Config.Comment(
            {
                    "This amount of time after danger is encountered, the heartbeat gets quieter; -1 means never, 1000 is 1 second"
            })
    @Config.RangeInt(min = -1)
    public int quietHeartbeatDelay = -1;

    @Config.Name("027 Quiet Heartbeat Volume")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.quietHeartbeatVolume")
    @Config.Comment(
            {
                    "How loud the heartbeat sound is when it gets quieter"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double quietHeartbeatVolume = 0.5;

    @Config.Name("030 Max Heartbeat Duration")
    @Config.LangKey(ImInDanger.MODID + ".config.sound.maxHeartbeatDuration")
    @Config.Comment(
            {
                    "Limits how long the heartbeat will play when in danger, in milliseconds; -1 means no limit, 1000 is 1 second"
            })
    @Config.RangeInt(min = -1)
    public int maxHeartbeatDuration = -1;
}
