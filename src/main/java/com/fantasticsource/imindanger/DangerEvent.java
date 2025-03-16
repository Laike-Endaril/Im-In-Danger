package com.fantasticsource.imindanger;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DangerEvent extends Event
{
    public boolean danger;
    public EntityPlayerMP player = null;
    public EntityLiving attacker = null;

    public DangerEvent(boolean danger)
    {
        this.danger = danger;
    }

    public DangerEvent(EntityPlayerMP player, EntityLiving attacker)
    {
        this(true);
        this.player = player;
        this.attacker = attacker;
    }
}
