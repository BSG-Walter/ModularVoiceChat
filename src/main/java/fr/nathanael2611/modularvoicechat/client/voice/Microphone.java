package fr.nathanael2611.modularvoicechat.client.voice;

import fr.nathanael2611.modularvoicechat.client.MicrophoneException;

public interface Microphone {

    void open() throws MicrophoneException;

    void start();

    void stop();

    void close();

    boolean isOpen();

    boolean isStarted();

    int available();

    short[] read();

}
