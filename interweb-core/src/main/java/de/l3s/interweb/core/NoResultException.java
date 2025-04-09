package de.l3s.interweb.core;

import java.io.Serial;

public class NoResultException extends ConnectorException {

    @Serial
    private static final long serialVersionUID = -2445493340688986949L;

    public NoResultException(String message) {
        super(message);
    }
}
