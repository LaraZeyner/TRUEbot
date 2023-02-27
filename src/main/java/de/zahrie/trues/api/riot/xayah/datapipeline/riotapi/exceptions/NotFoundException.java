package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class NotFoundException extends OriannaException {
    private static final long serialVersionUID = -349203811791575505L;

    public NotFoundException(final String message) {
        super(message);
    }
}
