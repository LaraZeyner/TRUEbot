package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class GatewayTimeoutException extends OriannaException {
    @Serial
    private static final long serialVersionUID = 7314447374663868361L;

    public GatewayTimeoutException(final String message) {
        super(message);
    }
}