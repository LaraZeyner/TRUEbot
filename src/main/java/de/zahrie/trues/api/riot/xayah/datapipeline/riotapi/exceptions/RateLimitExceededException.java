package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class RateLimitExceededException extends OriannaException {
    private static final long serialVersionUID = 5431308186582990449L;

    public RateLimitExceededException(final String message) {
        super(message);
    }
}
