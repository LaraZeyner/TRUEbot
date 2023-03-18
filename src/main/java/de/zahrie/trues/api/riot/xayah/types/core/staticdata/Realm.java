package de.zahrie.trues.api.riot.xayah.types.core.staticdata;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;

public class Realm extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.staticdata.Realm> {
  public static final class Builder {
    private Platform platform;

    private Builder() {
    }

    public Realm get() {
      if (platform == null) {
        platform = Orianna.getSettings().getDefaultPlatform();
        if (platform == null) {
          throw new IllegalStateException(
              "No platform/region was set! Must either set a default platform/region with Orianna.setDefaultPlatform or Orianna.setDefaultRegion, or include a platform/region with the request!");
        }
      }

      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.<String, Object>builder().put("platform", platform);
      return Orianna.getSettings().getPipeline().get(Realm.class, builder.build());
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

  public static final String REALM_LOAD_GROUP = "realm";
  @Serial
  private static final long serialVersionUID = 232469766173551775L;

  public static Realm get() {
    return new Builder().get();
  }

  public static Builder withPlatform(final Platform platform) {
    return new Builder().withPlatform(platform);
  }

  public static Builder withRegion(final Region region) {
    return new Builder().withRegion(region);
  }

  private final Supplier<Map<String, String>> latestVersions = Suppliers.memoize(() -> {
    load(REALM_LOAD_GROUP);
    return coreData.getLatestVersions() == null ? null : Collections.unmodifiableMap(coreData.getLatestVersions());
  })::get;

  public Realm(final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Realm coreData) {
    super(coreData, 1);
  }

  @Override
  public boolean exists() {
    if (coreData.getCdn() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getCdn() != null;
  }

  public String getCDN() {
    if (coreData.getCdn() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getCdn();
  }

  public String getCSSVersion() {
    if (coreData.getCSSVersion() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getCSSVersion();
  }

  public String getDefaultLocale() {
    if (coreData.getDefaultLocale() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getDefaultLocale();
  }

  public String getLatestDataDragon() {
    if (coreData.getLatestDataDragon() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getLatestDataDragon();
  }

  public Map<String, String> getLatestVersions() {
    return latestVersions.get();
  }

  public String getLegacyMode() {
    if (coreData.getLegacyMode() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getLegacyMode();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(REALM_LOAD_GROUP);
  }

  public int getMaxProfileIconId() {
    if (coreData.getMaxProfileIconId() == 0) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getMaxProfileIconId();
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  public String getStore() {
    if (coreData.getStore() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getStore();
  }

  public String getVersion() {
    if (coreData.getVersion() == null) {
      load(REALM_LOAD_GROUP);
    }
    return coreData.getVersion();
  }

  @Override
  protected void loadCoreData(final String group) {
    final ImmutableMap.Builder<String, Object> builder;
    if (group.equals(REALM_LOAD_GROUP)) {
      builder = ImmutableMap.builder();
      if (coreData.getPlatform() != null) {
        builder.put("platform", Platform.withTag(coreData.getPlatform()));
      }
      final de.zahrie.trues.api.riot.xayah.types.data.staticdata.Realm data =
          Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.staticdata.Realm.class, builder.build());
      if (data != null) {
        coreData = data;
      }
    }
  }
}
