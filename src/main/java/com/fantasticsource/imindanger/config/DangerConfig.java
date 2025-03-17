package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

@Config(modid = ImInDanger.MODID)
public class DangerConfig
{
    @Config.Name("050 Danger Smoothing")
    @Config.LangKey(ImInDanger.MODID + ".config.dangerSmoothing")
    @Config.Comment(
            {
                    "How long you need to be out of combat before indicators fade and sounds stop, in milliseconds (1000 = 1 second)",
                    "This can help prevent repeated alarm sounds and restarting heartbeats"
            })
    @Config.RangeInt(min = 0)
    public static int dangerSmoothing = 5000;

    @Config.Name("Sound")
    @Config.LangKey(ImInDanger.MODID + ".config.sound")
    public static SoundConfig soundSettings = new SoundConfig();

    @Config.Name("Visuals")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals")
    public static VisualsConfig visualSettings = new VisualsConfig();
}
