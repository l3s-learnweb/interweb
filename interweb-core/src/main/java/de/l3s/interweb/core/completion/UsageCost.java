package de.l3s.interweb.core.completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsageCost {
    @JsonProperty("response")
    private double response;
    @JsonProperty("chat")
    private double chat;

    public double getResponse() {
        return response;
    }

    public void setResponse(double response) {
        this.response = response;
    }

    public double getChat() {
        return chat;
    }

    public void setChat(double chat) {
        this.chat = chat;
    }
}
