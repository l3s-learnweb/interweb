package de.l3s.interweb.connector.openai;

public class ErrorResponse {
    Error error;

    public static class Error {
        String message;
        Object type;
        String param;
        String code;
        Integer status;
    }
}
