package fr.nathanael2611.modularvoicechat.audio.impl;

import com.sun.jna.ptr.PointerByReference;
import de.maxhenkel.opus4j.Opus;
import fr.nathanael2611.modularvoicechat.audio.api.IAudioDecoder;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import fr.nathanael2611.modularvoicechat.util.Utils;

import javax.annotation.Nullable;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class OpusDecoder implements IAudioDecoder
{

    protected PointerByReference opusDecoder;
    protected int sampleRate;
    protected int frameSize;
    protected int maxPayloadSize;

    private OpusDecoder(int sampleRate, int frameSize, int maxPayloadSize) {
        this.sampleRate = sampleRate;
        this.frameSize = frameSize;
        this.maxPayloadSize = maxPayloadSize;
        open();
    }

    public static IAudioDecoder createDecoder() {
        return createDecoder();
    }

    private void open() {
        if (opusDecoder != null) {
            return;
        }
        IntBuffer error = IntBuffer.allocate(1);
        opusDecoder = Opus.INSTANCE.opus_decoder_create(sampleRate, 1, error);
        if (error.get() != Opus.OPUS_OK && opusDecoder == null) {
            throw new IllegalStateException("Opus decoder error " + error.get());
        }
        Helpers.log("Initializing Opus decoder with sample rate " + sampleRate + " Hz, frame size " + frameSize + " bytes and max payload size " + maxPayloadSize + " bytes");
    }

    @Override
    public short[] decoder(@Nullable byte[] data) {
        if (isClosed()) {
            throw new IllegalStateException("Decoder is closed");
        }
        int result;
        ShortBuffer decoded = ShortBuffer.allocate(4096);
        if (data == null || data.length == 0) {
            result = Opus.INSTANCE.opus_decode(opusDecoder, null, 0, decoded, frameSize, 0);
        } else {
            result = Opus.INSTANCE.opus_decode(opusDecoder, data, data.length, decoded, frameSize, 0);
        }

        if (result < 0) {
            throw new RuntimeException("Failed to decode audio data");
        }

        short[] audio = new short[result];
        decoded.get(audio);

        return audio;
    }
    public boolean isClosed() {
        return opusDecoder == null;
    }
    @Override
    public void close() {
        if (opusDecoder == null) {
            return;
        }
        Opus.INSTANCE.opus_decoder_destroy(opusDecoder);
        opusDecoder = null;
    }

    @Nullable
    public static OpusDecoder createDecoder(int sampleRate, int frameSize, int maxPayloadSize) {
        return Utils.createSafe(() -> new OpusDecoder(sampleRate, frameSize, maxPayloadSize), e -> {
            Helpers.log("Failed to load native Opus decoder: " + e.getMessage());
        });
    }
}
