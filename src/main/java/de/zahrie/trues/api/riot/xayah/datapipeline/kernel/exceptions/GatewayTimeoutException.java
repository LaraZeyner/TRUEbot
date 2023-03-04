package de.zahrie.trues.api.riot.xayah.datapipeline.kernel.exceptions;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.datapipeline.common.TimeoutException.Type;
import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class GatewayTimeoutException extends OriannaException {
    @Serial
    private static final long serialVersionUID = 430582157720690257L;
    private final Type type;

    public GatewayTimeoutException(final String message, final Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
