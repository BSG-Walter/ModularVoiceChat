package fr.nathanael2611.modularvoicechat.api;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event will be called after a player recorded a voice sample,
 * just before this sample be encoded and sent to the server.
 */
public class VoiceRecordedEvent extends Event
{

    /* The recorded audio-sample */
    private short[] recordedSamples;

    /**
     * Constructor
     * @param recordedSamples the recorder audio-samples
     */
    public VoiceRecordedEvent(short[] recordedSamples)
    {
        this.recordedSamples = recordedSamples;
    }

    /**
     * This event is cancelable
     * @return true
     */
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    /**
     * Getter for recorded samples
     * @return the recorded samples
     */
    public short[] getRecordedSamples()
    {
        return recordedSamples;
    }

    /**
     * Used to set the recorded samples to a new value
     * @param recordedSamples the new recorded samples
     */
    public void setRecordedSamples(short[] recordedSamples)
    {
        this.recordedSamples = recordedSamples;
    }

}
