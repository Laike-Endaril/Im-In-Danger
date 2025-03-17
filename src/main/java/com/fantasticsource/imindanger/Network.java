package com.fantasticsource.imindanger;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.fantasticsource.imindanger.ImInDanger.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(RemovedPartyMemberPacketHandler.class, DangerPacket.class, discriminator++, Side.CLIENT);
    }


    public static class DangerPacket implements IMessage
    {
        boolean danger;

        public DangerPacket()
        {
            //Required
        }

        public DangerPacket(boolean danger)
        {
            this.danger = danger;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(danger);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            danger = buf.readBoolean();
        }
    }

    public static class RemovedPartyMemberPacketHandler implements IMessageHandler<DangerPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(DangerPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                if (packet.danger)
                {
                    ImInDanger.dangerSmoothingStartTime = 0;
                    ImInDanger.setClientDanger(packet.danger);
                }
                else ImInDanger.dangerSmoothingStartTime = System.currentTimeMillis();
            });
            return null;
        }
    }
}
