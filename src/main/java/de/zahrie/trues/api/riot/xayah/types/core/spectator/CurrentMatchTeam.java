package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;

public abstract class CurrentMatchTeam extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Team> {
    private static final long serialVersionUID = 3485303289560691869L;

    public CurrentMatchTeam(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
        super(coreData);
    }

    public abstract SearchableList<Champion> getBans();

    public abstract SearchableList<Player> getParticipants();

    public abstract Side getSide();
}
