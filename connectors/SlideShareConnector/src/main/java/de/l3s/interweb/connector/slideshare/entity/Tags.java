package de.l3s.interweb.connector.slideshare.entity;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

@RegisterForReflection
public class Tags {

    @JsonProperty("Tag")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
