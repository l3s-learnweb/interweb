package de.l3s.interweb.core;

import java.io.Serial;

public class ConnectorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4178090074531977861L;

    public ConnectorException(String message) {
        super(message);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    @Deprecated
    public ConnectorException(Throwable cause) {
        super(cause);
    }
}
