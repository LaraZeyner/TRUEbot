package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;

public abstract class FeaturedMatchTeam extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Team> {
    @Serial
    private static final long serialVersionUID = 5123436228430917207L;

    public FeaturedMatchTeam(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Team coreData) {
        super(coreData);
    }

    public abstract SearchableList<Champion> getBans();

    public abstract SearchableList<Participant> getParticipants();

    public abstract Side getSide();
}
