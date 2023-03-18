package de.zahrie.trues.api.riot.xayah.types.core.spectator;

import java.io.Serial;

import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.ProfileIcon;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.SummonerSpell;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public abstract class Participant extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.spectator.Participant> {
    @Serial
    private static final long serialVersionUID = -919093957040517072L;

    public Participant(final de.zahrie.trues.api.riot.xayah.types.data.spectator.Participant coreData) {
        super(coreData);
    }

    public abstract RiotChampion getChampion();

    public abstract ProfileIcon getProfileIcon();

    public abstract Summoner getSummoner();

    public abstract SummonerSpell getSummonerSpellD();

    public abstract SummonerSpell getSummonerSpellF();

    public abstract FeaturedMatchTeam getTeam();

    public abstract boolean isBot();
}
