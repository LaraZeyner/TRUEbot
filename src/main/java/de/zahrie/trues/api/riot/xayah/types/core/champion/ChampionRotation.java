package de.zahrie.trues.api.riot.xayah.types.core.champion;

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
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champion;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.Champions;

public class ChampionRotation extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.champion.ChampionRotation> {
    public static class Builder {
        private Platform platform;

        private Builder() {}

        public ChampionRotation get() {
            if(platform == null) {
                platform = Orianna.getSettings().getDefaultPlatform();
                if(platform == null) {
                    throw new IllegalStateException(
                        "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                }
            }

            final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("platform", platform);
            return Orianna.getSettings().getPipeline().get(ChampionRotation.class, builder.build());
        }

        public Builder withPlatform(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder withRegion(final Region region) {
            platform = region.getPlatform();
            return this;
        }
    }

    public static final String CHAMPION_ROTATION_LOAD_GROUP = "champion-rotation";

    private static final long serialVersionUID = 3798507940612239922L;

    public static ChampionRotation get() {
        return new Builder().get();
    }

    public static Builder withPlatform(final Platform platform) {
        return new Builder().withPlatform(platform);
    }

    public static Builder withRegion(final Region region) {
        return new Builder().withRegion(region);
    }

    private final Supplier<SearchableList<Champion>> freeChampions = Suppliers.memoize(new Supplier<SearchableList<Champion>>() {
        @Override
        public SearchableList<Champion> get() {
            load(CHAMPION_ROTATION_LOAD_GROUP);
            if(coreData.getFreeChampionIds() == null) {
                return null;
            }
            return SearchableLists
                .unmodifiableFrom(Champions.withIds(coreData.getFreeChampionIds()).withPlatform(Platform.withTag(coreData.getPlatform())).get());
        }
    });

    private final Supplier<SearchableList<Champion>> freeChampionsForNewPlayers = Suppliers.memoize(new Supplier<SearchableList<Champion>>() {
        @Override
        public SearchableList<Champion> get() {
            load(CHAMPION_ROTATION_LOAD_GROUP);
            if(coreData.getFreeChampionIdsForNewPlayers() == null) {
                return null;
            }
            return SearchableLists
                .unmodifiableFrom(Champions.withIds(coreData.getFreeChampionIdsForNewPlayers()).withPlatform(Platform.withTag(coreData.getPlatform())).get());
        }
    });

    public ChampionRotation(final de.zahrie.trues.api.riot.xayah.types.data.champion.ChampionRotation coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.getFreeChampionIds() == null) {
            load(CHAMPION_ROTATION_LOAD_GROUP);
        }
        return coreData.getFreeChampionIds() != null;
    }

    @Searchable({Champion.class, String.class, int.class})
    public SearchableList<Champion> getFreeChampions() {
        return freeChampions.get();
    }

    public SearchableList<Champion> getFreeChampionsForNewPlayers() {
        return freeChampionsForNewPlayers.get();
    }

    @Override
    protected List<String> getLoadGroups() {
        return Arrays.asList(new String[] {
            CHAMPION_ROTATION_LOAD_GROUP
        });
    }

    public int getMaxNewPlayerLevel() {
        if(coreData.getMaxNewPlayerLevel() == 0) {
            load(CHAMPION_ROTATION_LOAD_GROUP);
        }
        return coreData.getMaxNewPlayerLevel();
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    @Override
    protected void loadCoreData(final String group) {
        ImmutableMap.Builder<String, Object> builder;
        switch(group) {
            case CHAMPION_ROTATION_LOAD_GROUP:
                builder = ImmutableMap.builder();
                if(coreData.getPlatform() != null) {
                    builder.put("platform", Platform.withTag(coreData.getPlatform()));
                }

                final de.zahrie.trues.api.riot.xayah.types.data.champion.ChampionRotation data =
                    Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.champion.ChampionRotation.class, builder.build());
                if(data != null) {
                    coreData = data;
                }
                break;
            default:
                break;
        }
    }
}
