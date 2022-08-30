package fr.nathanael2611.modularvoicechat.audio.micro;

import com.sun.jna.Pointer;
import de.maxhenkel.rnnoise4j.RNNoise;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import fr.nathanael2611.modularvoicechat.util.Utils;

import javax.annotation.Nullable;

public class Denoiser {

    private static final int FRAME_SIZE = 480;

    private final Pointer state;
    private boolean closed;

    private Denoiser() {
        state = RNNoise.INSTANCE.rnnoise_create(null);
    }

    public short[] denoise(short[] audio) {
        if (closed) {
            throw new IllegalStateException("Tried to denoise with a closed denoiser");
        }
        float[] data = Utils.shortsToFloats(audio);
        if (data.length % FRAME_SIZE != 0) {
            throw new IllegalArgumentException("Denoising data frame size is not divisible by 480");
        }
        float[] chunk = new float[FRAME_SIZE];
        float[] denoisedChunk = new float[FRAME_SIZE];
        float[] denoised = new float[data.length];
        for (int i = 0; i < data.length / FRAME_SIZE; i++) {
            System.arraycopy(data, FRAME_SIZE * i, chunk, 0, FRAME_SIZE);
            RNNoise.INSTANCE.rnnoise_process_frame(state, denoisedChunk, chunk);
            System.arraycopy(denoisedChunk, 0, denoised, FRAME_SIZE * i, FRAME_SIZE);
        }
        return Utils.floatsToShorts(denoised);
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        RNNoise.INSTANCE.rnnoise_destroy(state);
    }
    @Nullable
    public static Denoiser createDenoiser() {
        return Utils.createSafe(Denoiser::new, e -> Helpers.log("Failed to load RNNoise: " + e.getMessage()));
    }

}