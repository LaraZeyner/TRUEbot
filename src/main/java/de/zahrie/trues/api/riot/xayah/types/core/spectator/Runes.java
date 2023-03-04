package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.RunePath;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Runes extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Runes> {
    @Serial
    private static final long serialVersionUID = 3692909600109196352L;

    public Runes(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Runes coreData) {
        super(coreData);
    }

    public RunePath getPrimaryPath() {
        return RunePath.withId(coreData.getPrimaryPath());
    }

    public RunePath getSecondaryPath() {
        return RunePath.withId(coreData.getSecondaryPath());
    }
}
