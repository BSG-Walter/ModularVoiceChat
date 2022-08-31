package fr.nathanael2611.modularvoicechat.network.vanilla;

import fr.nathanael2611.modularvoicechat.util.Helpers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAudioSampleServer implements IMessage {
    private byte[] opusBytes = new byte[3840];

    public PacketAudioSampleServer(){

    }
    public PacketAudioSampleServer(byte[] buf){

        Helpers.log("creating server");
        this.opusBytes = buf;
        Helpers.log("endCreating server");
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        Helpers.log("frombytes");
        Helpers.log(String.valueOf(buf.maxCapacity()));
        Helpers.log(String.valueOf(buf.readableBytes()));
        Helpers.log(String.valueOf(buf));
        buf.readBytes(this.opusBytes);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Helpers.log("tobytes");
        buf.writeBytes(this.opusBytes);
    }
    public static class Message implements IMessageHandler<PacketAudioSampleServer, IMessage>
    {
        //@SideOnly(Side.SERVER)
        @Override
        public IMessage onMessage(PacketAudioSampleServer message, MessageContext ctx)
        {
            Helpers.log("esserver");
            Helpers.log(String.valueOf(message.opusBytes));
            return null;
        }
    }
}
