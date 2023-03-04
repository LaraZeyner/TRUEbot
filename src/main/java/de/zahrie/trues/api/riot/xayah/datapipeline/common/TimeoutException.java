package de.zahrie.trues.api.riot.xayah.datapipeline.common;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class TimeoutException extends OriannaException {
    public enum Type {
            HTTP,
            RATE_LIMITER
    }

    @Serial
    private static final long serialVersionUID = -1889177348125407210L;
    private final Type type;

    public TimeoutException(final String message, final Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}