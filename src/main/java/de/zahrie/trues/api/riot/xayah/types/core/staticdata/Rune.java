package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.common.RuneType;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;

public class Rune extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Rune> {
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

    public Rune get() {
      if (name == null && id == null) {
        throw new IllegalStateException("Must set an ID or name for the Rune!");
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

      return Orianna.getSettings().getPipeline().get(Rune.class, builder.build());
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

  public static final String RUNE_LOAD_GROUP = "rune";

  @Serial
  private static final long serialVersionUID = 2420355663284372069L;

  public static Builder named(final String name) {
    return new Builder(name);
  }

  public static Builder withId(final int id) {
    return new Builder(id);
  }

  private final Supplier<Image> image = Suppliers.memoize(() -> {
    load(RUNE_LOAD_GROUP);
    return coreData.getImage() == null ? null : new Image(coreData.getImage());
  })::get;

  private final Supplier<Set<String>> includedData = Suppliers.memoize(() ->
      coreData.getIncludedData() == null ? null : Collections.unmodifiableSet(coreData.getIncludedData()))::get;

  private final Supplier<RuneStats> stats = Suppliers.memoize(() -> {
    load(RUNE_LOAD_GROUP);
    return coreData.getStats() == null ? null : new RuneStats(coreData.getStats());
  })::get;

  private final Supplier<List<String>> tags = Suppliers.memoize(() -> {
    load(RUNE_LOAD_GROUP);
    return coreData.getTags() == null ? null : Collections.unmodifiableList(coreData.getTags());
  })::get;

  public Rune(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Rune coreData) {
    super(coreData, 1);
  }

  @Override
  public boolean exists() {
    if (coreData.getDescription() == null) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getDescription() != null;
  }

  public String getDescription() {
    if (coreData.getDescription() == null) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getDescription();
  }

  @Searchable(int.class)
  public int getId() {
    if (coreData.getId() == 0) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getId();
  }

  public Image getImage() {
    return image.get();
  }

  public Set<String> getIncludedData() {
    return includedData.get();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(RUNE_LOAD_GROUP);
  }

  public String getLocale() {
    return coreData.getLocale();
  }

  @Searchable(String.class)
  public String getName() {
    if (coreData.getName() == null) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getName();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  public String getSanitizedDescription() {
    if (coreData.getSanitizedDescription() == null) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getSanitizedDescription();
  }

  public RuneStats getStats() {
    return stats.get();
  }

  public List<String> getTags() {
    return tags.get();
  }

  public int getTier() {
    if (coreData.getTier() == 0) {
      load(RUNE_LOAD_GROUP);
    }
    return coreData.getTier();
  }

  public RuneType getType() {
    if (coreData.getType() == null) {
      load(RUNE_LOAD_GROUP);
    }
    return RuneType.withColor(coreData.getType());
  }

  public String getVersion() {
    return coreData.getVersion();
  }

  @Override
  protected void loadCoreData(final String group) {
    final ImmutableMap.Builder<String, Object> builder;
    if (group.equals(RUNE_LOAD_GROUP)) {
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
      final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Rune data =
          Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.staticdata.Rune.class, builder.build());
      if (data != null) {
        coreData = data;
      }
    }
  }
}
