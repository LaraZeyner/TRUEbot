package de.zahrie.trues.api.riot.xayah.types.core.match;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;

public class Position extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.Position> {
    private static final long serialVersionUID = -5247967831201155642L;

    public Position(final de.zahrie.trues.api.riot.xayah.types.data.match.Position coreData) {
        super(coreData);
    }

    public int getX() {
        return coreData.getX();
    }

    public int getY() {
        return coreData.getY();
    }
}
