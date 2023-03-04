package de.zahrie.trues.api.riot.xayah.datapipeline.riotapi.exceptions;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.OriannaException;

public class UnsupportedMediaTypeException extends OriannaException {
    @Serial
    private static final long serialVersionUID = 5431308186582990449L;

    public UnsupportedMediaTypeException(final String message) {
        super(message);
    }
}
