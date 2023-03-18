package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.GameMode;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableList;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.SearchableLists;

public class SummonerSpell extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.SummonerSpell> {
  public static final class Builder {
    private Integer id;
    private Set<String> includedData;
    private String name, version, locale;
    private Platform platform;

    private Builder(final int id) {
      this.id = id;
    }

    private Builder(final String name) {
      this.name = name;
    }

    public SummonerSpell get() {
      if (name == null && id == null) {
        throw new IllegalStateException("Must set an ID or name for the SummonerSpell!");
      }

      if (platform == null) {
        platform = Orianna.getSettings().getDefaultPlatform();
        if (platform == null) {
          throw new IllegalStateException(
              "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
        }
      }

      if (version == null) {
        version = Orianna.getSettings().getCurrentVersion(platform);
      }

      if (locale == null) {
        locale = Orianna.getSettings().getDefaultLocale();
        locale = locale == null ? platform.getDefaultLocale() : locale;
      }

      if (includedData == null) {
        includedData = ImmutableSet.of("all");
      }

      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder().put("platform", platform).put("version", version)
          .put("locale", locale).put("includedData", includedData);

      if (id != null) {
        builder.put("id", id);
      } else {
        builder.put("name", name);
      }

      return Orianna.getSettings().getPipeline().get(SummonerSpell.class, builder.build());
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

  @Serial
  private static final long serialVersionUID = -4080746989103816773L;
  public static final String SUMMONER_SPELL_LOAD_GROUP = "summoner-spell";

  public static Builder named(final String name) {
    return new Builder(name);
  }

  public static Builder withId(final int id) {
    return new Builder(id);
  }

  private final Supplier<List<Double>> cooldowns = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getCooldowns() == null ? null : Collections.unmodifiableList(coreData.getCooldowns());
  })::get;

  private final Supplier<List<Integer>> costs = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getCosts() == null ? null : Collections.unmodifiableList(coreData.getCosts());
  })::get;

  private final Supplier<List<List<Double>>> effects = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getEffects() == null ? null : coreData.getEffects().stream().map(effect -> effect != null ? Collections.unmodifiableList(effect) : null).toList();
  })::get;

  private final Supplier<Image> image = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getImage() == null ? null : new Image(coreData.getImage());
  })::get;

  private final Supplier<Set<String>> includedData = Suppliers.memoize(() ->
      coreData.getIncludedData() == null ? null : Collections.unmodifiableSet(coreData.getIncludedData()))::get;

  private final Supplier<List<String>> levelUpEffects = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getLevelUpEffects() == null ? null : Collections.unmodifiableList(coreData.getLevelUpEffects());
  })::get;

  private final Supplier<List<String>> levelUpKeywords = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getLevelUpKeywords() == null ? null : Collections.unmodifiableList(coreData.getLevelUpKeywords());
  })::get;

  private final Supplier<Set<GameMode>> modes = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getModes() == null ? null : coreData.getModes().stream().map(GameMode::valueOf).collect(Collectors.toUnmodifiableSet());
  })::get;

  private final Supplier<List<Integer>> ranges = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getRanges() == null ? null : Collections.unmodifiableList(coreData.getRanges());
  })::get;

  private final Supplier<SearchableList<SpellVariables>> variables = Suppliers.memoize(() -> {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getVariables() == null ? null : SearchableLists.unmodifiableFrom(coreData.getVariables().stream().map(SpellVariables::new).collect(Collectors.toCollection(() -> new ArrayList<>(coreData.getVariables().size()))));
  })::get;

  public SummonerSpell(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.SummonerSpell coreData) {
    super(coreData, 1);
  }

  @Override
  public boolean exists() {
    if (coreData.getDescription() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getDescription() != null;
  }

  public List<Double> getCooldowns() {
    return cooldowns.get();
  }

  public List<Integer> getCosts() {
    return costs.get();
  }

  public String getDescription() {
    load(SUMMONER_SPELL_LOAD_GROUP);
    return coreData.getDescription();
  }

  public List<List<Double>> getEffects() {
    return effects.get();
  }

  @Searchable(int.class)
  public int getId() {
    if (coreData.getId() == 0) {
      load(SUMMONER_SPELL_LOAD_GROUP);
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
    if (coreData.getKey() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getKey();
  }

  public List<String> getLevelUpEffects() {
    return levelUpEffects.get();
  }

  public List<String> getLevelUpKeywords() {
    return levelUpKeywords.get();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(SUMMONER_SPELL_LOAD_GROUP);
  }

  public String getLocale() {
    return coreData.getLocale();
  }

  public int getMaxRank() {
    if (coreData.getMaxRank() == 0) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getMaxRank();
  }

  public Set<GameMode> getModes() {
    return modes.get();
  }

  @Searchable(String.class)
  public String getName() {
    if (coreData.getName() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getName();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public List<Integer> getRanges() {
    return ranges.get();
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  public String getResource() {
    if (coreData.getResource() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getResource();
  }

  public String getResourceDescription() {
    if (coreData.getResourceDescription() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getResourceDescription();
  }

  public String getSanitizedDescription() {
    if (coreData.getSanitizedDescription() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getSanitizedDescription();
  }

  public String getSanitizedTooltip() {
    if (coreData.getSanitizedTooltip() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getSanitizedTooltip();
  }

  public int getSummonerLevelRequirement() {
    if (coreData.getSummonerLevelRequirement() == 0) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getSummonerLevelRequirement();
  }

  public String getTooltip() {
    if (coreData.getTooltip() == null) {
      load(SUMMONER_SPELL_LOAD_GROUP);
    }
    return coreData.getTooltip();
  }

  public SearchableList<SpellVariables> getVariables() {
    return variables.get();
  }

  public String getVersion() {
    return coreData.getVersion();
  }

  @Override
  protected void loadCoreData(final String group) {
    final ImmutableMap.Builder<String, Object> builder;
    if (group.equals(SUMMONER_SPELL_LOAD_GROUP)) {
      builder = ImmutableMap.builder();
      if (coreData.getId() != 0) {
        builder.put("id", coreData.getId());
      }
      if (coreData.getName() != null) {
        builder.put("name", coreData.getName());
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
      final de.zahrie.trues.api.riot.xayah.types.data.staticdata.SummonerSpell data =
          Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.staticdata.SummonerSpell.class, builder.build());
      if (data != null) {
        coreData = data;
      }
    }
  }
}
