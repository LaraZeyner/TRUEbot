package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;
import java.util.List;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public abstract class Player extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Player> {
    @Serial
    private static final long serialVersionUID = 4241892066509529815L;

    public Player(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Player coreData) {
        super(coreData);
    }

    @Searchable({RiotChampion.class, String.class, int.class})
    public abstract RiotChampion getChampion();

    public abstract List<GameCustomizationObject> getCustomizationObjects();

    public abstract ProfileIcon getProfileIcon();

    public abstract Runes getRunes();

    @Searchable({Summoner.class, String.class, long.class})
    public abstract Summoner getSummoner();

    public abstract SummonerSpell getSummonerSpellD();

    public abstract SummonerSpell getSummonerSpellF();

    public abstract CurrentMatchTeam getTeam();

    public abstract boolean isBot();
}
