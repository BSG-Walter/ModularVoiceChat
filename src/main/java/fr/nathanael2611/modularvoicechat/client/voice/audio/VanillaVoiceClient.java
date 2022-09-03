package fr.nathanael2611.modularvoicechat.client.voice.audio;

import com.esotericsoftware.kryonet.Client;
import fr.nathanael2611.modularvoicechat.ModularVoiceChat;
import fr.nathanael2611.modularvoicechat.client.voice.KryoNetClientListener;
import fr.nathanael2611.modularvoicechat.client.voice.VoiceClient;
import fr.nathanael2611.modularvoicechat.network.objects.KryoObjects;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VanillaVoiceClient extends VoiceClient {

    private final ScheduledExecutorService RECONNECT_SERVICE = Executors.newSingleThreadScheduledExecutor();

    /**
     * Constructor
     *
     * @param playerName the player name
     */
    protected VanillaVoiceClient(String playerName) {
        super(playerName, "127.0.0.1", 0);
        RECONNECT_SERVICE.scheduleAtFixedRate(() -> {
            if(!this.client.isConnected() && host != null)
            {
                try
                {
                    Helpers.log(String.format("Try to connect to the UDP server! [%s:%s]", host, this.port));
                    client.connect(5000, host, port, port);
                    this.authenticate(playerName);
                } catch (IOException e)
                {
                    e.printStackTrace();
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§4[" + ModularVoiceChat.MOD_NAME + "] §c" + I18n.format("mvc.error.cantconnect")));
                    Helpers.log("Failed to connect to VoiceServer.");
                }
            }
            else if(host == null)
            {
                Helpers.log("Host is null!");
            }
            else if(!isHandshakeDone())
            {
                {
                    this.authenticate(playerName);
                }
            }
        }, 5, 15, TimeUnit.SECONDS);


    }
}
