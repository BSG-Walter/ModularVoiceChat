package fr.nathanael2611.modularvoicechat.audio.micro;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import de.maxhenkel.rnnoise4j.RNNoise;
import fr.nathanael2611.modularvoicechat.api.VoiceRecordedEvent;
import fr.nathanael2611.modularvoicechat.audio.api.NoExceptionCloseable;
import fr.nathanael2611.modularvoicechat.audio.api.IAudioEncoder;
import fr.nathanael2611.modularvoicechat.audio.impl.OpusEncoder;
import fr.nathanael2611.modularvoicechat.config.ClientConfig;
import fr.nathanael2611.modularvoicechat.proxy.ClientProxy;
import fr.nathanael2611.modularvoicechat.util.*;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 *
 * Based on: https://github.com/MC-U-Team/Voice-Chat/blob/1.15.2/audio-client/src/main/java/info/u_team/voice_chat/audio_client/micro/MicroRecorder.java
 */
public class MicroRecorder implements NoExceptionCloseable
{

    private final ExecutorService executor = Executors.newSingleThreadExecutor(ThreadUtil.createDaemonFactory("micro recorder"));

    private final MicroData microData;
    private final Consumer<byte[]> opusPacketConsumer;
    private final IAudioEncoder encoder;

    private volatile boolean send;

    private Denoiser denoiser;

    public MicroRecorder(MicroData microData, Consumer<byte[]> opusPacketConsumer, int bitrate)
    {
        this.microData = microData;
        this.opusPacketConsumer = opusPacketConsumer;

        this.encoder = new OpusEncoder(48000, 2, 20, bitrate, 0, 1000);
        this.denoiser = Denoiser.createDenoiser();
    }

    public void start()
    {
        if (send || !microData.isAvailable())
        {
            return;
        }
        send = true;

        if (denoiser != null && denoiser.isClosed()) {
            denoiser = Denoiser.createDenoiser();
        }
        executor.execute(() ->
        {
            final byte[] buffer = new byte[960 * 2 * 2];
            while (send && microData.isAvailable())
            {
                byte[] samples = microData.read(buffer);
                {
                    samples = denoiseIfEnabled(samples);
                    VoiceRecordedEvent event = new VoiceRecordedEvent(samples);
                    MinecraftForge.EVENT_BUS.post(event);
                    byte[] recordedSamples = event.getRecordedSamples();
                    if(!event.isCanceled())
                    {
                        opusPacketConsumer.accept(encoder.encode(recordedSamples));
                    }
                }
            }
            ThreadUtil.execute(10, 20, () -> opusPacketConsumer.accept(encoder.silence()));
        });
    }
    public byte[] denoiseIfEnabled(byte[] audio) {
        if (denoiser != null && ClientProxy.getConfig().isSuppressed()) {
            return denoiser.denoise(audio);
        }
        return audio;
    }
    public void stop()
    {
        send = false;
        microData.flush();
    }

    public boolean isSending()
    {
        return send;
    }

    @Override
    public void close()
    {
        if (denoiser != null) {
            denoiser.close();
        }
        executor.shutdown();
        encoder.close();
    }
}
