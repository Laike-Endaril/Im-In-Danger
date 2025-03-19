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
    public static int dangerSmoothing = 3000;

    @Config.Name("050 Danger Smoothing Fade")
    @Config.LangKey(ImInDanger.MODID + ".config.dangerSmoothingFade")
    @Config.Comment(
            {
                    "",
                    "If true, heartbeat and indicator fade even during danger smoothing, but if combat is re-entered during smoothing, the alarm still doesn't sound and the heartbeat timers continue from where they were instead of restarting",
                    "",
                    "If false, heartbeat and indicator continue as if still in danger until the danger smoothing timer runs out, then fade"
            })
    public static boolean dangerSmoothingFade = true;


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
