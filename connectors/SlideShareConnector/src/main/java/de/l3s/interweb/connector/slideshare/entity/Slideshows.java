package de.l3s.interweb.connector.slideshare.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@RegisterForReflection
public class Slideshows {

    @JsonProperty("Meta")
    private Meta meta;
    @JsonProperty("Slideshow")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Slideshow> searchResults;

    @JsonProperty("Message")
    private Message errorMessage;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Slideshow> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Slideshow> searchResults) {
        this.searchResults = searchResults;
    }

    public Message getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Message message) {
        this.errorMessage = message;
    }
}
