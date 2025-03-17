package com.fantasticsource.imindanger.config;

import com.fantasticsource.imindanger.ImInDanger;
import net.minecraftforge.common.config.Config;

public class VisualsConfig
{
    @Config.Name("010 Danger Indicator Type")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals.dangerIndicatorType")
    @Config.Comment(
            {
                    "What kind of danger indicator is shown when in danger",
                    "0 = None",
                    "1 = Regular Image"
            })
    @Config.RangeInt(min = 0, max = 1)
    public int dangerIndicatorType = 1;

    @Config.Name("020 Danger Indicator Fade Time")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals.dangerIndicatorFadeTime")
    @Config.Comment(
            {
                    "How long the danger indicator takes to fade away once danger has passed, in milliseconds (1000 = 1 second)"
            })
    @Config.RangeInt(min = 0)
    public int dangerIndicatorFadeTime = 0;
}
