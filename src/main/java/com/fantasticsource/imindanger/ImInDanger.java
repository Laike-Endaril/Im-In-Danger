package com.fantasticsource.imindanger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@Mod(modid = ImInDanger.MODID, name = ImInDanger.NAME, version = ImInDanger.VERSION)
public class ImInDanger
{
    public static final String MODID = "imindanger";
    public static final String NAME = "I'm In Danger!";
    public static final String VERSION = "1.12.2.000";

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
                        if (!inDangerPlayers.contains(player) && !MinecraftForge.EVENT_BUS.post(new DangerEvent((EntityPlayerMP) target, attacker))) Network.WRAPPER.sendTo(new Network.DangerPacket(true), player);
                    }
                }
            }
        }

        for (EntityPlayerMP player : inDangerPlayers)
        {
            if (!inDangerPlayersNew.contains(player) && !MinecraftForge.EVENT_BUS.post(new DangerEvent(player, null))) Network.WRAPPER.sendTo(new Network.DangerPacket(false), player);
        }

        inDangerPlayers = inDangerPlayersNew;
    }


    @SideOnly(Side.CLIENT)
    public static void setClientDanger(boolean danger)
    {
        if (clientInDanger != danger && !MinecraftForge.EVENT_BUS.post(new DangerEvent(danger)))
        {
            //TODO play sound
            //TODO show indicator
            System.out.println(danger);
        }
    }
}
