package de.zahrie.trues.api.riot.xayah.types.core.match;

import de.zahrie.trues.api.riot.xayah.types.common.Lane;
import de.zahrie.trues.api.riot.xayah.types.common.Role;
import de.zahrie.trues.api.riot.xayah.types.common.RunePath;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Item;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ReforgedRune;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public abstract class Participant extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.match.Participant> {
    private static final long serialVersionUID = 5673949825555239078L;

    public Participant(final de.zahrie.trues.api.riot.xayah.types.data.match.Participant coreData) {
        super(coreData);
    }

    @Searchable({Champion.class, String.class, int.class})
    public abstract Champion getChampion();

    public abstract Tier getHighestTierInSeason();

    @Searchable({Item.class, String.class, int.class})
    public abstract SearchableList<Item> getItems();

    public abstract Lane getLane();

    public abstract Summoner getPreTransferSummoner();

    public abstract RunePath getPrimaryRunePath();

    public abstract ProfileIcon getProfileIcon();

    public abstract Role getRole();

    @Searchable({ReforgedRune.class, String.class, int.class})
    public abstract SearchableList<RuneStats> getRuneStats();

    public abstract RunePath getSecondaryRunePath();

    public abstract ParticipantStats getStats();

    @Searchable({Summoner.class, String.class, long.class})
    public abstract Summoner getSummoner();

    @Searchable({SummonerSpell.class, String.class, long.class})
    public abstract SummonerSpell getSummonerSpellD();

    @Searchable({SummonerSpell.class, String.class, long.class})
    public abstract SummonerSpell getSummonerSpellF();

    public abstract Team getTeam();

    public abstract ParticipantTimeline getTimeline();
}
