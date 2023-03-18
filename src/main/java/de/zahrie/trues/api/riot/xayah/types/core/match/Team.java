package de.zahrie.trues.api.riot.xayah.types.core.match;

import java.io.Serial;

import de.zahrie.trues.api.riot.matchhistory.champion.Champion;
import de.zahrie.trues.api.riot.xayah.types.common.Side;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Item;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public abstract class Team extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.Team> {
    @Serial
    private static final long serialVersionUID = -5161738749707752541L;

    public Team(final de.zahrie.trues.api.riot.xayah.types.data.match.Team coreData) {
        super(coreData);
    }

    @Searchable({RiotChampion.class, String.class, int.class})
    public abstract SearchableList<Champion> getBans();

    public abstract int getBaronKills();

    public abstract int getDominionScore();

    public abstract int getDragonKills();

    public abstract int getInhibitorKills();

    @Searchable({Summoner.class, RiotChampion.class, Item.class, SummonerSpell.class, String.class, long.class, int.class})
    public abstract SearchableList<MatchParticipant> getParticipants();

    public abstract SearchableList<Champion> getPickTurns();

    public abstract int getRiftHeraldKills();

    public abstract Side getSide();

    public abstract int getTowerKills();

    public abstract int getVilemawKills();

    public abstract boolean isFirstBaronKiller();

    public abstract boolean isFirstBloodKiller();

    public abstract boolean isFirstDragonKiller();

    public abstract boolean isFirstInhibitorKiller();

    public abstract boolean isFirstRiftHeraldKiller();

    public abstract boolean isFirstTowerKiller();

    public abstract boolean isWinner();
}
