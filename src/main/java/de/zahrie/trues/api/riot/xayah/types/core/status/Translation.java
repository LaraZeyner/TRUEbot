package de.zahrie.trues.api.riot.xayah.types.core.status;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Translation extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.status.Translation> {
    @Serial
    private static final long serialVersionUID = 1239359235508212254L;

    public Translation(final de.zahrie.trues.api.riot.xayah.types.data.status.Translation coreData) {
        super(coreData);
    }

    public String getContent() {
        return coreData.getContent();
    }

    public String getHeading() {
        return coreData.getHeading();
    }

    public String getLocale() {
        return coreData.getLocale();
    }
}
