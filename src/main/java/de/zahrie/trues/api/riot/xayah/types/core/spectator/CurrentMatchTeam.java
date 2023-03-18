package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;

public abstract class CurrentMatchTeam extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Team> {
    @Serial
    private static final long serialVersionUID = 3485303289560691869L;

    public CurrentMatchTeam(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
        super(coreData);
    }

    public abstract SearchableList<RiotChampion> getBans();

    public abstract SearchableList<Player> getParticipants();

    public abstract Side getSide();
}
