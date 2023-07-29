package de.l3s.interweb.connector.ipernity.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IpernityResponse {
    @JsonProperty(required = true)
    public Docs docs;
    @JsonProperty(required = true)
    public Api api;

    public static class Api {
        public String status;
        public String message;
        public Integer code;
        public Integer at;
    }
}
