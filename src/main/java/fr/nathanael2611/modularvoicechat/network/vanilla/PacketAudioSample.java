package fr.nathanael2611.modularvoicechat.network.vanilla;

import fr.nathanael2611.modularvoicechat.client.ClientEventHandler;
import fr.nathanael2611.modularvoicechat.client.voice.VoiceClientManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.MicroManager;
import fr.nathanael2611.modularvoicechat.client.voice.audio.SpeakerManager;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.net.InetSocketAddress;

public class PacketAudioSample implements IMessage {
    private ByteBuf opusBytes;

    PacketAudioSample(ByteBuf buf){
        this.opusBytes = buf;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.opusBytes = buf;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBytes(this.opusBytes);
    }

    public static class Message implements IMessageHandler<PacketAudioSample, IMessage>
    {
        @Override
        public IMessage onMessage(PacketAudioSample message, MessageContext ctx)
        {
            if(ctx.side.isServer()) {
                Helpers.log("esclient");
            }else{
                Helpers.log("esserver");
            }
            return null;
        }
    }
}
