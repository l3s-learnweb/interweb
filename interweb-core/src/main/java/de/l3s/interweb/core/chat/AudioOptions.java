package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;

@RegisterForReflection
public class AudioOptions {

    /**
     * The voice the model uses to respond. Supported voices are alloy, ash, ballad, coral, echo, sage, shimmer, and verse.
     */
    @JsonProperty("voice")
    private String voice;

    /**
     * Specifies the output audio format. Must be one of wav, mp3, flac, opus, or pcm16.
     */
    @JsonProperty("format")
    private String format;

    public AudioOptions() {
    }

    public AudioOptions(String voice, String format) {
        this.voice = voice;
        this.format = format;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}

