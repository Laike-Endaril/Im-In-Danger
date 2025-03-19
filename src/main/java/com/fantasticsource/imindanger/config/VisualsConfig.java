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

    @Config.Name("030 Danger Indicator X Position")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals.dangerIndicatorXPosition")
    @Config.Comment(
            {
                    "The horizontal location of the danger indicator (0 = left, 0.5 = center, 1 = right)"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double dangerIndicatorXPosition = 0.5;

    @Config.Name("040 Danger Indicator Y Position")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals.dangerIndicatorYPosition")
    @Config.Comment(
            {
                    "The vertical location of the danger indicator (0 = top, 0.5 = center, 1 = bottom)"
            })
    @Config.RangeDouble(min = 0, max = 1)
    public double dangerIndicatorYPosition = 0.8;

    @Config.Name("050 Danger Indicator Scale")
    @Config.LangKey(ImInDanger.MODID + ".config.visuals.dangerIndicatorScale")
    @Config.Comment(
            {
                    "The damage indicator size is multiplied by this amount",
                    "Note that render size has nothing to do with original image file dimensions (but image quality does)"
            })
    @Config.RangeDouble(min = 0)
    public double dangerIndicatorScale = 1;
}
