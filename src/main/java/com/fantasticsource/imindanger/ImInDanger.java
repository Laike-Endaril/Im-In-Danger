package com.fantasticsource.imindanger;

import com.fantasticsource.imindanger.config.DangerConfig;
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
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_QUADS;

@Mod(modid = ImInDanger.MODID, name = ImInDanger.NAME, version = ImInDanger.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.051,)")
public class ImInDanger
{
    public static final String MODID = "imindanger";
    public static final String NAME = "I'm In Danger!";
    public static final String VERSION = "1.12.2.001";

    private static final ResourceLocation DANGER_INDICATOR_TEXTURE = new ResourceLocation(MODID, "image/danger.png");

    public static final ResourceLocation
            ALERT_SOUND_RL = new ResourceLocation(MODID, "alert"),
            HEARTBEAT_SOUND_RL = new ResourceLocation(MODID, "heartbeat");
    public static final SoundEvent
            ALERT_SOUND_EVENT = new SoundEvent(ALERT_SOUND_RL).setRegistryName(ALERT_SOUND_RL),
            HEARTBEAT_SOUND_EVENT = new SoundEvent(HEARTBEAT_SOUND_RL).setRegistryName(HEARTBEAT_SOUND_RL);

    public static SimpleSound alertSound = null, heartbeatSound = null;


    public static boolean clientInDanger = false;
    public static long dangerSmoothingStartTime = 0;
    public static ArrayList<EntityPlayerMP> inDangerPlayers = new ArrayList<>();

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(ImInDanger.class);
        Network.init();
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


    public static long lastFadeTrigger = 0;
    public static float lastAlpha = 0, lastFadeTriggerAlpha = 0;

    @SideOnly(Side.CLIENT)
    public static void setClientDanger(boolean danger)
    {
        if (clientInDanger != danger && !MinecraftForge.EVENT_BUS.post(new DangerEvent(danger)))
        {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

            if (danger)
            {
                //"Alert" trigger (client)
                if (!soundHandler.isSoundPlaying(alertSound)) soundHandler.playSound(alertSound);
                if (!soundHandler.isSoundPlaying(heartbeatSound)) soundHandler.playSound(heartbeatSound);
            }
            else
            {
                //"Safe" trigger (client)
                soundHandler.stopSound(heartbeatSound);
                dangerSmoothingStartTime = 0;
            }

            clientInDanger = danger;
            lastFadeTrigger = System.currentTimeMillis();
            lastFadeTriggerAlpha = lastAlpha;
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
            soundHandler.stopSound(heartbeatSound);
            lastFadeTrigger = 0;

            alertSound = null;
            heartbeatSound = null;
        }
        else
        {
            if (alertSound == null)
            {
                alertSound = new SimpleSound(ALERT_SOUND_RL, SoundCategory.HOSTILE, 0, -999999999, 0);
                heartbeatSound = new SimpleSound(HEARTBEAT_SOUND_RL, SoundCategory.HOSTILE, 0, -999999999, 0);

                //Preload sound data
                soundHandler.playSound(alertSound);
                soundHandler.stopSound(alertSound);
                soundHandler.playSound(heartbeatSound);
                soundHandler.stopSound(heartbeatSound);

                alertSound = new SimpleSound(ALERT_SOUND_RL, SoundCategory.HOSTILE, Minecraft.getMinecraft().player);
                heartbeatSound = new SimpleSound(HEARTBEAT_SOUND_RL, SoundCategory.HOSTILE, 0, Minecraft.getMinecraft().player);
            }

            alertSound.volume = (float) DangerConfig.soundSettings.alertVolume;

            if (dangerSmoothingStartTime != 0 && System.currentTimeMillis() - dangerSmoothingStartTime >= DangerConfig.dangerSmoothing)
            {
                setClientDanger(false);
            }
            else if (DangerConfig.soundSettings.maxHeartbeatDuration != -1 && System.currentTimeMillis() - lastFadeTrigger > DangerConfig.soundSettings.maxHeartbeatDuration)
            {
                soundHandler.stopSound(heartbeatSound);
            }
            else if (System.currentTimeMillis() - lastFadeTrigger >= DangerConfig.soundSettings.quietHeartbeatDelay)
            {
                heartbeatSound.volume = (float) DangerConfig.soundSettings.quietHeartbeatVolume;
                if (heartbeatSound.volume == 0) soundHandler.stopSound(heartbeatSound);
            }
            else
            {
                heartbeatSound.volume = (float) DangerConfig.soundSettings.heartbeatVolume;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
        if (DangerConfig.visualSettings.dangerIndicatorType == 0) return;


        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        long time = System.currentTimeMillis();
        switch (DangerConfig.visualSettings.dangerIndicatorType)
        {
            case 1:
                if (lastFadeTrigger == 0) break;


                float alpha;
                if (clientInDanger) alpha = lastFadeTriggerAlpha + (float) (time - lastFadeTrigger) / DangerConfig.visualSettings.dangerIndicatorFadeInTime;
                else alpha = lastFadeTriggerAlpha - (float) (time - lastFadeTrigger) / DangerConfig.visualSettings.dangerIndicatorFadeTime;

                alpha = Tools.min(Tools.max(alpha, 0), 1);
                lastAlpha = alpha;
                if (alpha == 0) break;


                GlStateManager.color(1, 1, 1, alpha);

                GlStateManager.translate(8 + (sr.getScaledWidth() - 16) * DangerConfig.visualSettings.dangerIndicatorXPosition, 8 + (sr.getScaledHeight() - 16) * DangerConfig.visualSettings.dangerIndicatorYPosition, 0);

                float uvleft = 0;
                float uvright = 1;
                float uvtop = 0;
                float uvbottom = 1;

                Minecraft.getMinecraft().renderEngine.bindTexture(DANGER_INDICATOR_TEXTURE);

                GlStateManager.glBegin(GL_QUADS);
                GlStateManager.glTexCoord2f(uvleft, uvtop);
                GlStateManager.glVertex3f(-8, -8, 0);
                GlStateManager.glTexCoord2f(uvleft, uvbottom);
                GlStateManager.glVertex3f(-8, 8, 0);
                GlStateManager.glTexCoord2f(uvright, uvbottom);
                GlStateManager.glVertex3f(8, 8, 0);
                GlStateManager.glTexCoord2f(uvright, uvtop);
                GlStateManager.glVertex3f(8, -8, 0);
                GlStateManager.glEnd();

                GlStateManager.color(1, 1, 1, 1);
                break;
        }

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
    }
}
