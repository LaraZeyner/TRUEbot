package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class ForbiddenException extends OriannaException {
    @Serial
    private static final long serialVersionUID = 5431308186582990449L;

    public ForbiddenException(final String message) {
        super(message);
    }
}
