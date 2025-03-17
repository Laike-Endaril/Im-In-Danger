package com.fantasticsource.imindanger;

import com.fantasticsource.mctools.sound.SimpleSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@Mod(modid = ImInDanger.MODID, name = ImInDanger.NAME, version = ImInDanger.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.050,)")
public class ImInDanger
{
    public static final String MODID = "imindanger";
    public static final String NAME = "I'm In Danger!";
    public static final String VERSION = "1.12.2.000";

    public static final ResourceLocation
            ALERT_SOUND_RL = new ResourceLocation(MODID, "alert"),
            HEARTBEAT_SOUND_RL = new ResourceLocation(MODID, "heartbeat");
    public static final SoundEvent
            ALERT_SOUND_EVENT = new SoundEvent(ALERT_SOUND_RL).setRegistryName(ALERT_SOUND_RL),
            HEARTBEAT_SOUND_EVENT = new SoundEvent(HEARTBEAT_SOUND_RL).setRegistryName(HEARTBEAT_SOUND_RL);

    public static SimpleSound alertSound = null, heartbeatSound = null;


    public static boolean clientInDanger = false;
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
            if (entity instanceof EntityLiving)
            {
                EntityLiving attacker = (EntityLiving) entity;
                EntityLivingBase target = attacker.getAttackTarget();
                if (target instanceof EntityPlayerMP)
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


    @SideOnly(Side.CLIENT)
    public static void setClientDanger(boolean danger)
    {
        if (clientInDanger != danger && !MinecraftForge.EVENT_BUS.post(new DangerEvent(danger)))
        {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

            //"Alert" and "Safe" triggers (client)
            if (danger)
            {
                if (alertSound == null)
                {
                    alertSound = new SimpleSound(ALERT_SOUND_RL, SoundCategory.HOSTILE);
                    heartbeatSound = new SimpleSound(HEARTBEAT_SOUND_RL, SoundCategory.HOSTILE, 0);
                }

                if (!soundHandler.isSoundPlaying(alertSound)) soundHandler.playSound(alertSound);
                if (!soundHandler.isSoundPlaying(heartbeatSound)) soundHandler.playSound(heartbeatSound);
            }
            else
            {
                if (soundHandler.isSoundPlaying(heartbeatSound)) soundHandler.stopSound(heartbeatSound);
            }
            //TODO show indicator

            clientInDanger = danger;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
        if (soundHandler == null || heartbeatSound == null) return;


        if (Minecraft.getMinecraft().world == null)
        {
            setClientDanger(false);
            if (soundHandler.isSoundPlaying(heartbeatSound)) soundHandler.stopSound(heartbeatSound);
        }
    }
}
