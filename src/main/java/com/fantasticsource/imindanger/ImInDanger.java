package com.fantasticsource.imindanger;

import com.fantasticsource.imindanger.config.DangerConfig;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.sound.SimpleSound;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_QUADS;

@Mod(modid = ImInDanger.MODID, name = ImInDanger.NAME, version = ImInDanger.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.051,)")
public class ImInDanger
{
    public static final String MODID = "imindanger";
    public static final String NAME = "I'm In Danger!";
    public static final String VERSION = "1.12.2.004";

    private static final ResourceLocation DANGER_INDICATOR_TEXTURE = new ResourceLocation(MODID, "image/danger.png");

    public static final ResourceLocation
            ALERT_SOUND_RL = new ResourceLocation(MODID, "alert"),
            HEARTBEAT_SOUND_RL = new ResourceLocation(MODID, "heartbeat");
    public static final SoundEvent
            ALERT_SOUND_EVENT = new SoundEvent(ALERT_SOUND_RL).setRegistryName(ALERT_SOUND_RL),
            HEARTBEAT_SOUND_EVENT = new SoundEvent(HEARTBEAT_SOUND_RL).setRegistryName(HEARTBEAT_SOUND_RL);


    public static ArrayList<EntityPlayerMP> inDangerPlayers = new ArrayList<>();


    public static SimpleSound alertSound = null, heartbeatSound = null;

    public static boolean clientInDanger = false;

    public static long lastDangerStartTime = 0, lastDangerEndTime = 0, lastAlarmTime = 0, lastIntensity0ToNon0Time = 0, lastIntensityNon0To0Time = 0;
    public static float lastTickIntensity = 0;


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) throws IllegalAccessException
    {
        File configFile = new File(Loader.instance().getConfigDir().getAbsolutePath() + File.separator + MODID + ".cfg");
        Configuration config = new Configuration(configFile);
        ConfigCategory category = config.getCategory("general");
        category.remove("050 Danger Smoothing");
        category.remove("050 Danger Smoothing Fade");
        category = config.getCategory("general.visuals");
        boolean changed = false;
        if (category.containsKey("017 Danger Indicator Fade-In Time"))
        {
            config.getCategory("general").get("030 Danger Fade-In Time").set(category.get("017 Danger Indicator Fade-In Time").getInt());
            category.remove("017 Danger Indicator Fade-In Time");
            changed = true;
        }
        if (category.containsKey("020 Danger Indicator Fade Time"))
        {
            config.getCategory("general").get("040 Danger Fade-Out Time").set(category.get("020 Danger Indicator Fade Time").getInt());
            category.remove("020 Danger Indicator Fade Time");
            changed = true;
        }
        category = config.getCategory("general.sound");
        if (category.containsKey("015 Minimum Time Between Alarms"))
        {
            category.get("015 Minimum Calm Before Alarm").set(category.get("015 Minimum Time Between Alarms").getInt());
            category.remove("015 Minimum Time Between Alarms");
            changed = true;
        }
        if (changed)
        {
            config.save();
            MCTools.reloadConfig(configFile.getAbsolutePath(), MODID);
        }


        MinecraftForge.EVENT_BUS.register(ImInDanger.class);
        Network.init();
        PotionAndEnchant.init();
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        //Preload sound data
        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        alertSound = new SimpleSound(ALERT_SOUND_RL, SoundCategory.HOSTILE, 0, -999999999, 0);
        heartbeatSound = new SimpleSound(HEARTBEAT_SOUND_RL, SoundCategory.HOSTILE, 0, -999999999, 0);
        soundHandler.playSound(alertSound);
        soundHandler.stopSound(alertSound);
        soundHandler.playSound(heartbeatSound);
        soundHandler.stopSound(heartbeatSound);
        alertSound = null;
        heartbeatSound = null;
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void soundRegistry(RegistryEvent.Register<SoundEvent> event)
    {
        ForgeRegistries.SOUND_EVENTS.registerAll(ALERT_SOUND_EVENT, HEARTBEAT_SOUND_EVENT);
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) return;


        ArrayList<EntityPlayerMP> inDangerPlayersNew = new ArrayList<>();

        for (Entity entity : event.world.loadedEntityList)
        {
            if (!entity.isEntityAlive()) continue;


            ResourceLocation rl = EntityList.getKey(entity);
            if (entity instanceof EntityLiving && (rl == null || !Tools.contains(DangerConfig.serverSettings.sneakyEntities, rl.toString())))
            {
                EntityLiving attacker = (EntityLiving) entity;
                boolean found = false;
                for (String string : DangerConfig.serverSettings.sneakyPotions)
                {
                    Potion sneakyPotion = Potion.REGISTRY.getObject(new ResourceLocation(string));
                    {
                        for (Potion potion : attacker.getActivePotionMap().keySet())
                        {
                            if (potion == sneakyPotion)
                            {
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                }


                EntityLivingBase target = attacker.getAttackTarget();
                if (!found && target instanceof EntityPlayerMP)
                {
                    EntityPlayerMP player = (EntityPlayerMP) target;
                    if (!inDangerPlayersNew.contains(player))
                    {
                        //Conditions for having dangersense
                        boolean hasDangersense = false;
                        switch (DangerConfig.dangersenseMode)
                        {
                            case 0:
                                hasDangersense = true;
                                break;

                            case 1:
                                hasDangersense = player.getActivePotionEffect(PotionAndEnchant.potionDangersense) != null;
                                break;

                            case 2:
                                break;

                            case 3:
                                hasDangersense = player.getActivePotionEffect(PotionAndEnchant.potionDangersense) != null;
                                break;
                        }

                        if (hasDangersense)
                        {
                            inDangerPlayersNew.add(player);
                            if (!inDangerPlayers.contains(player) && !MinecraftForge.EVENT_BUS.post(new DangerEvent((EntityPlayerMP) target, attacker)))
                            {
                                //"Alert" trigger (server)
                                Network.WRAPPER.sendTo(new Network.DangerPacket(true), player);
                            }
                        }
                    }
                }
            }
        }

        for (EntityPlayerMP player : inDangerPlayers)
        {
            if (!inDangerPlayersNew.contains(player) && !MinecraftForge.EVENT_BUS.post(new DangerEvent(player, null)))
            {
                //"Safe" trigger (server)
                Network.WRAPPER.sendTo(new Network.DangerPacket(false), player);
            }
        }

        inDangerPlayers = inDangerPlayersNew;
    }


    @SideOnly(Side.CLIENT)
    public static float currentDangerIntensity()
    {
        if (lastDangerStartTime == 0) return 0;

        if (lastDangerStartTime > lastDangerEndTime)
        {
            if (DangerConfig.dangerIndicatorFadeInTime == 0) return 1;
            return Tools.min(1, lastTickIntensity + 50f / DangerConfig.dangerIndicatorFadeInTime); // 1000 millis/s / 20ticks/s = 50 = 50millis/tick
        }

        if (DangerConfig.dangerIndicatorFadeOutTime == 0) return 0;
        return Tools.max(0, lastTickIntensity - 50f / DangerConfig.dangerIndicatorFadeOutTime); // 1000 millis/s / 20ticks/s = 50millis/tick
    }

    @SideOnly(Side.CLIENT)
    public static void setClientDanger(boolean danger)
    {
        if (clientInDanger != danger && !MinecraftForge.EVENT_BUS.post(new DangerEvent(danger)))
        {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

            if (danger)
            {
                //"Alert" trigger (client)
                lastDangerStartTime = System.currentTimeMillis();

                if (!soundHandler.isSoundPlaying(alertSound) && lastTickIntensity == 0 && System.currentTimeMillis() - lastIntensityNon0To0Time >= DangerConfig.soundSettings.minCalmBeforeAlarm)
                {
                    alertSound.volume = 1;
                    soundHandler.playSound(alertSound);
                    lastAlarmTime = System.currentTimeMillis();
                }
                if (!soundHandler.isSoundPlaying(heartbeatSound))
                {
                    heartbeatSound.volume = 0.001f;
                    soundHandler.playSound(heartbeatSound);
                }
            }
            else
            {
                //"Safe" trigger (client)
                lastDangerEndTime = System.currentTimeMillis();
            }

            clientInDanger = danger;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

        if (Minecraft.getMinecraft().world == null)
        {
            setClientDanger(false);

            lastDangerStartTime = 0;
            lastTickIntensity = 0;
            lastAlarmTime = 0;
            lastIntensity0ToNon0Time = 0;
            lastIntensityNon0To0Time = 0;

            lastTickIntensity = 0;

            soundHandler.stopSound(alertSound);
            soundHandler.stopSound(heartbeatSound);
            alertSound = null;
            heartbeatSound = null;
        }
        else
        {
            if (alertSound == null)
            {
                alertSound = new SimpleSound(ALERT_SOUND_RL, SoundCategory.HOSTILE, Minecraft.getMinecraft().player);
                heartbeatSound = new SimpleSound(HEARTBEAT_SOUND_RL, SoundCategory.HOSTILE, 0, Minecraft.getMinecraft().player);
            }


            boolean lastWas0 = lastTickIntensity == 0;
            lastTickIntensity = currentDangerIntensity();
            if (lastWas0)
            {
                if (lastTickIntensity != 0) lastIntensity0ToNon0Time = System.currentTimeMillis();
            }
            else
            {
                if (lastTickIntensity == 0) lastIntensityNon0To0Time = System.currentTimeMillis();
            }

            alertSound.volume = (float) DangerConfig.soundSettings.alertVolume;

            if (DangerConfig.soundSettings.maxHeartbeatDuration != -1 && System.currentTimeMillis() - lastIntensity0ToNon0Time > DangerConfig.soundSettings.maxHeartbeatDuration)
            {
                soundHandler.stopSound(heartbeatSound);
            }
            else if (System.currentTimeMillis() - lastIntensity0ToNon0Time >= DangerConfig.soundSettings.quietHeartbeatDelay)
            {
                heartbeatSound.volume = (float) DangerConfig.soundSettings.quietHeartbeatVolume * lastTickIntensity;
            }
            else
            {
                heartbeatSound.volume = (float) DangerConfig.soundSettings.heartbeatVolume * lastTickIntensity;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
        if (DangerConfig.visualSettings.dangerIndicatorType == 0 || lastTickIntensity == 0) return;
        float alpha = currentDangerIntensity();
        if (alpha == 0) return;


        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, alpha);


        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        switch (DangerConfig.visualSettings.dangerIndicatorType)
        {
            case 1:
                float size = (float) (16 * DangerConfig.visualSettings.dangerIndicatorScale), halfSize = size * 0.5f;
                GlStateManager.translate(halfSize + (sr.getScaledWidth() - size) * DangerConfig.visualSettings.dangerIndicatorXPosition, halfSize + (sr.getScaledHeight() - size) * DangerConfig.visualSettings.dangerIndicatorYPosition, 0);

                float uvleft = 0;
                float uvright = 1;
                float uvtop = 0;
                float uvbottom = 1;

                Minecraft.getMinecraft().renderEngine.bindTexture(DANGER_INDICATOR_TEXTURE);

                GlStateManager.glBegin(GL_QUADS);
                GlStateManager.glTexCoord2f(uvleft, uvtop);
                GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
                GlStateManager.glTexCoord2f(uvleft, uvbottom);
                GlStateManager.glVertex3f(-halfSize, halfSize, 0);
                GlStateManager.glTexCoord2f(uvright, uvbottom);
                GlStateManager.glVertex3f(halfSize, halfSize, 0);
                GlStateManager.glTexCoord2f(uvright, uvtop);
                GlStateManager.glVertex3f(halfSize, -halfSize, 0);
                GlStateManager.glEnd();

                break;
        }


        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
    }
}
