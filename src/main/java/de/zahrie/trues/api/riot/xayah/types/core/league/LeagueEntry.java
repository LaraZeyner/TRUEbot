package de.zahrie.trues.api.riot.xayah.types.core.league;

import java.io.Serial;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.zahrie.trues.api.riot.xayah.types.common.Division;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Queue;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.Tier;
import de.zahrie.trues.api.riot.xayah.types.core.OriannaObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public class LeagueEntry extends OriannaObject<de.zahrie.trues.api.riot.xayah.types.data.league.LeagueEntry> implements Comparable<LeagueEntry> {
    @Serial
    private static final long serialVersionUID = -8320702451565649681L;

    private final Supplier<League> league = Suppliers.memoize(() -> {
        if(coreData.getLeagueId() == null) {
            return null;
        }
        return League.withId(coreData.getLeagueId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    private final Supplier<Series> promos = Suppliers.memoize(() -> {
        if(coreData.getPromos() == null) {
            return null;
        }
        return new Series(coreData.getPromos());
    });

    private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
        if(coreData.getSummonerId() == null) {
            return null;
        }
        final Summoner summoner = Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
        if(summoner.getCoreData().getName() == null) {
            summoner.getCoreData().setName(coreData.getSummonerName());
        }
        return summoner;
    });

    public LeagueEntry(final de.zahrie.trues.api.riot.xayah.types.data.league.LeagueEntry coreData) {
        super(coreData);
    }

    @Override
    public int compareTo(final LeagueEntry o) {
        int result = getTier().compare(o.getTier());
        if(result != 0) {
            return result;
        }
        result = getDivision().compare(o.getDivision());
        if(result != 0) {
            return result;
        }
        return Integer.compare(getLeaguePoints(), o.getLeaguePoints());
    }

    public Division getDivision() {
        return Division.valueOf(coreData.getDivision());
    }

    public League getLeague() {
        return league.get();
    }

    public int getLeaguePoints() {
        return coreData.getLeaguePoints();
    }

    public int getLosses() {
        return coreData.getLosses();
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Series getPromos() {
        return promos.get();
    }

    public Queue getQueue() {
        return Queue.withTag(coreData.getQueue());
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    @Searchable({Summoner.class, String.class, long.class})
    public Summoner getSummoner() {
        return summoner.get();
    }

    public Tier getTier() {
        return Tier.valueOf(coreData.getTier());
    }

    public int getWins() {
        return coreData.getWins();
    }

    public boolean isFreshBlood() {
        return coreData.isFreshBlood();
    }

    public boolean isInactive() {
        return coreData.isInactive();
    }

    public boolean isOnHotStreak() {
        return coreData.isOnHotStreak();
    }

    public boolean isVeteran() {
        return coreData.isVeteran();
    }
}
