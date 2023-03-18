package de.zahrie.trues.api.riot.xayah.types.core.championmastery;

import java.io.Serial;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import de.zahrie.trues.api.riot.xayah.Orianna;
import de.zahrie.trues.api.riot.xayah.types.common.Platform;
import de.zahrie.trues.api.riot.xayah.types.common.Region;
import de.zahrie.trues.api.riot.xayah.types.core.GhostObject;
import de.zahrie.trues.api.riot.xayah.types.core.searchable.Searchable;
import de.zahrie.trues.api.riot.xayah.types.core.staticdata.RiotChampion;
import de.zahrie.trues.api.riot.xayah.types.core.summoner.Summoner;
import org.joda.time.DateTime;

public class ChampionMastery extends GhostObject<de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMastery> implements Comparable<ChampionMastery> {
  public static final class Builder {
    public final class SubBuilder {
      private final RiotChampion riotChampion;

      private SubBuilder(final RiotChampion riotChampion) {
        this.riotChampion = riotChampion;
      }

      public ChampionMastery get() {
        final ImmutableMap.Builder<String, Object> builder =
            ImmutableMap.<String, Object>builder().put("platform", summoner.getPlatform()).put("summonerId", summoner.getId()).put("championId",
                riotChampion.getId());

        return Orianna.getSettings().getPipeline().get(ChampionMastery.class, builder.build());
      }
    }

    private final Summoner summoner;

    private Builder(final Summoner summoner) {
      this.summoner = summoner;
    }

    public SubBuilder withChampion(final RiotChampion riotChampion) {
      return new SubBuilder(riotChampion);
    }
  }

  public static final String CHAMPION_MASTERY_LOAD_GROUP = "champion-mastery";
  @Serial
  private static final long serialVersionUID = -4377419492958529379L;

  public static Builder forSummoner(final Summoner summoner) {
    return new Builder(summoner);
  }

  private final Supplier<RiotChampion> champion = Suppliers.memoize(() ->
      coreData.getChampionId() == 0 ? null : RiotChampion.withId(coreData.getChampionId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

  private final Supplier<Summoner> summoner = Suppliers.memoize(() ->
      coreData.getSummonerId() == null ? null : Summoner.withId(coreData.getSummonerId()).withPlatform(Platform.withTag(coreData.getPlatform())).get())::get;

  public ChampionMastery(final de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMastery coreData) {
    super(coreData, 1);
  }

  @Override
  public int compareTo(final ChampionMastery o) {
    final int result = Integer.compare(getLevel(), o.getLevel());
    if (result != 0) {
      return result;
    }
    return Integer.compare(getPoints(), o.getPoints());
  }

  @Override
  public boolean exists() {
    if (coreData.getPoints() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getPoints() != 0;
  }

  @Searchable({RiotChampion.class, String.class, int.class})
  public RiotChampion getChampion() {
    return champion.get();
  }

  public DateTime getLastPlayed() {
    if (coreData.getLastPlayed() == null) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getLastPlayed();
  }

  public int getLevel() {
    if (coreData.getLevel() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getLevel();
  }

  @Override
  protected List<String> getLoadGroups() {
    return List.of(CHAMPION_MASTERY_LOAD_GROUP);
  }

  public Platform getPlatform() {
    return Platform.withTag(coreData.getPlatform());
  }

  public int getPoints() {
    if (coreData.getPoints() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getPoints();
  }

  public int getPointsSinceLastLevel() {
    if (coreData.getPointsSinceLastLevel() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getPointsSinceLastLevel();
  }

  public int getPointsUntilNextLevel() {
    if (coreData.getPointsUntilNextLevel() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getPointsUntilNextLevel();
  }

  public Region getRegion() {
    return Platform.withTag(coreData.getPlatform()).getRegion();
  }

  @Searchable({Summoner.class, String.class, long.class})
  public Summoner getSummoner() {
    return summoner.get();
  }

  public int getTokens() {
    if (coreData.getTokens() == 0) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.getTokens();
  }

  public boolean isChestGranted() {
    if (coreData.isChestGranted()) {
      load(CHAMPION_MASTERY_LOAD_GROUP);
    }
    return coreData.isChestGranted();
  }

  @Override
  protected void loadCoreData(final String group) {
    final ImmutableMap.Builder<String, Object> builder;
    if (group.equals(CHAMPION_MASTERY_LOAD_GROUP)) {
      builder = ImmutableMap.builder();
      if (coreData.getSummonerId() != null) {
        builder.put("summonerId", coreData.getSummonerId());
      }
      if (coreData.getChampionId() != 0) {
        builder.put("championId", coreData.getChampionId());
      }
      if (coreData.getPlatform() != null) {
        builder.put("platform", Platform.withTag(coreData.getPlatform()));
      }
      final de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMastery data =
          Orianna.getSettings().getPipeline().get(de.zahrie.trues.api.riot.xayah.types.data.championmastery.ChampionMastery.class, builder.build());
      if (data != null) {
        coreData = data;
      }
    }
  }
}
