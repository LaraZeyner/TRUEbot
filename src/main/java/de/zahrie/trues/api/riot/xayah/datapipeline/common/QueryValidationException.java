package de.zahrie.trues.api.riot.xayah.datapipeline.common;

import java.io.Serial;

public class QueryValidationException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 5500321793855768637L;

    public QueryValidationException(final String message) {
        super(message);
    }
}
