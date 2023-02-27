package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class ServiceUnavailableException extends OriannaException {
    private static final long serialVersionUID = 5431308186582990449L;

    public ServiceUnavailableException(final String message) {
        super(message);
    }
}
