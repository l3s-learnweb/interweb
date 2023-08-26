package de.l3s.interweb.connector.openai.client;

public class ErrorResponse {
    public Error error;

    public static class Error {
        public String message;
        public Object type;
        public String param;
        public String code;
        public Integer status;
    }
}
