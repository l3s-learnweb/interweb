package de.l3s.interweb.core.chat;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class UsageCost {
    private double response;
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
