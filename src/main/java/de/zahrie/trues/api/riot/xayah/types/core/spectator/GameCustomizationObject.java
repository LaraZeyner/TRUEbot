package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class GameCustomizationObject extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.GameCustomizationObject> {
    @Serial
    private static final long serialVersionUID = -5738452634787853940L;

    public GameCustomizationObject(final de.zahrie.trues.api.riot.xayah.types.data.spectator.GameCustomizationObject coreData) {
        super(coreData);
    }

    public String getCategory() {
        return coreData.getCategory();
    }

    public String getContent() {
        return coreData.getContent();
    }
}
