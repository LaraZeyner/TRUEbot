package de.zahrie.trues.api.riot.xayah.types.core.league;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.merakianalytics.datapipelines.iterators.CloseableIterator;
import com.merakianalytics.datapipelines.iterators.CloseableIterators;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public class LeaguePositions extends GhostObject.ListProxy<LeagueEntry, de.zahrie.trues.api.riot.xayah.types.data.league.LeagueEntry, de.zahrie.trues.api.riot.xayah.types.data.league.LeaguePositions> {
    public static final class Builder {
        private final Summoner summoner;

        private Builder(final Summoner summoner) {
            this.summoner = summoner;
        }

        public LeaguePositions get() {
            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId());

            return Orianna.getSettings().getPipeline().get(LeaguePositions.class, builder.build());
        }
    }

    public static final class ManyBuilder {
        private boolean streaming = false;
        private final Iterable<Summoner> summoners;

        private ManyBuilder(final Iterable<Summoner> summoners) {
            this.summoners = summoners;
        }

        public SearchableList<LeaguePositions> get() {
            final List<String> ids = new ArrayList<>();
            final Iterator<Summoner> iterator = summoners.iterator();
            Summoner summoner = iterator.next();

            if(summoner == null) {
                return SearchableLists.from(Collections.emptyList());
            }

            final Platform platform = summoner.getPlatform();
            ids.add(summoner.getId());
            while(iterator.hasNext()) {
                summoner = iterator.next();

                if(platform != summoner.getPlatform()) {
                    throw new IllegalArgumentException("All summoners must be from the same platform/region!");
                }
                ids.add(summoner.getId());
            }

            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", platform).put("summonerIds", ids);

            final CloseableIterator<LeaguePositions> result =
                Orianna.getSettings().getPipeline().getMany(LeaguePositions.class, builder.build(), streaming);
            return streaming ? SearchableLists.from(CloseableIterators.toLazyList(result)) : SearchableLists.from(CloseableIterators.toList(result));
        }

        public ManyBuilder streaming() {
            streaming = true;
            return this;
        }
    }

    @Serial
    private static final long serialVersionUID = -8683905800046401766L;

    public static Builder forSummoner(final Summoner summoner) {
        return new Builder(summoner);
    }

    public static ManyBuilder forSummoners(final Iterable<Summoner> summoners) {
        return new ManyBuilder(summoners);
    }

    public static ManyBuilder forSummoners(final Summoner... summoners) {
        return new ManyBuilder(Arrays.asList(summoners));
    }

    private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
        if(coreData.getSummonerId() == null) {
            return null;
        }
        return Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    public LeaguePositions(final de.zahrie.trues.api.riot.xayah.types.data.league.LeaguePositions coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.isEmpty()) {
            load(LIST_PROXY_LOAD_GROUP);
        }
        return !coreData.isEmpty();
    }

    @Override
    protected List<String> getLoadGroups() {
        return List.of(LIST_PROXY_LOAD_GROUP);
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    @Searchable({Summoner.class, String.class, long.class})
    public Summoner getSummoner() {
        return summoner.get();
    }

    @Override
    protected void loadCoreData(final String group) {
        final ImmutableMap.Builder<String, Object> builder;
      if (group.equals(LIST_PROXY_LOAD_GROUP)) {
        builder = ImmutableMap.builder();
        if (coreData.getPlatform() != null) {
          builder.put("platform", Platform.withTag(coreData.getPlatform()));
        }
        if (coreData.getSummonerId() != null) {
          builder.put("summonerId", coreData.getSummonerId());
        }
        final de.zahrie.trues.api.riot.xayah.types.data.league.LeaguePositions data =
            Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.league.LeaguePositions.class, builder.build());
        if (data != null) {
          coreData = data;
        }
        loadListProxyData(LeagueEntry::new);
      }
    }
}
