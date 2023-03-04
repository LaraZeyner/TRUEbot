package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;

public class Champion extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Champion> {
    public static final class Builder {
        private Integer id;
        private Set<String> includedData;
        private String name, key, version, locale;
        private Platform platform;

        private Builder(final int id) {
            this.id = id;
        }

        private Builder(final String name, final boolean isName) {
            if(isName) {
                this.name = name;
            } else {
                key = name;
            }
        }

        public Champion get() {
            if(name == null && id == null && key == null) {
                throw new IllegalStateException("Must set an ID, name, or key for the Champion!");
            }

            if(platform == null) {
                platform = Orianna.getSettings().getDefaultPlatform();
                if(platform == null) {
                    throw new IllegalStateException(
                        "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
                }
            }

            if(version == null) {
                version = Orianna.getSettings().getCurrentVersion(platform);
            }

            if(locale == null) {
                locale = Orianna.getSettings().getDefaultLocale();
                locale = locale == null ? platform.getDefaultLocale() : locale;
            }

            if(includedData == null) {
                includedData = ImmutableSet.of("all");
            }

            final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object> builder().put("platform", platform).put("version", version)
                .put("locale", locale).put("includedData", includedData);

            if(id != null) {
                builder.put("id", id);
            } else if(key != null) {
                builder.put("key", key);
            } else {
                builder.put("name", name);
            }

            return Orianna.getSettings().getPipeline().get(Champion.class, builder.build());
        }

        public Builder withIncludedData(final Iterable<String> includedData) {
            this.includedData = Sets.newHashSet(includedData);
            return this;
        }

        public Builder withIncludedData(final String... includedData) {
            this.includedData = Sets.newHashSet(includedData);
            return this;
        }

        public Builder withLocale(final String locale) {
            this.locale = locale;
            return this;
        }

        public Builder withPlatform(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public Builder withRegion(final Region region) {
            platform = region.getPlatform();
            return this;
        }

        public Builder withVersion(final String version) {
            this.version = version;
            return this;
        }
    }

    public static final String CHAMPION_LOAD_GROUP = "champion";
    @Serial
    private static final long serialVersionUID = -2685748491353023270L;

    public static Builder named(final String name) {
        return new Builder(name, true);
    }

    public static Builder withId(final int id) {
        return new Builder(id);
    }

    public static Builder withKey(final String key) {
        return new Builder(key, false);
    }

    private final Supplier<List<String>> allyTips = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getAllyTips() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getAllyTips());
    });

    private final Supplier<List<String>> enemyTips = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getEnemyTips() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getEnemyTips());
    });

    private final Supplier<Image> image = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getImage() == null) {
            return null;
        }
        return new Image(coreData.getImage());
    });

    private final Supplier<Set<String>> includedData = Suppliers.memoize(() -> {
        if(coreData.getIncludedData() == null) {
            return null;
        }
        return Collections.unmodifiableSet(coreData.getIncludedData());
    });

    private final Supplier<Passive> passive = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getPassive() == null) {
            return null;
        }
        return new Passive(coreData.getPassive());
    });

    private final Supplier<SearchableList<RecommendedItems>> recommendedItems = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getRecommendedItems() == null) {
            return null;
        }
        final List<RecommendedItems> recommendedItems = new ArrayList<>(coreData.getRecommendedItems().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.RecommendedItems items : coreData.getRecommendedItems()) {
            recommendedItems.add(new RecommendedItems(items));
        }
        return SearchableLists.unmodifiableFrom(recommendedItems);
    });

    private final Supplier<SearchableList<Skin>> skins = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getSkins() == null) {
            return null;
        }
        final List<Skin> skins = new ArrayList<>(coreData.getSkins().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Skin skin : coreData.getSkins()) {
            skins.add(new Skin(skin));
        }
        return SearchableLists.unmodifiableFrom(skins);
    });

    private final Supplier<SearchableList<ChampionSpell>> spells = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getSpells() == null) {
            return null;
        }
        final List<ChampionSpell> spells = new ArrayList<>(coreData.getSpells().size());
        for(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.ChampionSpell spell : coreData.getSpells()) {
            spells.add(new ChampionSpell(spell));
        }
        return SearchableLists.unmodifiableFrom(spells);
    });

    private final Supplier<ChampionStats> stats = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getStats() == null) {
            return null;
        }
        return new ChampionStats(coreData.getStats());
    });

    private final Supplier<List<String>> tags = Suppliers.memoize(() -> {
        load(CHAMPION_LOAD_GROUP);
        if(coreData.getTags() == null) {
            return null;
        }
        return Collections.unmodifiableList(coreData.getTags());
    });

    public Champion(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Champion coreData) {
        super(coreData, 1);
    }

    @Override
    public boolean exists() {
        if(coreData.getTitle() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getTitle() != null;
    }

    public List<String> getAllyTips() {
        return allyTips.get();
    }

    public String getBlurb() {
        if(coreData.getBlurb() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getBlurb();
    }

    public int getDefenseRating() {
        if(coreData.getDefenseRating() == 0) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getDefenseRating();
    }

    public int getDifficultyRating() {
        if(coreData.getDifficultyRating() == 0) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getDifficultyRating();
    }

    public List<String> getEnemyTips() {
        return enemyTips.get();
    }

    @Searchable(int.class)
    public int getId() {
        if(coreData.getId() == 0) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getId();
    }

    public Image getImage() {
        return image.get();
    }

    public Set<String> getIncludedData() {
        return includedData.get();
    }

    @Searchable(String.class)
    public String getKey() {
        if(coreData.getKey() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getKey();
    }

    @Override
    protected List<String> getLoadGroups() {
        return List.of(CHAMPION_LOAD_GROUP);
    }

    public String getLocale() {
        return coreData.getLocale();
    }

    public String getLore() {
        if(coreData.getLore() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getLore();
    }

    public int getMagicRating() {
        if(coreData.getMagicRating() == 0) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getMagicRating();
    }

    @Searchable(String.class)
    public String getName() {
        if(coreData.getName() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getName();
    }

    public Passive getPassive() {
        return passive.get();
    }

    public int getPhysicalRating() {
        if(coreData.getPhysicalRating() == 0) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getPhysicalRating();
    }

    public Platform getPlatform() {
        return Platform.withTag(coreData.getPlatform());
    }

    public SearchableList<RecommendedItems> getRecommendedItems() {
        return recommendedItems.get();
    }

    public Region getRegion() {
        return Platform.withTag(coreData.getPlatform()).getRegion();
    }

    public String getResource() {
        if(coreData.getResource() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getResource();
    }

    public SearchableList<Skin> getSkins() {
        return skins.get();
    }

    public SearchableList<ChampionSpell> getSpells() {
        return spells.get();
    }

    public ChampionStats getStats() {
        return stats.get();
    }

    public List<String> getTags() {
        return tags.get();
    }

    @Searchable(String.class)
    public String getTitle() {
        if(coreData.getTitle() == null) {
            load(CHAMPION_LOAD_GROUP);
        }
        return coreData.getTitle();
    }

    public String getVersion() {
        return coreData.getVersion();
    }

    @Override
    protected void loadCoreData(final String group) {
        final ImmutableMap.Builder<String, Object> builder;
      if (group.equals(CHAMPION_LOAD_GROUP)) {
        builder = ImmutableMap.builder();
        if (coreData.getId() != 0) {
          builder.put("id", coreData.getId());
        }
        if (coreData.getName() != null) {
          builder.put("name", coreData.getName());
        }
        if (coreData.getKey() != null) {
          builder.put("key", coreData.getKey());
        }
        if (coreData.getPlatform() != null) {
          builder.put("platform", Platform.withTag(coreData.getPlatform()));
        }
        if (coreData.getVersion() != null) {
          builder.put("version", coreData.getVersion());
        }
        if (coreData.getLocale() != null) {
          builder.put("locale", coreData.getLocale());
        }
        if (coreData.getIncludedData() != null) {
          builder.put("includedData", coreData.getIncludedData());
        }
        final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Champion data =
            Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.staticdata.Champion.class, builder.build());
        if (data != null) {
          coreData = data;
        }
      }
    }
}
