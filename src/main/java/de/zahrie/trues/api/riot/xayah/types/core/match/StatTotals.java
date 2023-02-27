package de.zahrie.trues.api.riot.xayah.types.core.match;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class StatTotals extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.StatTotals> {
    private static final long serialVersionUID = -8528007572486657286L;

    public StatTotals(final de.zahrie.trues.api.riot.xayah.types.data.match.StatTotals coreData) {
        super(coreData);
    }

    public double getAt10() {
        return coreData.getAt10();
    }

    public double getAt20() {
        return coreData.getAt20();
    }

    public double getAt30() {
        return coreData.getAt30();
    }

    public double getAtGameEnd() {
        return coreData.getAtGameEnd();
    }
}
