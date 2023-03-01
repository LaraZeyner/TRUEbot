package de.zahrie.trues.api.riot.xayah.types.core.championmastery;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;

public class ChampionMasteryScore extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteryScore> implements Comparable<ChampionMasteryScore> {
    public static class Builder {
        private final Summoner summoner;

        private Builder(final Summoner summoner) {
            this.summoner = summoner;
        }

        public ChampionMasteryScore get() {
            final ImmutableMap.Builder<String, Object> builder =
                ImmutableMap.<String, Object> builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId());

            return Orianna.getSettings().getPipeline().get(ChampionMasteryScore.class, builder.build());
        }
    }

    public static final String CHAMPION_MASTERY_SCORE_LOAD_GROUP = "champion-mastery-score";
    private static final long serialVersionUID = 5794183951997021894L;

    public static Builder forSummoner(final Summoner summoner) {
        return new Builder(summoner);
    }

    private final Supplier<Summoner> summoner = Suppliers.memoize(() -> {
        if(coreData.getSummonerId() == null) {
            return null;
        }
        return Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get();
    });

    public ChampionMasteryScore(final de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteryScore coreData) {
        super(coreData, 1);
    }

    @Override
    public int compareTo(final ChampionMasteryScore o) {
        return Integer.compare(getScore(), o.getScore());
    }

    @Override
    public boolean exists() {
        if(coreData.getScore() == 0) {
            load(CHAMPION_MASTERY_SCORE_LOAD_GROUP);
        }
        return coreData.getScore() != 0;
    }

    @Override
    protected List<String> getLoadGroups() {
        return Arrays.asList(new String[] {
            CHAMPION_MASTERY_SCORE_LOAD_GROUP
        });
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    public int getScore() {
        if(coreData.getScore() == 0) {
            load(CHAMPION_MASTERY_SCORE_LOAD_GROUP);
        }
        return coreData.getScore();
    }

    @Searchable({Summoner.class, String.class, long.class})
    public Summoner getSummoner() {
        return summoner.get();
    }

    @Override
    protected void loadCoreData(final String group) {
        ImmutableMap.Builder<String, Object> builder;
        switch(group) {
            case CHAMPION_MASTERY_SCORE_LOAD_GROUP:
                builder = ImmutableMap.builder();
                if(coreData.getSummonerId() != null) {
                    builder.put("summonerId", coreData.getSummonerId());
                }
                if(coreData.getPlatform() != null) {
                    builder.put("platform", Platform.withTag(coreData.getPlatform()));
                }
                final de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteryScore data =
                    Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMasteryScore.class, builder.build());
                if(data != null) {
                    coreData = data;
                }
                break;
            default:
                break;
        }
    }
}
