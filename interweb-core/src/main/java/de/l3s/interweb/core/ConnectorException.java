package de.l3s.interweb.core;

import java.io.Serial;
import java.rmi.RemoteException;

public class ConnectorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4178090074531977861L;

    public ConnectorException(String message) {
        super(message);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorException(String message, String remoteReason) {
        super(message, new RemoteException(remoteReason));
    }
}
