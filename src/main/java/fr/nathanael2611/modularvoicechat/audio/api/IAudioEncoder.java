package fr.nathanael2611.modularvoicechat.audio.api;

public interface IAudioEncoder extends NoExceptionCloseable {
	
	byte[] encode(short[] pcm);
	
	byte[] silence();

	boolean isClosed();

	int encoderId();
	
}
