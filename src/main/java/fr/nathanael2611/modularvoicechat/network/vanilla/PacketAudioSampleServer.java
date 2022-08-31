package fr.nathanael2611.modularvoicechat.network.vanilla;

import fr.nathanael2611.modularvoicechat.util.Helpers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAudioSampleServer implements IMessage {
    private ByteBuf opusBytes;

    public PacketAudioSampleServer(){

    }
    public PacketAudioSampleServer(ByteBuf buf){

        Helpers.log("creating");
        this.opusBytes = buf;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Helpers.log("frombytes");
        this.opusBytes.clear();
        this.opusBytes.writeBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        Helpers.log("tobytes");
        buf.clear();
        buf.writeBytes(this.opusBytes);
    }

    public static class Message implements IMessageHandler<PacketAudioSampleServer, IMessage>
    {
        @SideOnly(Side.SERVER)
        @Override
        public IMessage onMessage(PacketAudioSampleServer message, MessageContext ctx)
        {
            Helpers.log("esserver");
            Helpers.log(String.valueOf(message.opusBytes));
            return null;
        }
    }
}
