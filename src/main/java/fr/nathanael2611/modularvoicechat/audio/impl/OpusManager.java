package fr.nathanael2611.modularvoicechat.audio.impl;

import de.maxhenkel.opus4j.Opus;
import fr.nathanael2611.modularvoicechat.config.GameConfig;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import fr.nathanael2611.modularvoicechat.util.Utils;
import net.minecraft.client.audio.SoundManager;
import org.concentus.OpusApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.nathanael2611.modularvoicechat.audio.impl.OpusEncoderMode.*;

public class OpusManager {

    private static Boolean nativeOpusCompatible;
    public static final int SAMPLE_RATE = 48000;
    public static final int FRAME_SIZE = (SAMPLE_RATE / 1000) * 20;

    public static boolean isNativeOpusCompatible() {
        if (nativeOpusCompatible == null) {
            Boolean isCompatible = Utils.createSafe(OpusManager::isOpusCompatible, e -> {
                Helpers.log("Failed to load native Opus codec: " + e.getMessage());
            });
            if (isCompatible == null) {
                Helpers.log("Failed to load native Opus codec - Falling back to Java Opus implementation");
            }
            nativeOpusCompatible = isCompatible != null && isCompatible;
        }
        return nativeOpusCompatible;
    }
    public static OpusEncoder createEncoder(GameConfig.Codec mode) {
        int application = GameConfig.Codec.VOIP.getOpusValue();
        if (mode != null) {
            switch (mode) {
                case VOIP:
                    application = GameConfig.Codec.VOIP.getOpusValue();
                    break;
                case AUDIO:
                    application = GameConfig.Codec.AUDIO.getOpusValue();
                    break;
                case RESTRICTED_LOWDELAY:
                    application = GameConfig.Codec.RESTRICTED_LOWDELAY.getOpusValue();
                    break;
            };
        }
        return createEncoder(SAMPLE_RATE, FRAME_SIZE, 1024, application);
    }
    public static OpusEncoder createEncoder(int sampleRate, int frameSize, int maxPayloadSize, int application) {
        return OpusEncoder.createEncoder(sampleRate, frameSize, maxPayloadSize, application);
    }

    public static OpusDecoder createDecoder() {
        return createDecoder(SAMPLE_RATE, FRAME_SIZE, 1024);
    }
    public static OpusDecoder createDecoder(int sampleRate, int frameSize, int maxPayloadSize) {
        return OpusDecoder.createDecoder(sampleRate, frameSize, maxPayloadSize);
    }

    public static Pattern VERSIONING_PATTERN = Pattern.compile("^[^\\d\\.]* ?(?<major>\\d+)(?:\\.(?<minor>\\d+)(?:\\.(?<patch>\\d+)){0,1}){0,1}.*$");

    private static boolean isOpusCompatible() {
        String versionString = Opus.INSTANCE.opus_get_version_string();

        Matcher matcher = VERSIONING_PATTERN.matcher(versionString);
        if (!matcher.matches()) {
            Helpers.log("Failed to parse Opus version " + versionString);
            return false;
        }
        String majorGroup = matcher.group("major");
        String minorGroup = matcher.group("minor");
        String patchGroup = matcher.group("patch");
        int actualMajor = majorGroup == null ? 0 : Integer.parseInt(majorGroup);
        int actualMinor = minorGroup == null ? 0 : Integer.parseInt(minorGroup);
        int actualPatch = patchGroup == null ? 0 : Integer.parseInt(patchGroup);

        if (!isMinimum(actualMajor, actualMinor, actualPatch, 1, 1, 0)) {
            Helpers.log("Outdated Opus version detected: " + versionString);
            return false;
        }

        Helpers.log("Using Opus version " + versionString);
        return true;
    }

    private static boolean isMinimum(int actualMajor, int actualMinor, int actualPatch, int major, int minor, int patch) {
        if (major > actualMajor) {
            return false;
        } else if (major == actualMajor) {
            if (minor > actualMinor) {
                return false;
            } else if (minor == actualMinor) {
                return patch <= actualPatch;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static GameConfig.Codec getEncoderMode(){

        return GameConfig.Codec.VOIP;
    }
}
