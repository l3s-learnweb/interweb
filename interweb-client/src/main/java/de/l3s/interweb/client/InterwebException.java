package de.l3s.interweb.client;

import java.io.Serial;

public class InterwebException extends Exception {
    @Serial
    private static final long serialVersionUID = 1648272342540671760L;

    public InterwebException(String message) {
        super(message);
    }

    public InterwebException(String message, Throwable cause) {
        super(message, cause);
    }
}
