package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class UnauthorizedException extends OriannaException {
    private static final long serialVersionUID = -7024907949361933670L;

    public UnauthorizedException(final String message) {
        super(message);
    }
}
