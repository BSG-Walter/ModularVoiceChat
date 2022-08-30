package fr.nathanael2611.modularvoicechat.audio.api;

import javax.annotation.Nullable;

public interface IAudioDecoder extends NoExceptionCloseable {

	short[] decoder(@Nullable byte[] data);
	
}
