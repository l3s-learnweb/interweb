package de.l3s.interweb.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectWrapper<T> {
    @JsonProperty("object")
    private String object;

    @JsonProperty("data")
    private T data;

    public ObjectWrapper() {
    }

    public ObjectWrapper(String object, T data) {
        this.object = object;
        this.data = data;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
